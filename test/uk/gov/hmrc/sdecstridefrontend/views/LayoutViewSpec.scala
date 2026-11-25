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
import play.twirl.api.Html
import uk.gov.hmrc.sdecstridefrontend.config.AppConfig
import uk.gov.hmrc.sdecstridefrontend.views.html.Layout

class LayoutViewSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {
  private val view     = app.injector.instanceOf[Layout]
  private val request  = FakeRequest("GET", "/sdec-stride-frontend/layout")
  private val messages = app.injector.instanceOf[MessagesApi].preferred(request)

  private given AppConfig = app.injector.instanceOf[AppConfig]

  private val title        = "This is a title page"
  private val contentBlock = """<p>Hello, <a href="https://example.com">world</a>!</p>"""

  private val page: Document =
    Jsoup.parse(view(pageTitle = title)(contentBlock = Html(contentBlock))(using request, messages).body)

  "Layout Page" should {
    "have the title in the page" in {
      page.title() shouldBe title
    }

    "populate the main content html element with contentBlock input" in {
      page.body().getElementsByTag("main").html() shouldBe contentBlock
    }

    "have the black link when given showBackLink=true (default value)" in {
      page.body().getElementsByClass("govuk-back-link").hasText shouldBe true
    }

    "not have the black link when given showBackLink=false" in {
      val pageWithBlackLinkFalse =
        Jsoup.parse(
          view(pageTitle = title, showBackLink = false)(contentBlock = Html(contentBlock))(using request, messages).body
        )
      pageWithBlackLinkFalse.body().getElementsByClass("govuk-back-link").hasText shouldBe false
    }
  }
}
