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

import uk.gov.hmrc.sdecstridefrontend.forms.ThreadTypeFormProvider
import uk.gov.hmrc.sdecstridefrontend.models.ThreadType
import uk.gov.hmrc.sdecstridefrontend.views.html.createthread.ThreadTypeView
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.*
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class ThreadTypeController @Inject() (
  override val messagesApi: MessagesApi,
  formProvider:             ThreadTypeFormProvider,
  mcc:                      MessagesControllerComponents,
  threadTypeView:           ThreadTypeView
) extends FrontendController(mcc)
    with I18nSupport
    with Logging {

  private def form(implicit request: RequestHeader): Form[ThreadType] = {
    given Messages = messagesApi.preferred(request)
    formProvider()
  }

  def onPageLoad: Action[AnyContent] =
    Action: request =>
      given Request[AnyContent] = request
      Ok(threadTypeView(form))

  def onSubmit: Action[AnyContent] =
    logger.info(s"Handling Thread Type Submission")
    Action.async: request =>
      given Request[AnyContent] = request
      processForm()

  private def processForm()(using Request[AnyContent]): Future[Result] =
    form
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(
            BadRequest(threadTypeView(formWithErrors))
          ),
        threadType => Future.successful(Redirect(routes.ThreadTypeController.onPageLoad()))
      )
}
