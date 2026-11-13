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

package uk.gov.hmrc.sdecstridefrontend.security.service

import jakarta.inject.Inject
import uk.gov.hmrc.auth.core.{Enrolment, Enrolments}
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Email, Name}
import uk.gov.hmrc.sdecstridefrontend.config.EnrollmentConfig
import uk.gov.hmrc.sdecstridefrontend.models.StaffAuth

class EnrolmentService @Inject() (config: EnrollmentConfig) extends EnrolmentServiceAlgebra {

  override def extractStaff(
    enrolments:  Enrolments,
    email:       Option[String],
    credentials: Option[Credentials],
    name:        Option[Name]
  ): Option[StaffAuth] = {
    val sdecEnrolments: Set[Enrolment] = enrolments.enrolments.filter(e => filterForSdec(e))
    for {
      emailStr  <- email
      cred      <- credentials
      nameMaybe <- name
    } yield StaffAuth(
      credentials = cred,
      email = Email(emailStr),
      enrolments = Enrolments(sdecEnrolments),
      name = nameMaybe
    )
  }

  override def verifyEnrollment(staff: StaffAuth): Boolean =
    staff.enrolments.enrolments.nonEmpty
  
  private def filterForSdec(enrolment: Enrolment): Boolean =
    enrolment.key.toUpperCase.startsWith(config.sdecAccessPrefixMatch)
}
