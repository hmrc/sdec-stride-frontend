/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.sdecstridefrontend.security.action

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import play.api.Environment
import play.api.inject.bind
import play.api.mvc.*
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import play.mvc.Http.Status
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Name}
import uk.gov.hmrc.auth.core.{AuthConnector, Enrolments}
import uk.gov.hmrc.sdecstridefrontend.config.LoginConfig
import uk.gov.hmrc.sdecstridefrontend.infrastructure.{ApplicationConfigBuilder, TestAuthConnector}
import uk.gov.hmrc.sdecstridefrontend.models.{StaffAuth, TestStaff}
import uk.gov.hmrc.sdecstridefrontend.security.service.EnrolmentServiceAlgebra

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StrideAuthSpec extends AnyFunSuite with Matchers with ScalaFutures with ApplicationConfigBuilder {

  val enrolmentService = new EnrolmentServiceAlgebra {
    override def extractStaff(
      enrolments:  Enrolments,
      email:       Option[String],
      credentials: Option[Credentials],
      name:        Option[Name]
    ): Option[StaffAuth] =
      credentials.get.providerId match {
        case "1000" => Some(TestStaff.standardStaff)
        case "1001" => Some(TestStaff.insufficientRoleStaff)
        case _      => None
      }

    override def verifyEnrollment(staffAuth: StaffAuth): Boolean =
      staffAuth.enrolments.enrolments.exists(_.key.startsWith("sdec_"))
  }
  val conf = Map[String, Any](
    "host"               -> "",
    "appName"            -> "sdec-stride-frontend",
    "urls.login"         -> "/authorise",
    "urls.loginContinue" -> "/sdec-stride-frontend",
    "urls.signOut"       -> "/sign-out"
  )
  val loginConfig = LoginConfig(configuration(conf))

  test("should execute the action successfully") {
    val authConnector = new TestAuthConnector(Future.successful(TestStaff.staffRetrievalResult))
    val app           = application()
      .overrides(
        bind[LoginConfig].toInstance(loginConfig)
      )
      .build()
    val auth   = strideAuth(authConnector, enrolmentService, app)
    val action = (user: StaffAuth, _: Request[AnyContent]) => Future.successful(Results.Ok(user.email.value))

    val request = FakeRequest("GET", "/test")

    val result: Future[Result] = auth.authorisedFromStride(action).apply(request)
    whenReady(result) { r =>
      r.header.status shouldBe Status.OK
    }
  }

  test("should redirect with insufficient role") {
    val authConnector = new TestAuthConnector(Future.successful(TestStaff.insufficientEnrolmentsRetrievalResult))
    val app           = application()
      .overrides(
        bind[LoginConfig].toInstance(loginConfig)
      )
      .build()
    val auth   = strideAuth(authConnector, enrolmentService, app)
    val action = (user: StaffAuth, _: Request[AnyContent]) => Future.successful(Results.Ok(user.email.value))

    val request = FakeRequest("GET", "/test")

    val result: Future[Result] = auth.authorisedFromStride(action).apply(request)
    whenReady(result) { r =>
      r.header.status          shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some("/sdec-stride-frontend/insufficient-roles")
    }
  }

  private def strideAuth(
    authConnector:    AuthConnector,
    enrolmentService: EnrolmentServiceAlgebra,
    application:      play.api.Application
  ): StrideAuth = {

    val actionBuilder =
      application.injector.instanceOf[DefaultActionBuilder]

    new StrideAuth(
      authConnector = authConnector,
      env = application.injector.instanceOf[Environment],
      config = application.injector.instanceOf[LoginConfig],
      actionBuilder = actionBuilder,
      enrolmentService = enrolmentService
    )
  }

}
