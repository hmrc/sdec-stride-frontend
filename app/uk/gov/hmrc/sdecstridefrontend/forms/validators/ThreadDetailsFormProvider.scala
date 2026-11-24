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

package uk.gov.hmrc.sdecstridefrontend.forms.validators

import play.api.data.Form
import play.api.data.Forms.*
import play.api.data.validation.{Constraint, Invalid, Valid}
import uk.gov.hmrc.sdecstridefrontend.forms.mappings.Mappings
import uk.gov.hmrc.sdecstridefrontend.models.ThreadDetails

import java.time.LocalDate
import javax.inject.Inject

class ThreadDetailsFormProvider @Inject() extends Mappings {

  private val messageMaxLength = 1000

  private def maxLengthWithOverage(
    maximum:  Int,
    errorKey: String
  ): Constraint[String] =
    Constraint {
      case str if str.length <= maximum =>
        Valid
      case str =>
        Invalid(errorKey, str.length - maximum)
    }

  private val responseDateMustBeTodayOrFuture: Constraint[LocalDate] =
    Constraint {
      case date if date.isAfter(LocalDate.now().minusDays(1)) =>
        Valid

      case _ =>
        Invalid("label.the_response_date_must_be_in_the_future")
    }

  def apply(): Form[ThreadDetails] = Form(
    mapping(
      "message" -> text("label.enter_a_message")
        .verifying(
          maxLengthWithOverage(
            messageMaxLength,
            "label.message_must_be_1000_characters_or_less"
          )
        ),
      "responseDate" ->
        threadDetailsDate(
          invalidKey = "label.enter_a_valid_response_date",
          allRequiredKey = "label.enter_the_day_month_and_year",
          twoRequiredKey = "label.enter_the_missing_parts_of_the_date",
          requiredKey = "label.enter_the_missing_part_of_the_date"
        ).verifying(responseDateMustBeTodayOrFuture)
    )(
      ThreadDetails.apply
    )(threadDetails =>
      Some(
        (
          threadDetails.message,
          threadDetails.responseDate
        )
      )
    )
  )
}
