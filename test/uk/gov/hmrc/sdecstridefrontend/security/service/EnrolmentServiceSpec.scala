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

package uk.gov.hmrc.sdecstridefrontend.security.service



import org.scalatest.OptionValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import play.api.Configuration
import uk.gov.hmrc.sdecstridefrontend.config.EnrollmentConfig
import uk.gov.hmrc.sdecstridefrontend.infrastructure.ApplicationConfigBuilder
import uk.gov.hmrc.sdecstridefrontend.models.TestStaff

class EnrolmentServiceSpec extends AnyFunSuite with Matchers with ScalaFutures with ApplicationConfigBuilder with OptionValues {
  val conf = Map[String, Any](
    "stride.accessPrefixMatch" -> "SDEC_"
  )
  val config = EnrollmentConfig(Configuration.from(conf))
  val sut = new EnrolmentService(config)

  test("verify a staff with sufficient enrollments") {
    sut.verifyEnrollment(TestStaff.standardStaff) shouldBe true
  }

  test("fail to verify a staff with no sdec") {
    sut.verifyEnrollment(TestStaff.insufficientRoleStaff) shouldBe false
  }

  test("should extract staff") {
    val staff = sut.extractStaff(
      TestStaff.standardStaff.enrolments,
      Some(TestStaff.standardStaff.email.value),
      Some(TestStaff.standardStaff.credentials),
      Some(TestStaff.standardStaff.name)
    )

    staff.value shouldBe TestStaff.standardStaff
  }

  test("should return none when one of the values does not exist") {
    val staff = sut.extractStaff(
      TestStaff.standardStaff.enrolments,
      Some(TestStaff.standardStaff.email.value),
      Some(TestStaff.standardStaff.credentials),
      None
    )

    staff shouldBe None
  }
}
