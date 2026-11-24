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
import uk.gov.hmrc.sdecstridefrontend.forms.RecipientDetailsFormProvider
import uk.gov.hmrc.sdecstridefrontend.models.RecipientDetails
import uk.gov.hmrc.sdecstridefrontend.views.html.createthread.RecipientDetailsView

import scala.jdk.CollectionConverters.*

class RecipientDetailsViewSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  private val view         = app.injector.instanceOf[RecipientDetailsView]
  private val request      = FakeRequest("GET", "/sdec-stride-frontend/create-thread/recipient-details")
  private val messages     = app.injector.instanceOf[MessagesApi].preferred(request)
  private val formProvider = app.injector.instanceOf[RecipientDetailsFormProvider]

  private def form: Form[RecipientDetails] = formProvider()

  private lazy val page: Document =
    Jsoup.parse(view(form)(using request, messages).body)

  private val textInputs = Seq(
    ("firstName", "First name"),
    ("lastName", "Last name"),
    ("email", "Email address"),
    ("phoneNumber", "Mobile number"),
    ("nationalInsuranceNumber", "National Insurance number")
  )

  val pageTitle   = "Recipient details"
  val serviceName = "sdec-stride-frontend"

  "RecipientDetailsView" should {

    "have the Recipient details page title" in {
      page.title() shouldBe s"$pageTitle - $serviceName - GOV.UK"
    }

    "show a Recipient details h1" in {
      val h1Elements: Elements = page.getElementsByTag("h1")
      h1Elements.size() shouldBe 1
      h1Elements.text() shouldBe pageTitle
    }

    "post the form to the submit route" in {
      val form = page.select("#main-content form")
      form.attr("method") shouldBe "POST"
      form.attr("action") shouldBe routes.RecipientDetailsController.onSubmit().url
    }

    textInputs.foreach { case (name, labelText) =>
      s"show a $labelText text input" in {
        val input = page.select(s"#main-content input#$name")

        input.size()       shouldBe 1
        input.attr("name") shouldBe name
        input.attr("type") shouldBe "text"

        page.select(s"#main-content label[for=$name]").text() shouldBe labelText
      }
    }

    "show Yes/No radios for an existing case" in {
      val radios = page.select("#main-content .govuk-radios")
      radios.select(".govuk-radios__label").eachText().asScala.toSeq shouldBe Seq("Yes", "No")
    }

    "show a hint for the existing case radios" in {
      page.select("#main-content #hasRelatedCase-hint").text() shouldBe
        "Select 'Yes' if this communication is linked to an existing case"
    }

    "show a case reference input inside the Yes conditional" in {
      val conditional = page.select("#main-content .govuk-radios__conditional")
      conditional.select("input#caseReferenceNumber").size()      shouldBe 1
      conditional.select("label[for=caseReferenceNumber]").text() shouldBe "Enter a case reference number"
    }

    "show a hint for the case reference input" in {
      page.select("#main-content #caseReferenceNumber-hint").text() shouldBe
        "Add a reference number linked to the case. This could be a National Insurance number, a customer ID number or the UTR reference"
    }

    "show a Continue button" in {
      val button = page.select("#main-content .govuk-button")
      button.size()               shouldBe 1
      button.first().text()       shouldBe "Continue"
      button.first().attr("type") shouldBe "submit"
    }

  }
}
