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

package uk.gov.hmrc.sdecstridefrontend.forms.mappings

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.data.FormError
import play.api.i18n.{Lang, MessagesImpl}
import play.api.test.Helpers.stubMessagesApi

import java.time.LocalDate

class ThreadDetailsDateFormatterSpec extends AnyWordSpec with Matchers {

  given messages: play.api.i18n.Messages =
    MessagesImpl(Lang("en"), stubMessagesApi())

  private val formatter =
    new ThreadDetailsDateFormatter(
      invalidKey = "invalid",
      allRequiredKey = "all.required",
      twoRequiredKey = "two.required",
      requiredKey = "required"
    )

  "bind" should {

    "return a valid date when all fields are valid" in {
      val result =
        formatter.bind(
          "responseDate",
          Map(
            "responseDate.day"   -> "1",
            "responseDate.month" -> "12",
            "responseDate.year"  -> "2099"
          )
        )

      result shouldBe Right(LocalDate.of(2099, 12, 1))
    }

    "return allRequiredKey when all fields are missing" in {
      val result =
        formatter.bind("responseDate", Map.empty)

      result shouldBe Left(
        List(
          FormError(
            "responseDate",
            "all.required"
          )
        )
      )
    }

    "return twoRequiredKey when only one field is provided" in {
      val result =
        formatter.bind(
          "responseDate",
          Map(
            "responseDate.day" -> "1"
          )
        )

      result shouldBe Left(
        List(
          FormError(
            "responseDate",
            "two.required"
          )
        )
      )
    }

    "return year required error when year is missing" in {
      val result =
        formatter.bind(
          "responseDate",
          Map(
            "responseDate.day"   -> "1",
            "responseDate.month" -> "12"
          )
        )

      result shouldBe Left(
        List(
          FormError(
            "responseDate.year",
            "label.thread_details_response_date_error_year_required"
          )
        )
      )
    }

    "return year length error when year is not four digits" in {
      val result =
        formatter.bind(
          "responseDate",
          Map(
            "responseDate.day"   -> "1",
            "responseDate.month" -> "12",
            "responseDate.year"  -> "24"
          )
        )

      result shouldBe Left(
        List(
          FormError(
            "responseDate.year",
            "label.thread_details_response_date_error_year_length"
          )
        )
      )
    }

    "return invalid error when day is out of range" in {
      val result =
        formatter.bind(
          "responseDate",
          Map(
            "responseDate.day"   -> "32",
            "responseDate.month" -> "12",
            "responseDate.year"  -> "2099"
          )
        )

      result shouldBe Left(
        List(
          FormError(
            "responseDate.day",
            "invalid"
          )
        )
      )
    }

    "return invalid error when month is out of range" in {
      val result =
        formatter.bind(
          "responseDate",
          Map(
            "responseDate.day"   -> "1",
            "responseDate.month" -> "13",
            "responseDate.year"  -> "2099"
          )
        )

      result shouldBe Left(
        List(
          FormError(
            "responseDate.month",
            "invalid"
          )
        )
      )
    }

    "return invalid error for an impossible date" in {
      val result =
        formatter.bind(
          "responseDate",
          Map(
            "responseDate.day"   -> "31",
            "responseDate.month" -> "2",
            "responseDate.year"  -> "2099"
          )
        )

      result shouldBe Left(
        List(
          FormError(
            "responseDate.day",
            "invalid"
          )
        )
      )
    }
  }

  "unbind" should {

    "return a map representation of a LocalDate" in {
      formatter.unbind(
        "responseDate",
        LocalDate.of(2099, 12, 1)
      ) shouldBe Map(
        "responseDate.day"   -> "1",
        "responseDate.month" -> "12",
        "responseDate.year"  -> "2099"
      )
    }
  }

  "ThreadDetailsMonthFormatter" should {

    "unbind a month value" in {

      val formatter =
        new ThreadDetailsMonthFormatter("invalid")

      formatter.unbind("month", 12) shouldBe Map(
        "month" -> "12"
      )
    }
  }
}
