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
        Invalid("label.thread_details_responseDate_error_future")
    }

  def apply(): Form[ThreadDetails] = Form(
    mapping(
      "message" -> text("label.thread_details_error_message_required")
        .verifying(
          maxLengthWithOverage(
            messageMaxLength,
            "label.thread_details_error_message_length"
          )
        ),
      "responseDate" ->
        threadDetailsDate(
          invalidKey = "label.thread_details_responseDate_error_invalid",
          allRequiredKey = "label.thread_details_responseDate_error_all_required",
          twoRequiredKey = "label.thread_details_responseDate_error_two_required",
          requiredKey = "label.thread_details_responseDate_error_required"
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
