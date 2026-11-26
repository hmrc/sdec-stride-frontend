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

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .build()

  private val fakeRequest = FakeRequest("GET", "/sdec-stride-frontend")
    .withFormUrlEncodedBody(
      "firstName" -> "John",
      "lastName"  -> "Doe"
    )
  private val controller = app.injector.instanceOf[RecipientDetailsController]

  controller

  "onPageLoad" should {

    "return 200 with html response when sent GET" in {
      val result = controller.onPageLoad(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "onSubmit" should {
    val fakeRequest = FakeRequest("POST", "/sdec-stride-frontend")

    "return 303 See Other when sent a POST with valid form" in {
      val fakeRequestWithFormData = fakeRequest
        .withFormUrlEncodedBody(
          "firstName"               -> "John",
          "lastName"                -> "Doe",
          "email"                   -> "john.doe@hmrc.com",
          "phoneNumber"             -> "07777777777",
          "nationalInsuranceNumber" -> "AB123456C",
          "hasRelatedCase"          -> "false"
        )
      val result = controller.onSubmit()(fakeRequestWithFormData)
      status(result)           shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some("/sdec-stride-frontend/create-thread/thread-type")
    }

    "return 400 if given bad NI Number" in {
      val fakeRequestWithFormData = fakeRequest
        .withFormUrlEncodedBody(
          "firstName"               -> "John",
          "lastName"                -> "Doe",
          "email"                   -> "john.doe@hmrc.com",
          "phoneNumber"             -> "07777777777",
          "nationalInsuranceNumber" -> "AB1234_56C",
          "hasRelatedCase"          -> "false"
        )
      val result = controller.onSubmit()(fakeRequestWithFormData)
      status(result) shouldBe Status.BAD_REQUEST
    }

    "return 400 if given bad phone Number" in {
      val fakeRequestWithFormData = fakeRequest
        .withFormUrlEncodedBody(
          "firstName"               -> "John",
          "lastName"                -> "Doe",
          "email"                   -> "john.doe@hmrc.com",
          "phoneNumber"             -> "07777777_=*",
          "nationalInsuranceNumber" -> "AB123456C",
          "hasRelatedCase"          -> "false"
        )
      val result = controller.onSubmit()(fakeRequestWithFormData)
      status(result) shouldBe Status.BAD_REQUEST
    }

    "return 400 if firstName is missing" in {
      val fakeRequestWithFormData = fakeRequest
        .withFormUrlEncodedBody(
          "lastName"                -> "Doe",
          "email"                   -> "john.doe@hmrc.com",
          "phoneNumber"             -> "07777777777",
          "nationalInsuranceNumber" -> "AB123456C",
          "hasRelatedCase"          -> "false"
        )
      val result = controller.onSubmit()(fakeRequestWithFormData)
      status(result) shouldBe Status.BAD_REQUEST
    }

    "return 400 if last name is missing" in {
      val fakeRequestWithFormData = fakeRequest
        .withFormUrlEncodedBody(
          "firstName"               -> "John",
          "email"                   -> "john.doe@hmrc.com",
          "phoneNumber"             -> "07777777777",
          "nationalInsuranceNumber" -> "AB1234_56C",
          "hasRelatedCase"          -> "false"
        )
      val result = controller.onSubmit()(fakeRequestWithFormData)
      status(result) shouldBe Status.BAD_REQUEST
    }

    "return 400 if email is missing" in {
      val fakeRequestWithFormData = fakeRequest
        .withFormUrlEncodedBody(
          "firstName"               -> "John",
          "lastName"                -> "Doe",
          "phoneNumber"             -> "07777777777",
          "nationalInsuranceNumber" -> "AB123456C",
          "hasRelatedCase"          -> "false"
        )
      val result = controller.onSubmit()(fakeRequestWithFormData)
      status(result) shouldBe Status.BAD_REQUEST
    }

    "return 400 if phone number is missing" in {
      val fakeRequestWithFormData = fakeRequest
        .withFormUrlEncodedBody(
          "firstName"               -> "John",
          "lastName"                -> "Doe",
          "email"                   -> "john.doe@hmrc.com",
          "nationalInsuranceNumber" -> "AB123456C",
          "hasRelatedCase"          -> "false"
        )
      val result = controller.onSubmit()(fakeRequestWithFormData)
      status(result) shouldBe Status.BAD_REQUEST
    }

    "return 400 if nationalInsuranceNumber is missing" in {
      val fakeRequestWithFormData = fakeRequest
        .withFormUrlEncodedBody(
          "firstName"      -> "John",
          "lastName"       -> "Doe",
          "email"          -> "john.doe@hmrc.com",
          "phoneNumber"    -> "07777777777",
          "hasRelatedCase" -> "false"
        )
      val result = controller.onSubmit()(fakeRequestWithFormData)
      status(result) shouldBe Status.BAD_REQUEST
    }

    "return 400 if hasRelatedCase is missing" in {
      val fakeRequestWithFormData = fakeRequest
        .withFormUrlEncodedBody(
          "firstName"               -> "John",
          "lastName"                -> "Doe",
          "email"                   -> "john.doe@hmrc.com",
          "phoneNumber"             -> "07777777777",
          "nationalInsuranceNumber" -> "AB123456C"
        )
      val result = controller.onSubmit()(fakeRequestWithFormData)
      status(result) shouldBe Status.BAD_REQUEST
    }
  }
}
