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

package uk.gov.hmrc.sdecstridefrontend.views.createthread

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import uk.gov.hmrc.sdecstridefrontend.controllers.createthread.routes
import uk.gov.hmrc.sdecstridefrontend.fixtures.ThreadFixtures
import uk.gov.hmrc.sdecstridefrontend.viewmodels.checkyouranswers.{RecipientDetailsSummary, ThreadDetailsSummary}
import uk.gov.hmrc.sdecstridefrontend.viewmodels.govuk.all.SummaryListViewModel
import uk.gov.hmrc.sdecstridefrontend.views.html.createthread.CheckYourAnswersView

import scala.jdk.CollectionConverters.*

class CheckYourAnswersViewSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  private val view = app.injector.instanceOf[CheckYourAnswersView]

  private val request = FakeRequest("GET", "/sdec-stride-frontend/create-thread/check-your-answers")

  private given messages: Messages = app.injector.instanceOf[MessagesApi].preferred(request)

  private lazy val page: Document =
    Jsoup.parse(
      view(
        SummaryListViewModel(RecipientDetailsSummary.rows(ThreadFixtures.recipientWithoutCase)),
        SummaryListViewModel(ThreadDetailsSummary.rows(ThreadFixtures.threadType, ThreadFixtures.threadDetails))
      )(using request, messages).body
    )

  private val pageTitle = "Check your answers"

  private lazy val summaryLists: Elements = page.select("#main-content dl.govuk-summary-list")

  "CheckYourAnswersView" should {

    "have the Check your answers page title" in {
      page.title() mustBe pageTitle
    }

    "have a single H1 heading" in {
      val h1 = page.getElementsByTag("h1")
      h1.size() mustBe 1
      h1.text() mustBe pageTitle
    }

    "show the Create new thread caption" in {
      page.select(".govuk-caption-l").text() mustBe "Create new thread"
    }

    "show the hint text" in {
      val hint = page.select("#main-content p.govuk-body").first()

      hint.text() mustBe "Use this page to make any changes before sending your request"
    }

    "show the Recipient details and Thread details headings" in {
      val headings = page.select("#main-content h2.govuk-heading-m").eachText().asScala.toSeq

      headings mustBe Seq("Recipient details", "Thread details")
    }

    "show two summary lists" in {
      summaryLists.size() mustBe 2
    }

    "list the recipient rows in the first summary list" in {
      val recipientKeys = summaryLists.get(0).select(".govuk-summary-list__key").eachText().asScala.toSeq

      recipientKeys mustBe Seq(
        "Their name",
        "Email address",
        "Mobile number",
        "National Insurance number",
        "Related case"
      )
    }

    "list the thread rows in the second summary list" in {
      val threadKeys = summaryLists.get(1).select(".govuk-summary-list__key").eachText().asScala.toSeq

      threadKeys mustBe Seq(
        "What you want to do",
        "Message",
        "Requested item(s)",
        "Response deadline"
      )
    }

    "show a Change link on every row" in {
      val links = page.select(".govuk-summary-list__actions a").eachText().asScala.toSeq

      links.size mustBe 9
      all(links) must startWith("Change")
    }

    "not show a warning" in {
      page.select(".govuk-warning-text").size() mustBe 0
    }

    "post the form to the submit route" in {
      val formElement = page.select("#main-content form")

      formElement.attr("method") mustBe "POST"
      formElement.attr("action") mustBe routes.CheckYourAnswersController.onSubmit().url
    }

    "display the Confirm and send button" in {
      val button = page.select(".govuk-button")

      button.size() mustBe 1
      button.first().text() mustBe "Confirm and send"
      button.first().attr("type") mustBe "submit"
    }
  }

}
