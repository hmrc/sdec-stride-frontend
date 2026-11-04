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
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import uk.gov.hmrc.sdecstridefrontend.views.html.createthread.RecipientDetailsView

class RecipientDetailsViewSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite:

  private val view     = app.injector.instanceOf[RecipientDetailsView]
  private val request  = FakeRequest("GET", "/sdec-stride-frontend/create-thread/recipient-details")
  private val messages = app.injector.instanceOf[MessagesApi].preferred(request)

  private def page(): Document =
    Jsoup.parse(view()(using request, messages).body)

  "RecipientDetailsView" should:
    "have the Recipient Details page title" in:
      page().title() shouldBe "Recipient Details"

    "show a Recipient Details h1" in:
      val main = page().getElementById("main-content")
      main.getElementsByTag("h1").text() shouldBe "Recipient Details"

    "show a single Continue button leading to the thread type selection screen" in:
      val main    = page().getElementById("main-content")
      val buttons = main.getElementsByClass("govuk-button")
      buttons.size()               shouldBe 1
      buttons.first().text()       shouldBe "Continue"
      buttons.first().attr("href") shouldBe "/sdec-stride-frontend/create-thread/thread-type"
