package controllers

import play.api.mvc._

import jp.t2v.lab.play2.auth.AuthElement

import models.roles
import models.upload.Upload

/**
 * Created by Ravis on 23/02/15.
 */
object UploadController extends Controller with AuthElement with AuthConfigImpl{

  def uploadWithId(uid: Long) = TODO

  def deleteUploadWithId(uid: Long) = StackAction(AuthorityKey -> roles.Teacher) {
    implicit request =>
      Upload.find(uid) match {
        case None => NotFound("Методический материал с таким номером не найден")
        case Some(upload) if upload.teacherId == loggedIn.uid.get => {
          if (upload.delete()) Redirect(routes.TeacherController.uploads(loggedIn.uid.get))
          else InternalServerError("Не удалось удалить файл")
        }
        case _ => Forbidden("Недостаточно прав для удаления этого методического материала")
      }
  }

}
