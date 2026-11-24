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

class ThreadTypeControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .build()

  private val controller = app.injector.instanceOf[ThreadTypeController]

  "onPageLoad" should {

    "return 200" in {
      val result =
        controller.onPageLoad(
          FakeRequest("GET", "/create-thread/thread-type")
        )

      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result =
        controller.onPageLoad(
          FakeRequest("GET", "/create-thread/thread-type")
        )

      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "onSubmit" should {

    "return BAD_REQUEST when no thread type is selected" in {
      val result =
        controller.onSubmit(
          FakeRequest("POST", "/create-thread/thread-type")
        )

      status(result) shouldBe Status.BAD_REQUEST
    }

    "redirect to ThreadDetails when Request is selected" in {
      val result =
        controller.onSubmit(
          FakeRequest("POST", "/create-thread/thread-type")
            .withFormUrlEncodedBody(
              "threadType" -> "Request"
            )
        )

      status(result) shouldBe Status.SEE_OTHER

      redirectLocation(result) shouldBe Some(
        routes.ThreadDetailsController.onPageLoad().url
      )
    }

    "redirect back to ThreadType when Send is selected" in {
      val result =
        controller.onSubmit(
          FakeRequest("POST", "/create-thread/thread-type")
            .withFormUrlEncodedBody(
              "threadType" -> "Send"
            )
        )

      status(result) shouldBe Status.SEE_OTHER

      redirectLocation(result) shouldBe Some(
        routes.ThreadTypeController.onPageLoad().url
      )
    }

    "redirect back to ThreadType when SendAndRequest is selected" in {
      val result =
        controller.onSubmit(
          FakeRequest("POST", "/create-thread/thread-type")
            .withFormUrlEncodedBody(
              "threadType" -> "SendAndRequest"
            )
        )

      status(result) shouldBe Status.SEE_OTHER

      redirectLocation(result) shouldBe Some(
        routes.ThreadTypeController.onPageLoad().url
      )
    }
  }
}
