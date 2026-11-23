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

class ThreadDetailsControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .build()

  private val controller =
    app.injector.instanceOf[ThreadDetailsController]

  "onPageLoad" should {
    "return 200" in {
      val request = FakeRequest("GET", "/create-thread/thread-details")
      val result  = controller.onPageLoad(request)

      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val request = FakeRequest("GET", "/create-thread/thread-details")
      val result  = controller.onPageLoad(request)

      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "onSubmit" should {
    "return BAD_REQUEST when the form is invalid" in {
      val request = FakeRequest("POST", "/create-thread/thread-details").withFormUrlEncodedBody()
      val result  = controller.onSubmit(request)

      status(result) shouldBe Status.BAD_REQUEST
    }

    "redirect to Check Your Answers when the form is valid" in {
      val request =
        FakeRequest(
          "POST",
          "/create-thread/thread-details"
        ).withFormUrlEncodedBody(
          "message"            -> "Test message",
          "responseDate.day"   -> "1",
          "responseDate.month" -> "12",
          "responseDate.year"  -> "2099"
        )

      val result = controller.onSubmit(request)

      status(result) shouldBe Status.SEE_OTHER

      redirectLocation(result) shouldBe Some(
        routes.CheckYourAnswersController.onPageLoad().url
      )
    }
  }

}
