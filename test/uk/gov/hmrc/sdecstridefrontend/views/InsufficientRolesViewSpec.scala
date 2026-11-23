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
import org.jsoup.select.Elements
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import uk.gov.hmrc.sdecstridefrontend.config.AppConfig
import uk.gov.hmrc.sdecstridefrontend.views.html.InsufficientRoles

class InsufficientRolesViewSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {
  private val view        = app.injector.instanceOf[InsufficientRoles]
  private val request     = FakeRequest("GET", "/sdec-stride-frontend/insufficient-roles")
  private val messages    = app.injector.instanceOf[MessagesApi].preferred(request)
  private given AppConfig = app.injector.instanceOf[AppConfig]

  private val page: Document = Jsoup.parse(view()(using request, messages).body)

  "InsufficientRoles Page" should {
    "have the title in the page" in {
      page.title() shouldBe "Secure Document Exchange - sdec-stride-frontend - GOV.UK"
    }

    "have a unique H1 heading" in {
      val h1Elements: Elements = page.getElementsByTag("h1")
      h1Elements.size() shouldBe 1
      h1Elements.text() shouldBe "Insufficient Role"
      // h1Elements.text() shouldBe pageTitle
    }

    "show the insufficient roles" in {
      val mainPage = page.getElementById("main-content")
      mainPage.getElementsByTag("h1").text() shouldBe "Insufficient Role"
    }
  }

}
