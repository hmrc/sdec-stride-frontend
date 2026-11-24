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

import play.api.data.Forms.*
import play.api.data.format.Formatter
import play.api.data.validation.Constraint
import play.api.data.{Form, FormError, Mapping}
import uk.gov.hmrc.sdecstridefrontend.forms.mappings.Mappings
import uk.gov.hmrc.sdecstridefrontend.forms.validators.Validation
import uk.gov.hmrc.sdecstridefrontend.models.RecipientDetails

import javax.inject.Inject

class RecipientDetailsFormProvider @Inject() extends Mappings {

  private val caseReferenceMapping: Mapping[Option[String]] =
    of(new Formatter[Option[String]] {

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Option[String]] =
        if data.get("hasRelatedCase").contains("true") then {
          data.get(key).map(_.trim).filter(_.nonEmpty) match {
            case None =>
              Left(Seq(FormError(key, "label.enter_a_case_reference_number")))
            case Some(v) if !v.matches(Validation.caseRefRegex) =>
              Left(Seq(FormError(key, "label.number_must_only_include_letters_a_to_z_and_numbers_0_to_9")))
            case Some(v) =>
              Right(Some(v))
          }
        } else Right(None)

      override def unbind(key: String, value: Option[String]): Map[String, String] =
        value.map(key -> _).toMap
    })

  def apply(): Form[RecipientDetails] = Form(
    mapping(
      "firstName" -> text("label.enter_a_first_name").verifying(
        firstError(
          maxLength(Validation.nameMaxLength, "label.name_must_be_between_2_and_20_characters"),
          minLength(Validation.nameMinLength, "label.name_must_be_between_2_and_20_characters"),
          regexp(Validation.nameRegex, "label.name_must_only_include_letters_a_to_z_and_punctuation_")
        )
      ),
      "lastName" -> text("label.enter_a_last_name")
        .verifying(
          firstError(
            maxLength(Validation.nameMaxLength, "label.name_must_be_between_2_and_20_characters"),
            minLength(Validation.nameMinLength, "label.name_must_be_between_2_and_20_characters"),
            regexp(Validation.nameRegex, "label.name_must_only_include_letters_a_to_z_and_punctuation_")
          )
        ),
      "email" -> text("label.enter_an_email_address")
        .verifying(regexp(Validation.emailRegex, "label.enter_an_email_address_in_the_correct_format_")),
      "phoneNumber" -> text("label.enter_a_mobile_number")
        .verifying(
          firstError(
            regexp(Validation.phoneRegex, "label.enter_a_mobile_number_in_the_correct_format_"),
            regexp(Validation.phoneUkRegex, "label.enter_a_uk_mobile_number_")
          )
        ),
      "nationalInsuranceNumber" -> text(
        "label.enter_a_national_insurance_number"
      )
        .transform[String](_.replaceAll("\\s", "").toUpperCase, identity)
        .verifying(
          regexp(Validation.ninoValidRegex, "label.enter_a_national_insurance_number_in_the_correct_format")
        )
        .transform[String](formatNino, identity),
      "hasRelatedCase"      -> boolean("label.select_yes_or_no"),
      "caseReferenceNumber" -> caseReferenceMapping
    )(RecipientDetails.apply)(rd =>
      Some(
        (
          rd.firstName,
          rd.lastName,
          rd.email,
          rd.phoneNumber,
          rd.nationalInsuranceNumber,
          rd.hasRelatedCase,
          rd.caseReferenceNumber
        )
      )
    )
  )

  private def formatNino(nino: String): String =
    Seq(
      nino.substring(0, 2),
      nino.substring(2, 4),
      nino.substring(4, 6),
      nino.substring(6, 8),
      nino.substring(8, 9)
    ).mkString(" ")

}
