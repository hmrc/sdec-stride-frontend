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

package uk.gov.hmrc.sdecstridefrontend.viewmodels.checkyouranswers

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.sdecstridefrontend.models.RecipientDetails

object RecipientDetailsSummary {

  def rows(recipient: RecipientDetails)(using messages: Messages): Seq[SummaryListRow] =
    Seq(
      SummaryRow("label.their_name", s"${recipient.firstName} ${recipient.lastName}"),
      SummaryRow("label.email_address", recipient.email),
      SummaryRow("label.mobile_number", recipient.phoneNumber),
      SummaryRow("label.national_insurance_number", recipient.nationalInsuranceNumber),
      SummaryRow("label.related_case", relatedCaseAnswer(recipient))
    ) ++ caseReferenceRow(recipient)

  private def relatedCaseAnswer(recipient: RecipientDetails)(using messages: Messages): String =
    if recipient.hasRelatedCase then messages("label.yes") else messages("label.no")

  private def caseReferenceRow(recipient: RecipientDetails)(using Messages): Option[SummaryListRow] =
    recipient.caseReferenceNumber
      .filter(_ => recipient.hasRelatedCase)
      .map(reference => SummaryRow("label.related_reference_number", reference))

}
