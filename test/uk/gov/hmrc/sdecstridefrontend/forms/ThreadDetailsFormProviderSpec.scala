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

package uk.gov.hmrc.sdecstridefrontend.forms

import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.i18n.{Lang, MessagesImpl}
import play.api.test.Helpers.stubMessagesApi
import uk.gov.hmrc.sdecstridefrontend.forms.validators.ThreadDetailsFormProvider

import java.time.LocalDate

class ThreadDetailsFormProviderSpec extends AnyWordSpec with Matchers {

  given messages: play.api.i18n.Messages =
    MessagesImpl(Lang("en"), stubMessagesApi())

  private val form = new ThreadDetailsFormProvider()()

  "ThreadDetailsFormProvider" should {

    "bind valid data" in {
      val result =
        form.bind(
          Map(
            "message"            -> "Valid message",
            "responseDate.day"   -> "1",
            "responseDate.month" -> "12",
            "responseDate.year"  -> "2099"
          )
        )

      result.hasErrors shouldBe false
    }

    "fail when message is missing" in {
      val result =
        form.bind(
          Map(
            "responseDate.day"   -> "1",
            "responseDate.month" -> "12",
            "responseDate.year"  -> "2099"
          )
        )

      result.hasErrors shouldBe true

      result.error("message").value.message shouldBe
        "label.thread_details_error_message_required"
    }

    "fail when message exceeds 1000 characters" in {
      val result =
        form.bind(
          Map(
            "message"            -> ("a" * 1001),
            "responseDate.day"   -> "1",
            "responseDate.month" -> "12",
            "responseDate.year"  -> "2099"
          )
        )

      result.hasErrors shouldBe true

      result.error("message").value.message shouldBe
        "label.thread_details_error_message_length"
    }

    "fail when response date is in the past" in {
      val yesterday = LocalDate.now().minusDays(1)

      val result =
        form.bind(
          Map(
            "message"            -> "Valid message",
            "responseDate.day"   -> yesterday.getDayOfMonth.toString,
            "responseDate.month" -> yesterday.getMonthValue.toString,
            "responseDate.year"  -> yesterday.getYear.toString
          )
        )

      result.hasErrors shouldBe true

      result.errors.map(_.message) should contain(
        "label.thread_details_response_date_error_future"
      )
    }

    "allow today's date" in {
      val today = LocalDate.now()

      val result =
        form.bind(
          Map(
            "message"            -> "Valid message",
            "responseDate.day"   -> today.getDayOfMonth.toString,
            "responseDate.month" -> today.getMonthValue.toString,
            "responseDate.year"  -> today.getYear.toString
          )
        )

      result.hasErrors shouldBe false
    }

    "unbind a valid ThreadDetails instance" in {
      val bound =
        form.fill(
          uk.gov.hmrc.sdecstridefrontend.models.ThreadDetails(
            message = "Hello",
            responseDate = LocalDate.of(2099, 12, 1)
          )
        )

      bound("message").value shouldBe Some("Hello")
    }
  }
}
