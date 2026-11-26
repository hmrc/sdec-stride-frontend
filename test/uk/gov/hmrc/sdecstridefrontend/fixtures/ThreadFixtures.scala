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

package uk.gov.hmrc.sdecstridefrontend.fixtures

import uk.gov.hmrc.sdecstridefrontend.models.{RecipientDetails, ThreadDetails, ThreadTypeOptions}
import uk.gov.hmrc.sdecstridefrontend.session.CreateThreadSession

import java.time.LocalDate

object ThreadFixtures {

  val recipientWithoutCase: RecipientDetails =
    RecipientDetails(
      firstName = "Jane",
      lastName = "Doe",
      email = "jane.doe@example.com",
      phoneNumber = "07000000000",
      nationalInsuranceNumber = "AA123456A",
      hasRelatedCase = false,
      caseReferenceNumber = None
    )

  val recipientWithCase: RecipientDetails =
    recipientWithoutCase.copy(
      hasRelatedCase = true,
      caseReferenceNumber = Some("CS1234567TF890")
    )

  val threadType: ThreadTypeOptions = ThreadTypeOptions.Request

  val threadDetails: ThreadDetails =
    ThreadDetails(
      message = "Please send your latest payslips",
      responseDate = LocalDate.of(2029, 12, 28)
    )

  def sessionEntries(recipient: RecipientDetails = recipientWithCase): Seq[(String, String)] =
    CreateThreadSession.recipientEntries(recipient) ++
      CreateThreadSession.threadTypeEntries(threadType) ++
      CreateThreadSession.threadDetailsEntries(threadDetails)

}
