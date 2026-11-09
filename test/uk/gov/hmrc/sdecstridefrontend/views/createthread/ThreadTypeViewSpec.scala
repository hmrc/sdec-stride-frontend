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
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.Form
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import uk.gov.hmrc.sdecstridefrontend.forms.ThreadTypeFormProvider
import uk.gov.hmrc.sdecstridefrontend.models.ThreadType
import uk.gov.hmrc.sdecstridefrontend.views.html.createthread.ThreadTypeView

import scala.jdk.CollectionConverters.*

class ThreadTypeViewSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  private val view         = app.injector.instanceOf[ThreadTypeView]
  private val request      = FakeRequest("GET", "/sdec-stride-frontend")
  private val messages     = app.injector.instanceOf[MessagesApi].preferred(request)
  private val formProvider = app.injector.instanceOf[ThreadTypeFormProvider]

  private def form: Form[ThreadType] =
    given Messages = messages
    formProvider()

  private def page(): Document =
    Jsoup.parse(view(form)(using request, messages).body)

  "ThreadTypeView" should:
    "have the Thread Type page title" in:
      page().title() shouldBe "Thread Type"

    "show a 'What do you want to do?' h1" in:
      val main = page().getElementById("main-content")
      main.getElementsByClass("govuk-heading-l").text() shouldBe "What do you want to do?"

    "show a Radio Selection for choosing the Thread Type" in:
      val main   = page().getElementById("main-content")
      val radios = main.getElementsByClass("govuk-radios")
      radios.size() shouldBe 1

      main.getElementsByClass("govuk-radios__label").eachText().asScala.toSeq shouldBe Seq(
        "Send file(s)",
        "Request file(s)",
        "Send and request file(s)"
      )

    "show a hint under each radio option" in:
      val main = page().getElementById("main-content")
      main.getElementsByClass("govuk-radios__hint").eachText().asScala.toSeq shouldBe Seq(
        "You must include at least one file and a message. The recipient cannot respond.",
        "You must include a message and the recipient must respond with their file(s) and a message.",
        "You must include a file and a message and the recipient must respond with their file(s) and a message."
      )

    "show a Continue button" in:
      val main   = page().getElementById("main-content")
      val button = main.getElementsByClass("govuk-button")
      button.size()         shouldBe 1
      button.first().text() shouldBe "Continue"
}
