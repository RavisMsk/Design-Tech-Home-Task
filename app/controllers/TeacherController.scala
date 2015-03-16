package controllers

import models.user.{User, UserData}
import models.teacher.{Grade, Job, Teacher}
import models.teacher.Grade._
import models.teacher.Job._
import models.discipline.Discipline
import models.upload.{Upload, UploadData}
import models.roles

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import jp.t2v.lab.play2.auth.AuthElement

/**
 * Created by Ravis on 23/02/15.
 */

object TeacherController extends Controller with AuthElement with AuthConfigImpl {

  def index = StackAction(AuthorityKey -> roles.Teacher) {
    implicit request =>
      loggedIn.role match {
        case roles.Teacher => Redirect(routes.TeacherController.teacher(loggedIn.uid.get))
        case _ => Redirect(routes.Application.index())
      }
  }

  private def viewAsTeacher(user: User) = {
    if (user.role == roles.Teacher)
      Ok(views.html.TeacherController.teacher(user, user.getAssociatedRoleRecord))
    else
      Forbidden("Нет доступа к чужому аккаунту")
  }

  private def viewAsAdmin(tid: Long) = {
    User.find(tid) match {
      case Some(user) if user.role == roles.Teacher => Ok(views.html.TeacherController.teacher(user, user.getAssociatedRoleRecord))
      case _ => NotFound("Пользователь не найден")
    }
  }

  def teacher(tid: Long) = StackAction(AuthorityKey -> roles.Teacher) {
    implicit request =>
      loggedIn.role match {
        case roles.Teacher if loggedIn.uid.get == tid => viewAsTeacher(loggedIn)
        case roles.Administrator => viewAsAdmin(tid)
        case roles.Teacher if loggedIn.active == false => Forbidden(views.html.errors.forbidden(Some("Аккаунт пока не активирован. Обратитесь к администратору.")))
        case _ => Forbidden(views.html.errors.forbidden(None))
      }
  }

  private def updateAsAdmin(tid: Long, newData: UserData) = TODO

  private def updateAsOwner(user: User, teacher: Teacher, newData: UserData) = {
    user.update(name = newData.name, dob = newData.dob) match {
      case None => InternalServerError("Не удалось обновить профиль пользователя")
      case _ =>
    }
    val grade = newData match {
      case UserData(_, _, "Candidate", _) => Candidate
      case UserData(_, _, "Doctor", _) => Doctor
    }
    val job = newData match {
      case UserData(_, "Assistant", _, _) => Assistant
      case UserData(_, "AssocProf", _, _) => AssociateProfessor
      case UserData(_, "Professor", _, _) => Professor
    }
    teacher.update(grade, job) match {
      case None => InternalServerError("Не удалось обновить профиль преподавателя")
      case _ =>
    }
    Redirect(routes.TeacherController.teacher(user.uid.get))
  }

  private val updateForm = Form(
  mapping(
  "name" -> nonEmptyText(4, 30),
  "job" -> nonEmptyText,
  "grade" -> nonEmptyText,
  "dob" -> date
  )(UserData.apply)(UserData.unapply)
  )

  def updateTeacher(tid: Long) = StackAction(AuthorityKey -> roles.Teacher) {
    implicit request =>
      updateForm.bindFromRequest.fold({
        formWithErrors =>
          BadRequest("Bad update data")
      }, {
        userData => {
          loggedIn.role match {
            case roles.Administrator => Forbidden("Не мое дело")
            case roles.Teacher => updateAsOwner(loggedIn, loggedIn.getAssociatedRoleRecord, userData)
            case _ => Forbidden(views.html.errors.forbidden())
          }
        }
      })
  }

  def uploads(tid: Long) = StackAction(AuthorityKey -> roles.Teacher) {
    implicit request =>
      Ok(views.html.TeacherController.uploads(loggedIn, Upload.findByTeacherId(loggedIn.uid.get), Discipline.findAll))
  }

  def upload(tid: Long) = StackAction(AuthorityKey -> roles.Teacher) {
    implicit request =>
      Ok(views.html.TeacherController.uploadMaterial(loggedIn, Discipline.findAll, uploadForm))
  }

  private val uploadForm = Form(
    mapping(
      "title" -> nonEmptyText(4, 40),
      "author" -> nonEmptyText(5, 40),
      "discipline" -> nonEmptyText
    )(UploadData.apply)(UploadData.unapply)
  )

  def performUpload(tid: Long) = Action(parse.multipartFormData) {
    implicit request =>
      uploadForm.bindFromRequest().fold(
      formWithErrors => {
        BadRequest("Form errors: " + formWithErrors)
      },
      uploadData => {
        request.body.file("upload").map {
          material =>
            Upload(uploadData.title, uploadData.author, tid, uploadData.discipline.toLong, material.filename).insert() match {
              case None => InternalServerError("Something went wrong")
              case Some(upload) => {
                import java.io.File
                val filename = upload.filePath + material.filename
                material.ref.moveTo(new File(s"/Users/Ravis/TPk_Uploads/$filename"))
                Redirect(routes.TeacherController.uploads(tid))
              }
            }
        }.getOrElse {
          BadRequest("Select file plz")
        }
      }
      )
  }

}
