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

import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import uk.gov.hmrc.sdecstridefrontend.models.{ThreadType, ThreadTypeOptions}

class ThreadTypeFormProviderSpec extends AnyWordSpec with Matchers {

  private val form = new ThreadTypeFormProvider()()

  "ThreadTypeFormProvider" should {

    "bind Request successfully" in {
      val result =
        form.bind(
          Map(
            "threadType" -> "Request"
          )
        )

      result.hasErrors shouldBe false

      result.value shouldBe Some(
        ThreadType(ThreadTypeOptions.Request)
      )
    }

    "bind Send successfully" in {
      val result =
        form.bind(
          Map(
            "threadType" -> "Send"
          )
        )

      result.hasErrors shouldBe false

      result.value shouldBe Some(
        ThreadType(ThreadTypeOptions.Send)
      )
    }

    "bind SendAndRequest successfully" in {
      val result =
        form.bind(
          Map(
            "threadType" -> "SendAndRequest"
          )
        )

      result.hasErrors shouldBe false

      result.value shouldBe Some(
        ThreadType(ThreadTypeOptions.SendAndRequest)
      )
    }

    "return an error when no thread type is selected" in {
      val result =
        form.bind(Map.empty[String, String])

      result.hasErrors shouldBe true

      result.error("threadType").value.message shouldBe
        "label.select_what_you_need_to_do"
    }

    "return an error when an invalid thread type is supplied" in {
      val result =
        form.bind(
          Map(
            "threadType" -> "InvalidOption"
          )
        )

      result.hasErrors shouldBe true
    }

    "fill a ThreadType correctly" in {
      val filled =
        form.fill(
          ThreadType(ThreadTypeOptions.Request)
        )

      filled("threadType").value shouldBe Some("Request")
    }
  }
}
