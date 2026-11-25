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

import org.scalatest.OptionValues
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.shouldBe
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.data.FormError

import scala.collection.immutable.Seq

class RecipientDetailsFormProviderSpec
    extends AnyWordSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with OptionValues {

  "bind" should {
    "store full given form data Map in Form class if form data Map given is all correct" in {
      val formData = Map(
        "firstName"               -> "John",
        "lastName"                -> "Doe",
        "email"                   -> "john.doe@hmrc.com",
        "phoneNumber"             -> "07777777777",
        "nationalInsuranceNumber" -> "AB123456C",
        "hasRelatedCase"          -> "false"
      )
      val recipientDetailsFormProvider = RecipientDetailsFormProvider()
      val boundForm                    = recipientDetailsFormProvider().bind(formData)
      boundForm.data      shouldBe formData
      boundForm.hasErrors shouldBe false
    }

    "return a Form with errors if given a firstName that is longer than 20 characters" in {
      val formData = Map(
        "firstName"               -> "NebuchadnezzarAtaxerxes",
        "lastName"                -> "Doe",
        "email"                   -> "john.doe@hmrc.com",
        "phoneNumber"             -> "07777777777",
        "nationalInsuranceNumber" -> "AB123456C",
        "hasRelatedCase"          -> "false"
      )
      val recipientDetailsFormProvider = RecipientDetailsFormProvider()
      val boundForm                    = recipientDetailsFormProvider().bind(formData)

      boundForm.hasErrors          shouldBe true
      boundForm.error("firstName") shouldBe Some(
        FormError("firstName", List("label.name_must_be_between_2_and_20_characters"), Seq(20))
      )
    }

    "return a Form with errors if given lastName longer than 20 characters" in {
      val formData = Map(
        "firstName"               -> "John",
        "lastName"                -> "SimpsonWellingtonJone",
        "email"                   -> "john.doe@hmrc.com",
        "phoneNumber"             -> "07777777777",
        "nationalInsuranceNumber" -> "AB123456C",
        "hasRelatedCase"          -> "false"
      )
      val recipientDetailsFormProvider = RecipientDetailsFormProvider()
      val boundForm                    = recipientDetailsFormProvider().bind(formData)

      boundForm.hasErrors         shouldBe true
      boundForm.error("lastName") shouldBe Some(
        FormError("lastName", Seq("label.name_must_be_between_2_and_20_characters"), Seq(20))
      )
    }

    "return a Form with errors if given invalid email" in {
      val formData = Map(
        "firstName"               -> "John",
        "lastName"                -> "Doe",
        "email"                   -> "john.doe-------",
        "phoneNumber"             -> "07777777777",
        "nationalInsuranceNumber" -> "AB123456C",
        "hasRelatedCase"          -> "false"
      )
      val recipientDetailsFormProvider = RecipientDetailsFormProvider()
      val boundForm                    = recipientDetailsFormProvider().bind(formData)

      boundForm.hasErrors      shouldBe true
      boundForm.error("email") shouldBe Some(
        FormError(
          "email",
          List("label.enter_an_email_address_in_the_correct_format_"),
          Seq(
            """^[a-zA-Z0-9.!#$%&’'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)+$"""
          )
        )
      )
    }

    "return a Form with errors if given invalid phoneNumber" in {
      val formData = Map(
        "firstName"               -> "John",
        "lastName"                -> "Doe",
        "email"                   -> "john.doe@hmrc.com",
        "phoneNumber"             -> "07777777_*&",
        "nationalInsuranceNumber" -> "AB123456C",
        "hasRelatedCase"          -> "false"
      )
      val recipientDetailsFormProvider = RecipientDetailsFormProvider()
      val boundForm                    = recipientDetailsFormProvider().bind(formData)

      boundForm.hasErrors            shouldBe true
      boundForm.error("phoneNumber") shouldBe Some(
        FormError(
          "phoneNumber",
          List("label.enter_a_mobile_number_in_the_correct_format_"),
          Seq("""^[0-9 +().-]{9,16}$""")
        )
      )
    }

    "return a Form with errors if given invalid nationalInsuranceNumber, breaking rules for NI number" in {
      val formData = Map(
        "firstName"               -> "John",
        "lastName"                -> "Doe",
        "email"                   -> "john.doe@hmrc.com",
        "phoneNumber"             -> "07777777777",
        "nationalInsuranceNumber" -> "DD123456C", // The letter D is not allowed as either first or 2nd character
        "hasRelatedCase"          -> "false"
      )
      val recipientDetailsFormProvider = RecipientDetailsFormProvider()
      val boundForm                    = recipientDetailsFormProvider().bind(formData)

      boundForm.hasErrors                        shouldBe true
      boundForm.error("nationalInsuranceNumber") shouldBe Some(
        FormError(
          "nationalInsuranceNumber",
          List("label.enter_a_national_insurance_number_in_the_correct_format"),
          Seq(
            "^([ACEHJLMOPRSWXY][A-CEGHJ-NPR-TW-Z]|B[A-CEHJ-NPR-TW-Z]|G[ACEGHJ-NPR-TW-Z]|[KT][A-CEGHJ-MPR-TW-Z]|N[A-CEGHJL-NPR-SW-Z]|Z[A-CEGHJ-NPR-TW-Y])[0-9]{6}[A-D]$"
          )
        )
      )
    }

    "return a Form with errors if given invalid hasRelatedCase" in {
      val formData = Map(
        "firstName"               -> "John",
        "lastName"                -> "Doe",
        "email"                   -> "john.doe@hmrc.com",
        "phoneNumber"             -> "07777777777",
        "nationalInsuranceNumber" -> "AB123456C",
        "hasRelatedCase"          -> "NotABoolean"
      )
      val recipientDetailsFormProvider = RecipientDetailsFormProvider()
      val boundForm                    = recipientDetailsFormProvider().bind(formData)

      boundForm.hasErrors               shouldBe true
      boundForm.error("hasRelatedCase") shouldBe Some(FormError("hasRelatedCase", List("error.boolean"), Seq()))
    }

    "return a Form with errors if given missing firstName" in {
      val formData = Map(
        "lastName"                -> "Doe",
        "email"                   -> "john.doe@hmrc.com",
        "phoneNumber"             -> "07777777777",
        "nationalInsuranceNumber" -> "AB123456C",
        "hasRelatedCase"          -> "false"
      )
      val recipientDetailsFormProvider = RecipientDetailsFormProvider()
      val boundForm                    = recipientDetailsFormProvider().bind(formData)

      boundForm.hasErrors          shouldBe true
      boundForm.error("firstName") shouldBe Some(FormError("firstName", List("label.enter_a_first_name"), Seq()))
    }

    "return a Form with errors if given missing lastName" in {
      val formData = Map(
        "firstName"               -> "John",
        "email"                   -> "john.doe@hmrc.com",
        "phoneNumber"             -> "07777777777",
        "nationalInsuranceNumber" -> "AB123456C",
        "hasRelatedCase"          -> "false"
      )
      val recipientDetailsFormProvider = RecipientDetailsFormProvider()
      val boundForm                    = recipientDetailsFormProvider().bind(formData)

      boundForm.hasErrors         shouldBe true
      boundForm.error("lastName") shouldBe Some(FormError("lastName", List("label.enter_a_last_name"), Seq()))
    }

    "return a Form with errors if given missing email" in {
      val formData = Map(
        "firstName"               -> "John",
        "lastName"                -> "Doe",
        "phoneNumber"             -> "07777777777",
        "nationalInsuranceNumber" -> "AB123456C",
        "hasRelatedCase"          -> "false"
      )
      val recipientDetailsFormProvider = RecipientDetailsFormProvider()
      val boundForm                    = recipientDetailsFormProvider().bind(formData)

      boundForm.hasErrors      shouldBe true
      boundForm.error("email") shouldBe Some(FormError("email", List("label.enter_an_email_address"), Seq()))
    }

    "return a Form with errors if given missing phoneNumber" in {
      val formData = Map(
        "firstName"               -> "John",
        "lastName"                -> "Doe",
        "email"                   -> "john.doe@hmrc.com",
        "nationalInsuranceNumber" -> "AB123456C",
        "hasRelatedCase"          -> "false"
      )
      val recipientDetailsFormProvider = RecipientDetailsFormProvider()
      val boundForm                    = recipientDetailsFormProvider().bind(formData)

      boundForm.hasErrors            shouldBe true
      boundForm.error("phoneNumber") shouldBe Some(
        FormError("phoneNumber", List("label.enter_a_mobile_number"), Seq())
      )
    }

    "return a Form with errors if given missing nationalInsuranceNumber" in {
      val formData = Map(
        "firstName"      -> "John",
        "lastName"       -> "Doe",
        "email"          -> "john.doe@hmrc.com",
        "phoneNumber"    -> "07777777777",
        "hasRelatedCase" -> "false"
      )
      val recipientDetailsFormProvider = RecipientDetailsFormProvider()
      val boundForm                    = recipientDetailsFormProvider().bind(formData)

      boundForm.hasErrors                        shouldBe true
      boundForm.error("nationalInsuranceNumber") shouldBe Some(
        FormError("nationalInsuranceNumber", List("label.enter_a_national_insurance_number"), Seq())
      )
    }

    "return a Form with errors if given missing hasRelatedCase" in {
      val formData = Map(
        "firstName"               -> "John",
        "lastName"                -> "Doe",
        "email"                   -> "john.doe@hmrc.com",
        "phoneNumber"             -> "07777777777",
        "nationalInsuranceNumber" -> "AB123456C"
      )
      val recipientDetailsFormProvider = RecipientDetailsFormProvider()
      val boundForm                    = recipientDetailsFormProvider().bind(formData)

      boundForm.hasErrors               shouldBe true
      boundForm.error("hasRelatedCase") shouldBe Some(
        FormError("hasRelatedCase", List("label.select_yes_or_no"), Seq())
      )
    }

    "return a Form with caseReferenceNumber when given valid input with hasRelatedCase=true and a valid caseReferenceNumber" in {
      val formData = Map(
        "firstName"               -> "John",
        "lastName"                -> "Doe",
        "email"                   -> "john.doe@hmrc.com",
        "phoneNumber"             -> "07777777777",
        "nationalInsuranceNumber" -> "AB123456C",
        "hasRelatedCase"          -> "true",
        "caseReferenceNumber" -> "ThisIsAValidCase1234" // only requirement for caseReferenceNumber is not exceed 30 characters
      )
      val recipientDetailsFormProvider = RecipientDetailsFormProvider()
      val boundForm                    = recipientDetailsFormProvider().bind(formData)

      boundForm.data      shouldBe formData
      boundForm.hasErrors shouldBe false
    }

    "return a Form with errors if form data with hasRelatedCase=true but no caseReferenceNumber " in {
      val formData = Map(
        "firstName"               -> "John",
        "lastName"                -> "Doe",
        "email"                   -> "john.doe@hmrc.com",
        "phoneNumber"             -> "07777777777",
        "nationalInsuranceNumber" -> "AB123456C",
        "hasRelatedCase"          -> "true"
      )
      val recipientDetailsFormProvider = RecipientDetailsFormProvider()
      val boundForm                    = recipientDetailsFormProvider().bind(formData)

      boundForm.hasErrors                    shouldBe true
      boundForm.error("caseReferenceNumber") shouldBe Some(
        FormError(
          "caseReferenceNumber",
          List("label.enter_a_case_reference_number"),
          Seq.empty[String]
        )
      )
    }

    "return a Form with errors if form data with hasRelatedCase=true but caseReferenceNumber that is not purely alphaNumeric" in {
      val formData = Map(
        "firstName"               -> "John",
        "lastName"                -> "Doe",
        "email"                   -> "john.doe@hmrc.com",
        "phoneNumber"             -> "07777777777",
        "nationalInsuranceNumber" -> "AB123456C",
        "hasRelatedCase"          -> "true",
        "caseReferenceNumber"     -> "This_Is_Invalid%^&$£"
      )
      val recipientDetailsFormProvider = RecipientDetailsFormProvider()
      val boundForm                    = recipientDetailsFormProvider().bind(formData)

      boundForm.hasErrors                    shouldBe true
      boundForm.error("caseReferenceNumber") shouldBe Some(
        FormError(
          "caseReferenceNumber",
          List("label.number_must_only_include_letters_a_to_z_and_numbers_0_to_9"),
          Seq()
        )
      )
    }
  }

}
