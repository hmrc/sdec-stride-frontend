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

import play.api.i18n.Messages
import play.api.libs.json.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import uk.gov.hmrc.sdecstridefrontend.viewmodels.govuk.all.HintViewModel

enum ThreadTypeOptions(val messageKey: String, val hintKey: String) {
  case Send
      extends ThreadTypeOptions(
        "label.send_files",
        "label.you_must_include_at_least_one_file_and_a_message_the_recipient_"
      )
  case Request
      extends ThreadTypeOptions(
        "label.request_files",
        "label.you_must_include_a_message_and_the_recipient_must_"
      )
  case SendAndRequest
      extends ThreadTypeOptions(
        "label.send_and_request_files",
        "label.you_must_include_a_file_and_a_message_and_the_recipient_"
      )
}

object ThreadTypeOptions {

  def options(using messages: Messages): Seq[RadioItem] =
    values.toSeq.zipWithIndex.map { (option, index) =>
      RadioItem(
        id = Some(if index == 0 then "threadType" else s"threadType_$index"),
        value = Some(option.toString),
        content = Text(messages(option.messageKey)),
        hint = Some(HintViewModel(Text(messages(option.hintKey))))
      )
    }

  def parseTypeOption(value: String): Option[ThreadTypeOptions] =
    ThreadTypeOptions.values.find(_.toString.equalsIgnoreCase(value))

  given Enumerable[ThreadTypeOptions] = Enumerable(
    ThreadTypeOptions.values.map(status => status.toString -> status).toSeq*
  )

  given Format[ThreadTypeOptions] = Format(
    Reads {
      case JsString(s) =>
        parseTypeOption(s)
          .map(JsSuccess(_))
          .getOrElse(JsError("error.invalid"))
      case _ => JsError("error.invalid")
    },
    Writes(o => JsString(o.toString))
  )
}
