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

import jakarta.inject.Inject
import play.api.mvc.*
import play.api.mvc.Results.*
import play.api.{Environment, Logging}
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.AuthProvider.PrivilegedApplication
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendHeaderCarrierProvider
import uk.gov.hmrc.sdecstridefrontend.config.LoginConfig
import uk.gov.hmrc.sdecstridefrontend.models.StaffAuth
import uk.gov.hmrc.sdecstridefrontend.security.service.EnrolmentServiceAlgebra

import scala.concurrent.{ExecutionContext, Future}

trait StrideAuthAlgebra {
  def authorisedFromStride(
    action: (StaffAuth, Request[AnyContent]) => Future[Result]
  )(implicit ec: ExecutionContext): Action[AnyContent]
}

class StrideAuth @Inject() (
  val authConnector: AuthConnector,
  val env:           Environment,
  val config:        LoginConfig,
  actionBuilder:     DefaultActionBuilder,
  enrolmentService:  EnrolmentServiceAlgebra
) extends StrideAuthAlgebra
    with AuthorisedFunctions
    with FrontendHeaderCarrierProvider
    with Logging {

  /*
   * For using Retrivals.name for SRIDE users, see
   * https://confluence.tools.tax.service.gov.uk/spaces/SDEC/pages/1381269850/Extracting+External+Internal+User+Name
   */
  override def authorisedFromStride(action: (StaffAuth, Request[AnyContent]) => Future[Result])(implicit
    ec: ExecutionContext
  ): Action[AnyContent] =
    actionBuilder.async { request =>
      given Request[AnyContent] = request
      authorised(AuthProviders(PrivilegedApplication))
        .retrieve(
          Retrievals.allEnrolments
            .and(Retrievals.email)
            .and(Retrievals.credentials)
            .and(Retrievals.name)
        ) { case allEnrolments ~ email ~ credentials ~ name =>
          val staff = enrolmentService.extractStaff(allEnrolments, email, credentials, name)
          staff.fold(
            handleInsufficientStaffInformation()
          )(s => handleStaffRequest(s, action))
        }
        .recoverWith {
          case e: NoActiveSession =>
            logger.warn(s"No active session: ${e.getMessage}")
            handleNoActiveSession()
          case e: InsufficientEnrolments =>
            logger.warn(s"Insufficient enrolments: ${e.getMessage}")
            handleInsufficientStaffInformation()
        }
    }

  private def handleStaffRequest(
    staff:  StaffAuth,
    action: (StaffAuth, Request[AnyContent]) => Future[Result]
  )(using request: Request[AnyContent]): Future[Result] =
    if enrolmentService.verifyEnrollment(staff) then action(staff, request)
    else handleInsufficientStaffInformation()

  private def handleInsufficientStaffInformation(): Future[Result] =
    Future.successful(SeeOther(uk.gov.hmrc.sdecstridefrontend.controllers.routes.InsufficientRolesController.get.url))

  private def handleNoActiveSession(): Future[Result] =
    Future.successful {
      Redirect(config.loginUrl, config.sdecRedirect)
    }
}
