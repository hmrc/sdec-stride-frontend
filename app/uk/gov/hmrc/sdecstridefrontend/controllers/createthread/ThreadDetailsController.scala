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

import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.*
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.sdecstridefrontend.forms.ThreadDetailsFormProvider
import uk.gov.hmrc.sdecstridefrontend.models.RecipientDetails
import uk.gov.hmrc.sdecstridefrontend.views.html.createthread.ThreadDetailsView

import javax.inject.{Inject, Singleton}

@Singleton
class ThreadDetailsController @Inject() (
  override val messagesApi:  MessagesApi,
  threadDetailsFormProvider: ThreadDetailsFormProvider,
  mcc:                       MessagesControllerComponents,
  threadDetailsView:         ThreadDetailsView
) extends FrontendController(mcc)
    with I18nSupport
    with Logging {

  private def recipientFromSession(using request: Request[AnyContent]): RecipientDetails =
    RecipientDetails(
      firstName = request.session.get("firstName").getOrElse(""),
      lastName = request.session.get("lastName").getOrElse(""),
      email = request.session.get("email").getOrElse(""),
      phoneNumber = request.session.get("phoneNumber").getOrElse(""),
      nationalInsuranceNumber = request.session.get("nationalInsuranceNumber").getOrElse(""),
      hasRelatedCase = request.session.get("hasRelatedCase").exists(_.toBoolean),
      caseReferenceNumber = request.session.get("caseReferenceNumber")
    )

  def onPageLoad: Action[AnyContent] =
    Action { implicit request =>
      val form      = threadDetailsFormProvider()
      val recipient = recipientFromSession

      Ok(threadDetailsView(form, recipient))
    }

  def onSubmit: Action[AnyContent] =
    Action { implicit request =>
      val form      = threadDetailsFormProvider()
      val recipient = recipientFromSession

      form
        .bindFromRequest()
        .fold(
          formWithErrors => BadRequest(threadDetailsView(formWithErrors, recipient)),
          _ =>
            Redirect(
              routes.CheckYourAnswersController.onPageLoad()
            )
        )
    }
}
