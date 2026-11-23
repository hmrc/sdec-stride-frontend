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
import uk.gov.hmrc.sdecstridefrontend.models.{ThreadDetails, ThreadTypeOptions}
import uk.gov.hmrc.sdecstridefrontend.utils.DateTimeFormats.toDateFormat

object ThreadDetailsSummary {

  def rows(threadType: ThreadTypeOptions, threadDetails: ThreadDetails)(using messages: Messages): Seq[SummaryListRow] =
    Seq(
      SummaryRow("label.what_you_want_to_do", messages(threadType.messageKey)),
      SummaryRow.preservingLineBreaks("label.message", threadDetails.message),
      // TODO: replace with the real requested items once the thread details screen captures them
      SummaryRow("label.requested_items", "Item 1"),
      SummaryRow("label.response_deadline", threadDetails.responseDate.toDateFormat)
    )

}
