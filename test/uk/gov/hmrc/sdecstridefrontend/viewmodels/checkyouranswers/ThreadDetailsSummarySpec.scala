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

package uk.gov.hmrc.sdecstridefrontend.viewmodels.checkyouranswers

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.sdecstridefrontend.controllers.routes as appRoutes
import uk.gov.hmrc.sdecstridefrontend.fixtures.ThreadFixtures
import uk.gov.hmrc.sdecstridefrontend.models.{ThreadDetails, ThreadTypeOptions}

class ThreadDetailsSummarySpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  private given messages: Messages =
    app.injector.instanceOf[MessagesApi].preferred(FakeRequest())

  private def rowsFor(
    threadType:    ThreadTypeOptions = ThreadFixtures.threadType,
    threadDetails: ThreadDetails = ThreadFixtures.threadDetails
  ): Seq[SummaryListRow] =
    ThreadDetailsSummary.rows(threadType, threadDetails)

  "ThreadDetailsSummary.rows" when {

    "given the standard thread details" must {
      val rows = rowsFor()

      "show the four rows in order" in {
        rows.map(_.key.content) mustBe Seq(
          Text("What you want to do"),
          Text("Message"),
          Text("Requested item(s)"),
          Text("Response deadline")
        )
      }

      "show the message" in {
        rows(1).value.content.asHtml.body mustBe
          """<span class="sdec-preserve-line-breaks">Please send your latest payslips</span>"""
      }

      "show a dummy requested item until real items are captured" in {
        rows(2).value.content mustBe Text("Item 1")
      }

      "show the response deadline as a long date" in {
        rows(3).value.content mustBe Text("28 December 2029")
      }

      "link every Change action to the development in progress page" in {
        rows.flatMap(_.actions).flatMap(_.items).map(_.href).distinct mustBe
          Seq(appRoutes.DevelopmentInProgressController.onPageLoad().url)
      }

      "give every Change action visually hidden text naming its row" in {
        rows.flatMap(_.actions).flatMap(_.items).flatMap(_.visuallyHiddenText) mustBe Seq(
          "What you want to do",
          "Message",
          "Requested item(s)",
          "Response deadline"
        )
      }
    }

    "given each thread type" must
      Seq(
        ThreadTypeOptions.Send           -> "Send file(s)",
        ThreadTypeOptions.Request        -> "Request file(s)",
        ThreadTypeOptions.SendAndRequest -> "Send and request file(s)"
      ).foreach { (threadType, expectedLabel) =>
        s"show $expectedLabel for $threadType" in {
          rowsFor(threadType = threadType).head.value.content mustBe Text(expectedLabel)
        }
      }

    "given a message with line breaks" must {
      "keep the line breaks" in {
        val details = ThreadFixtures.threadDetails.copy(message = "Line one\n\nLine two")

        rowsFor(threadDetails = details)(1).value.content.asHtml.body must include("Line one\n\nLine two")
      }
    }

    "given a message containing markup" must {
      "escape it" in {
        val details = ThreadFixtures.threadDetails.copy(message = "<script>alert(1)</script>")

        rowsFor(threadDetails = details)(1).value.content.asHtml.body mustBe
          """<span class="sdec-preserve-line-breaks">&lt;script&gt;alert(1)&lt;/script&gt;</span>"""
      }
    }
  }

}
