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
import uk.gov.hmrc.sdecstridefrontend.models.RecipientDetails

import javax.inject.Inject

class RecipientDetailsFormProvider @Inject() extends Mappings {

  private val nameMaxLength   = 100
  private val ninoFormatRegex = "^[A-Z]{2}[0-9]{6}[A-D]$"
  private val ninoValidRegex  =
    "^([ACEHJLMOPRSWXY][A-CEGHJ-NPR-TW-Z]|B[A-CEHJ-NPR-TW-Z]|G[ACEGHJ-NPR-TW-Z]|[KT][A-CEGHJ-MPR-TW-Z]|N[A-CEGHJL-NPR-SW-Z]|Z[A-CEGHJ-NPR-TW-Y])[0-9]{6}[A-D]$"
  private val emailRegex =
    """^[a-zA-Z0-9.!#$%&’'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)+$"""
  private val phoneRegex       = """^[0-9 +().-]{9,16}$"""
  private val caseRefMaxLength = 30

  private val caseReferenceMapping: Mapping[Option[String]] =
    of(new Formatter[Option[String]] {

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Option[String]] =
        if data.get("hasRelatedCase").contains("true") then {
          data.get(key).map(_.trim).filter(_.nonEmpty) match {
            case None =>
              Left(Seq(FormError(key, "label.enter_a_reference_number_related_to_the_case")))
            case Some(v) if v.length > caseRefMaxLength =>
              Left(Seq(FormError(key, "label.reference_number_must_be_30_characters_or_less")))
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
        maxLength(nameMaxLength, "label.first_name_must_be_100_characters_or_less")
      ),
      "lastName" -> text("label.enter_a_last_name")
        .verifying(
          maxLength(nameMaxLength, "label.last_name_must_be_100_characters_or_less")
        ),
      "email" -> text("label.enter_an_email_address")
        .verifying(regexp(emailRegex, "label.enter_an_email_address_in_the_correct_format")),
      "phoneNumber" -> text("label.enter_a_mobile_number")
        .verifying(regexp(phoneRegex, "label.enter_a_mobile_number_in_the_correct_format")),
      "nationalInsuranceNumber" -> text(
        "label.enter_a_national_insurance_number"
      )
        .transform[String](_.replaceAll("\\s", "").toUpperCase, identity)
        .verifying(
          firstError(
            regexp(
              ninoFormatRegex,
              "label.enter_a_national_insurance_number_in_the_correct_format"
            ),
            regexp(
              ninoValidRegex,
              "label.enter_a_real_national_insurance_number"
            )
          )
        )
        .transform[String](formatNino, identity),
      "hasRelatedCase"      -> boolean("label.select_whether_this_relates_to_an_existing_case"),
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
