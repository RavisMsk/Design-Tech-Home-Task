package controllers

import play.api.mvc._
import jp.t2v.lab.play2.auth.OptionalAuthElement

import models.user.User

object Application extends Controller with OptionalAuthElement with AuthConfigImpl {

  def index = StackAction {
    implicit request =>
      Ok(views.html.Application.index(User.findAll, loggedIn))
  }

}