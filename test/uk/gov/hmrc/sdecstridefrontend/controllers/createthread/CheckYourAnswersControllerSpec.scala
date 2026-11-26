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

package uk.gov.hmrc.sdecstridefrontend.controllers.createthread

import org.jsoup.Jsoup
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.Status
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.sdecstridefrontend.controllers.routes as appRoutes
import uk.gov.hmrc.sdecstridefrontend.fixtures.ThreadFixtures
import uk.gov.hmrc.sdecstridefrontend.infrastructure.TestStrideAuth
import uk.gov.hmrc.sdecstridefrontend.models.{StaffAuth, TestStaff}
import uk.gov.hmrc.sdecstridefrontend.security.action.StrideAuthAlgebra
import uk.gov.hmrc.sdecstridefrontend.session.CreateThreadSession

import scala.jdk.CollectionConverters.*

class CheckYourAnswersControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .overrides(
        bind[StrideAuthAlgebra].to[TestStrideAuth],
        bind[StaffAuth].toInstance(TestStaff.standardStaff)
      )
      .build()

  private val controller = app.injector.instanceOf[CheckYourAnswersController]

  private val workspaceUrl = appRoutes.WorkspaceController.onPageLoad.url

  private def getRequest(entries: Seq[(String, String)]) =
    FakeRequest("GET", "/create-thread/check-your-answers").withSession(entries*)

  private def summaryKeys(html: String): Seq[String] =
    Jsoup.parse(html).select(".govuk-summary-list__key").eachText().asScala.toSeq

  private def summaryValues(html: String): Seq[String] =
    Jsoup.parse(html).select(".govuk-summary-list__value").eachText().asScala.toSeq

  "GET /create-thread/check-your-answers" when {
    "all the answers are in the session" must {
      val result = controller.onPageLoad(getRequest(ThreadFixtures.sessionEntries()))

      "return 200 HTML" in {
        status(result) mustBe Status.OK
        contentType(result) mustBe Some("text/html")
        charset(result) mustBe Some("utf-8")
      }

      "show the recipient and thread answers" in {
        val values = summaryValues(contentAsString(result))

        values must contain("Jane Doe")
        values must contain("Please send your latest payslips")
        values must contain("28 December 2029")
      }
    }

    "the user gave a case reference" must {
      "show Related case Yes and the reference number" in {
        val html = contentAsString(
          controller.onPageLoad(getRequest(ThreadFixtures.sessionEntries(ThreadFixtures.recipientWithCase)))
        )

        summaryKeys(html)   must contain allOf ("Related case", "Related reference number")
        summaryValues(html) must contain("CS1234567TF890")
      }
    }

    "the user did not give a case reference" must {
      "show Related case No and no reference number" in {
        val html = contentAsString(
          controller.onPageLoad(getRequest(ThreadFixtures.sessionEntries(ThreadFixtures.recipientWithoutCase)))
        )

        summaryKeys(html) must contain("Related case")
        summaryKeys(html) must not contain "Related reference number"
      }
    }

    "an answer is missing" must {
      "redirect to Workspace when the session is empty" in {
        val result = controller.onPageLoad(getRequest(Seq.empty))

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(workspaceUrl)
      }

      "redirect to Workspace when only some answers are in the session" in {
        val recipientOnly = CreateThreadSession.recipientEntries(ThreadFixtures.recipientWithCase)
        val result        = controller.onPageLoad(getRequest(recipientOnly))

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(workspaceUrl)
      }
    }
  }

  "POST /create-thread/check-your-answers" must {
    "redirect to the development in progress page until the countdown page exists" in {
      val result = controller.onSubmit(FakeRequest("POST", "/create-thread/check-your-answers"))

      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe Some(appRoutes.DevelopmentInProgressController.onPageLoad().url)
    }
  }

}
