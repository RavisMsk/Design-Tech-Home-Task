package models.teacher

import Grade._
import Job._

import anorm._
import play.api.db.DB
import play.api.Play.current

/**
 * Created by Ravis on 23/02/15.
 */
class Teacher(val tid: Long, val grade: Grade, val job: Job) {

  def update(grade: Grade = this.grade, job: Job = this.job): Option[Teacher] = {
    DB.withConnection {
      implicit conn =>
        val result = SQL(
          "update \"Teachers\""+
          "set grade={grade}, job={job}"+
          "where tid={tid}"
        ).on("grade" -> grade.toString, "job" -> job.toString, "tid" -> this.tid).executeUpdate()
        if (result == 0) None
        else Some(new Teacher(this.tid, grade, job))
    }
  }

  def insert(): Option[Teacher] = {
    DB.withConnection {
      implicit conn =>
        val tid: Option[Long] =
          SQL(
          "insert into \"Teachers\"(grade, job) values({grade}, {job})"
          ).on("grade" -> this.grade.toString, "job" -> this.job.toString).executeInsert()
        if (tid.isDefined) Some(new Teacher(tid.get, this.grade, this.job))
        else None
    }
  }

  def delete(): Boolean = {
    DB.withConnection {
      implicit conn =>
        val result =
          SQL("delete from \"Teachers\" where tid={tid}")
            .on("tid" -> this.tid)
            .executeUpdate()
        if (result == 0) false
        else true
    }
  }

}

object Teacher {

  def apply(tid: Long, grade: Grade, job: Job): Teacher =
    new Teacher(tid, grade, job)

  def Row2Teacher(row: Row): Teacher =
    this(
      row[Long]("tid"),
      row[String]("grade"),
      row[String]("job")
    )

  implicit def String2Grade(str: String): Grade = str match {
    case "Candidate" => Candidate
    case "Doctor" => Doctor
  }

  implicit def String2Job(str: String): Job = str match {
    case "Assistant" => Assistant
    case "AssocProf" => AssociateProfessor
    case "Professor" => Professor
  }

  def find(tid: Long): Option[Teacher] =
    DB.withConnection {
      implicit conn =>
        SQL("select * from \"Teachers\" where tid={tid}")
          .on("tid" -> tid).apply().headOption.map(Row2Teacher _)
    }

  def findAll: List[Teacher] =
    DB.withConnection {
      implicit conn =>
        SQL("select * from \"Teachers\"")().map(Row2Teacher _).toList
    }
}