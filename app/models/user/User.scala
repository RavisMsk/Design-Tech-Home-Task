package models.user

/**
 * Created by Ravis on 22/02/15.
 */

import models.roles
import models.teacher.Teacher

import anorm._
import play.api.db.DB
import play.api.Play.current

class User(val uid: Option[Long], val name: String, val dob: java.util.Date, val email: String, val password: String, val role: roles.ChairRole, val active: Boolean) {

  override def toString = "User(%d, %s, %s, %s)".format(uid.getOrElse(0), name, email, password)

  def Date2String(date: java.util.Date): String = "%d-%d-%d".format(date.getYear, date.getMonth, date.getDay)
  def Date2SqlDate(date: java.util.Date): java.sql.Date = new java.sql.Date(date.getTime)

  def update(name: String = this.name, password: String = this.password, dob: java.util.Date = this.dob, email: String = this.email): Option[User] = {
    DB.withConnection {
      implicit conn =>
        val result = SQL(
          "update \"Users\" set name={name}, dob={dob}, email={email}, password={password} "+
            "where uid={uid}"
        ).on("uid" -> this.uid.get, "name" -> name, "dob" -> dob, "email" -> email, "password" -> password).executeUpdate()
        if (result == 0) None
        else Some(new User(this.uid, name, dob, email, password, this.role, false))
    }
  }

  def insert(): Option[User] = {
    DB.withConnection {
      implicit conn =>
        val uid: Option[Long] =
          SQL("insert into \"Users\"(name, dob, email, password, role, active) values({name}, {dob}, {email}, {password}, {role}, false)")
          .on("name" -> this.name, "dob" -> Date2String(this.dob), "email" -> this.email, "role" -> this.role.toString, "password" -> this.password)
          .executeInsert()
        Some(new User(uid, this.name, this.dob, this.email, this.password, this.role, false))
    }
  }

  def delete(): Boolean = {
    DB.withConnection {
      implicit conn =>
        val result =
          SQL("delete from \"Users\" where uid={uid}")
          .on("uid" -> this.uid)
          .executeUpdate()
        if (result == 0) false
        else true
    }
  }

  def getAssociatedRoleRecord: Teacher = Teacher.find(this.uid.get).get

}

object User {
  def apply(name: String, dob: java.util.Date, email: String, password: String, role: roles.ChairRole): User =
    new User(None, name, dob, email, password, role, false)

  def apply(uid: Option[Long], name: String, dob: java.util.Date, email: String, password: String, role: roles.ChairRole): User =
    new User(uid, name, dob, email, password, role, false)

  def authenticate(email: String, password: String): Option[User] = {
    DB.withConnection {
      implicit conn =>
        User.findByEmail(email).filter(user => user.password == password && user.role == roles.Teacher)
    }
  }

  def find(uid: Long): Option[User] =
    DB.withConnection {
      implicit conn =>
        SQL(
          "select * from \"Users\" where uid={uid}"
        ).on("uid" -> uid).apply().headOption.map(Row2User _)
    }

  def findByEmail(email: String): Option[User] =
    DB.withConnection {
      implicit conn =>
        SQL(
        "select * from \"Users\"" +
          "where \"email\"={email}"
        ).on("email" -> email).apply().headOption.map(Row2User _)
    }

  def findAll: List[User] = {
    DB.withConnection {
      implicit conn =>
        SQL("select * from \"Users\"")().map(Row2User _).toList
    }
  }

  def Row2User(row: Row): User =
    this(
      Some(row[Long]("uid")),
      row[String]("name"),
      row[java.util.Date]("dob"),
      row[String]("email"),
      row[String]("password"),
      row[String]("role")
    )

  implicit def SqlDate2Date(date: java.sql.Date): java.util.Date = new java.util.Date(date.getTime)

  implicit def String2ChairRole(stringRole: String): roles.ChairRole = stringRole match {
    case "Admin" => roles.Administrator
    case "Teacher" => roles.Teacher
    case "Student" => roles.Student
  }

}
