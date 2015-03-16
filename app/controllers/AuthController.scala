package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import jp.t2v.lab.play2.auth.LoginLogout

import models.user.User

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by Ravis on 24/02/15.
 */

object AuthController extends Controller with LoginLogout with AuthConfigImpl {

  val loginForm = Form {
    mapping("email" -> text, "password" -> text(maxLength = 30))(User.authenticate)(_.map(u => (u.email, "")))
      .verifying("Неверный email или пароль", result => result.isDefined)
  }

  def signIn = Action {
    implicit request =>
      Ok(views.html.Auth.signin(loginForm))
  }

  def authenticate = Action.async {
    implicit request =>
      loginForm.bindFromRequest.fold(
        formWithErrors => Future.successful(BadRequest("Не удалось войти в систему")),
        user => gotoLoginSucceeded(user.get.uid.get)
      )
  }

//  private val signUpForm = Form{
//    mapping(
//      "email" -> email,
//      "password" -> text(maxLength = 30),
//      "name" -> nonEmptyText(4, 30)
//    )
//  }

  def signUp = TODO

  def signOut = Action.async {
    implicit request =>
      gotoLogoutSucceeded
  }

}
