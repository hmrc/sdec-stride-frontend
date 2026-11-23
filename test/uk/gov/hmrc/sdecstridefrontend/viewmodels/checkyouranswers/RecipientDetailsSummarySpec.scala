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
import uk.gov.hmrc.sdecstridefrontend.controllers.routes as appRoutes
import uk.gov.hmrc.sdecstridefrontend.fixtures.ThreadFixtures

class RecipientDetailsSummarySpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  private given messages: Messages =
    app.injector.instanceOf[MessagesApi].preferred(FakeRequest())

  "RecipientDetailsSummary.rows" when {

    "the recipient has no related case" must {
      val rows = RecipientDetailsSummary.rows(ThreadFixtures.recipientWithoutCase)

      "show the five recipient rows in order" in {
        rows.map(_.key.content) mustBe Seq(
          Text("Their name"),
          Text("Email address"),
          Text("Mobile number"),
          Text("National Insurance number"),
          Text("Related case")
        )
      }

      "show the recipient's details" in {
        rows.take(4).map(_.value.content) mustBe Seq(
          Text("Jane Doe"),
          Text("jane.doe@example.com"),
          Text("07000000000"),
          Text("AA123456A")
        )
      }

      "show No for related case" in {
        rows.last.value.content mustBe Text("No")
      }

      "not show a related reference number" in {
        rows.map(_.key.content) must not contain Text("Related reference number")
      }
    }

    "the recipient has a related case" must {
      val rows = RecipientDetailsSummary.rows(ThreadFixtures.recipientWithCase)

      "show Yes for related case" in {
        rows(4).value.content mustBe Text("Yes")
      }

      "show the related reference number after related case" in {
        rows.size mustBe 6
        rows.last.key.content mustBe Text("Related reference number")
        rows.last.value.content mustBe Text("CS1234567TF890")
      }
    }

    "building any rows" must {
      val rows        = RecipientDetailsSummary.rows(ThreadFixtures.recipientWithoutCase)
      val changeLinks = rows.flatMap(_.actions).flatMap(_.items)

      "link every Change action to the development in progress page" in {
        changeLinks.map(_.href).distinct mustBe Seq(appRoutes.DevelopmentInProgressController.onPageLoad().url)
      }

      "give every Change action visually hidden text naming its row" in {
        changeLinks.flatMap(_.visuallyHiddenText) mustBe Seq(
          "Their name",
          "Email address",
          "Mobile number",
          "National Insurance number",
          "Related case"
        )
      }
    }
  }

}
