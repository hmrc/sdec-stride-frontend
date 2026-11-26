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

package uk.gov.hmrc.sdecstridefrontend.controllers.createthread

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers.*

class RecipientDetailsControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  override def fakeApplication(): Application = new GuiceApplicationBuilder().build()

  private val controller = app.injector.instanceOf[RecipientDetailsController]

  "GET /create-thread/recipient-details" should {
    "return the recipient details page" in {
      val result = controller.onPageLoad(FakeRequest("GET", "/create-thread/recipient-details"))

      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include("Recipient details")
    }
  }

  "POST /create-thread/recipient-details" should {
    "redirect to thread type when the form is valid" in {
      val request =
        FakeRequest("POST", "/create-thread/recipient-details").withFormUrlEncodedBody(
          "firstName"               -> "Jane",
          "lastName"                -> "Doe",
          "email"                   -> "jane.doe@example.com",
          "phoneNumber"             -> "07000000000",
          "nationalInsuranceNumber" -> "AA123456A",
          "hasRelatedCase"          -> "true",
          "caseReferenceNumber"     -> "CS1234567TF890"
        )

      val result = controller.onSubmit()(request)

      status(result)           shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.ThreadTypeController.onPageLoad().url)
    }

    "return 400 when the form is invalid" in {
      val request = FakeRequest("POST", "/create-thread/recipient-details").withFormUrlEncodedBody()
      val result  = controller.onSubmit()(request)

      status(result) shouldBe Status.BAD_REQUEST
    }

    Seq(
      "the case reference is missing"             -> "",
      "the case reference has invalid characters" -> "CS-123!"
    ).foreach { (scenario, reference) =>
      s"return 400 when $scenario" in {
        val request =
          FakeRequest("POST", "/create-thread/recipient-details").withFormUrlEncodedBody(
            "firstName"               -> "Jane",
            "lastName"                -> "Doe",
            "email"                   -> "jane.doe@example.com",
            "phoneNumber"             -> "07000000000",
            "nationalInsuranceNumber" -> "AA123456A",
            "hasRelatedCase"          -> "true",
            "caseReferenceNumber"     -> reference
          )

        status(controller.onSubmit()(request)) shouldBe Status.BAD_REQUEST
      }
    }
  }

}
