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

package uk.gov.hmrc.sdecstridefrontend.session

import play.api.mvc.Session
import uk.gov.hmrc.sdecstridefrontend.models.{RecipientDetails, ThreadDetails, ThreadTypeOptions}

import java.time.LocalDate
import scala.util.Try

object CreateThreadSession {

  private val FirstName               = "firstName"
  private val LastName                = "lastName"
  private val Email                   = "email"
  private val PhoneNumber             = "phoneNumber"
  private val NationalInsuranceNumber = "nationalInsuranceNumber"
  private val HasRelatedCase          = "hasRelatedCase"
  private val CaseReferenceNumber     = "caseReferenceNumber"
  private val SelectedThreadType      = "threadType"
  private val ThreadMessage           = "threadMessage"
  private val ThreadResponseDate      = "threadResponseDate"

  def recipientEntries(recipient: RecipientDetails): Seq[(String, String)] =
    Seq(
      FirstName               -> recipient.firstName,
      LastName                -> recipient.lastName,
      Email                   -> recipient.email,
      PhoneNumber             -> recipient.phoneNumber,
      NationalInsuranceNumber -> recipient.nationalInsuranceNumber,
      HasRelatedCase          -> recipient.hasRelatedCase.toString,
      CaseReferenceNumber     -> recipient.caseReferenceNumber.getOrElse("")
    )

  def threadTypeEntries(threadType: ThreadTypeOptions): Seq[(String, String)] =
    Seq(SelectedThreadType -> threadType.toString)

  def threadDetailsEntries(threadDetails: ThreadDetails): Seq[(String, String)] =
    Seq(
      ThreadMessage      -> threadDetails.message,
      ThreadResponseDate -> threadDetails.responseDate.toString
    )

  def recipient(session: Session): Option[RecipientDetails] =
    for {
      firstName               <- session.get(FirstName)
      lastName                <- session.get(LastName)
      email                   <- session.get(Email)
      phoneNumber             <- session.get(PhoneNumber)
      nationalInsuranceNumber <- session.get(NationalInsuranceNumber)
      hasRelatedCase          <- session.get(HasRelatedCase).flatMap(_.toBooleanOption)
    } yield RecipientDetails(
      firstName = firstName,
      lastName = lastName,
      email = email,
      phoneNumber = phoneNumber,
      nationalInsuranceNumber = nationalInsuranceNumber,
      hasRelatedCase = hasRelatedCase,
      caseReferenceNumber = session.get(CaseReferenceNumber).filter(_.nonEmpty)
    )

  def threadType(session: Session): Option[ThreadTypeOptions] =
    session.get(SelectedThreadType).flatMap(ThreadTypeOptions.parseTypeOption)

  def threadDetails(session: Session): Option[ThreadDetails] =
    for {
      message      <- session.get(ThreadMessage)
      responseDate <- session.get(ThreadResponseDate).flatMap(date => Try(LocalDate.parse(date)).toOption)
    } yield ThreadDetails(message = message, responseDate = responseDate)

}
