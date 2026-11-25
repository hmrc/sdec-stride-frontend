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
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsString, JsSuccess}
import uk.gov.hmrc.sdecstridefrontend.helpers.TestEnum
import uk.gov.hmrc.sdecstridefrontend.helpers.TestEnum.{TestFailure, TestSuccess}

class EnumerableSpec extends AnyWordSpec with Matchers with OptionValues {

  "Enumerable" should {
    val enumerable = Enumerable(TestEnum.values.map(anEnum => anEnum.toString -> anEnum).toSeq*)
    "return the correct value for an existing name" in {

      enumerable.withName("TestSuccess").value shouldBe TestSuccess
      enumerable.withName("TestFailure").value shouldBe TestFailure
    }

    "return none when the enum is not an existing name" in {
      enumerable.withName("Success").isDefined shouldBe false
    }
  }
}
