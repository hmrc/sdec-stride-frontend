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

package uk.gov.hmrc.sdecstridefrontend.models
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.auth.core.{Enrolment, Enrolments}
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Email, Name}

object TestStaff {

  def enrolments(roles: String*) =
    Enrolments(
        roles.map { role =>
          Enrolment(role)
        }.toSet
    )

  val standardStaff = StaffAuth(
    credentials = Credentials(providerId = "1000", providerType = "PrivilegeApplication"),
    email = Email("tester@example.net"),
    enrolments = Enrolments(Set(Enrolment("sdec_integration_user"))),
    name = Name(Some("Test"), Some("User"))
  )
  
  val insufficientRoleStaff = standardStaff.copy(
    credentials = standardStaff.credentials.copy(providerId = "1001"), enrolments = enrolments("xxx_user")
  )

  val staffRetrievalResult =
    new ~(
      new ~(
        new ~(
          standardStaff.enrolments,
          Some(standardStaff.email.value)
        ),
        Some(standardStaff.credentials)
      ),
      Some(standardStaff.name)
    )

  val insufficientEnrolmentsRetrievalResult =
    new ~(
      new ~(
        new ~(
          insufficientRoleStaff.enrolments,
          Some(insufficientRoleStaff.email.value)
        ),
        Some(insufficientRoleStaff.credentials)
      ),
      Some(insufficientRoleStaff.name)
    )
}
