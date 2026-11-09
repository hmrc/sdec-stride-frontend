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

package uk.gov.hmrc.sdecstridefrontend.views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import uk.gov.hmrc.sdecstridefrontend.views.html.WorkspaceView

class WorkspaceViewSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  private val view     = app.injector.instanceOf[WorkspaceView]
  private val request  = FakeRequest("GET", "/sdec-stride-frontend")
  private val messages = app.injector.instanceOf[MessagesApi].preferred(request)

  private def page(): Document =
    Jsoup.parse(view()(using request, messages).body)

  "WorkspaceView" should:
    "have the Workspace page title" in:
      page().title() shouldBe "Workspace"

    "show a Workspace h1" in:
      val main = page().getElementById("main-content")
      main.getElementsByTag("h1").text() shouldBe "Workspace"

    "show a single Create New Thread button linking to Recipient Details" in:
      val main    = page().getElementById("main-content")
      val buttons = main.getElementsByClass("govuk-button")
      buttons.size()               shouldBe 1
      buttons.first().text()       shouldBe "Create new thread"
      buttons.first().attr("href") shouldBe "/sdec-stride-frontend/create-thread/recipient-details"

}
