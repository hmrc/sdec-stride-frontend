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
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import uk.gov.hmrc.sdecstridefrontend.controllers.createthread.routes
import uk.gov.hmrc.sdecstridefrontend.forms.validators.ThreadDetailsFormProvider
import uk.gov.hmrc.sdecstridefrontend.models.{RecipientDetails, ThreadDetails}
import uk.gov.hmrc.sdecstridefrontend.views.html.createthread.ThreadDetailsView

class ThreadDetailsViewSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  private val view = app.injector.instanceOf[ThreadDetailsView]

  private val request = FakeRequest("GET", "/sdec-stride-frontend/create-thread/thread-details")

  private val messages = app.injector.instanceOf[MessagesApi].preferred(request)

  private val formProvider = app.injector.instanceOf[ThreadDetailsFormProvider]

  private def form: Form[ThreadDetails] = formProvider()

  private val recipient =
    RecipientDetails(
      firstName = "Test",
      lastName = "User",
      email = "test.user@example.com",
      phoneNumber = "07000000000",
      nationalInsuranceNumber = "AA123456A",
      hasRelatedCase = false,
      caseReferenceNumber = None
    )

  private lazy val page: Document =
    Jsoup.parse(
      view(form, recipient)(using request, messages).body
    )

  private val pageTitle = "Thread details"

  "ThreadDetailsView" should {

    "have the Thread details page title" in {
      page.title() shouldBe pageTitle
    }

    "have a unique H1 heading" in {
      val h1Elements: Elements = page.getElementsByTag("h1")
      h1Elements.size() shouldBe 1
      h1Elements.text() shouldBe pageTitle
    }

    "post the form to the submit route" in {
      val formElement =
        page.select("#main-content form")

      formElement.attr("method") shouldBe "POST"

      formElement.attr("action") shouldBe
        routes.ThreadDetailsController.onSubmit().url
    }

    "display the recipient details in the recipient banner" in {

      val paragraphs =
        page.select(".sdec-recipient-banner__details p.govuk-body")

      paragraphs.size() shouldBe 3

      paragraphs.get(0).text() shouldBe "Test User"
      paragraphs.get(1).text() shouldBe "test.user@example.com"
      paragraphs.get(2).text() shouldBe "07000000000"
    }

    "display the message field" in {
      page.select("#message").size() shouldBe 1
    }

    "display the response date fields" in {
      page.select("input[name=responseDate.day]").size()   shouldBe 1
      page.select("input[name=responseDate.month]").size() shouldBe 1
      page.select("input[name=responseDate.year]").size()  shouldBe 1
    }

    "display the Continue button" in {
      val button =
        page.select(".govuk-button")

      button.size()               shouldBe 1
      button.first().text()       shouldBe "Continue"
      button.first().attr("type") shouldBe "submit"
    }
  }
}
