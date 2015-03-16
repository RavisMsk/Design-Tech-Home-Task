package models.upload

import anorm._
import play.api.db.DB
import play.api.Play.current

/**
 * Created by Ravis on 23/02/15.
 */

class Upload(val uid: Option[Long], val title: String, val author: String, val teacherId: Long, val disciplineId: Long, val filename: String) {

  val filePath: String = uid.getOrElse("NotInsertedFile")+"_"+teacherId+"_"+disciplineId

  def update(title: String = this.title, author: String = this.author, disciplineId: Long = this.disciplineId): Option[Upload] = {
    DB.withConnection {
      implicit conn =>
        val result = SQL(
          "update \"Upload\" " +
            "set title={title}, author={author}, did={did}" +
            "where uid={uid}"
        ).on("uid" -> this.uid.get, "title" -> title, "author" -> author, "did" -> disciplineId).executeUpdate()
        if (result == 0) None
        else Some(new Upload(this.uid, title, author, this.teacherId, disciplineId, this.filename))
    }
  }

  def insert(): Option[Upload] = {
    DB.withConnection {
      implicit conn =>
        val uid: Option[Long] =
          SQL("insert into \"Upload\"(title, author, tid, did, filename) values({title}, {author}, {tid}, {did}, {filename})")
            .on("title" -> this.title, "author" -> this.author, "tid" -> this.teacherId, "did" -> this.disciplineId, "filename" -> this.filename)
            .executeInsert()
        Some(new Upload(uid, this.title, this.author, this.teacherId, this.disciplineId, this.filename))
    }
  }

  def delete(): Boolean = {
    DB.withConnection {
      implicit conn =>
        val result =
          SQL("delete from \"Upload\" where uid={uid}")
            .on("uid" -> this.uid)
            .executeUpdate()
        if (result == 0) false
        else true
    }
  }

}

object Upload {
  def apply(title: String, author: String, teacherId: Long, disciplineId: Long, filename: String): Upload =
    new Upload(None, title, author, teacherId, disciplineId, filename)

  def apply(uid: Option[Long], title: String, author: String, teacherId: Long, disciplineId: Long, filename: String): Upload =
    new Upload(uid, title, author, teacherId, disciplineId, filename)

  def Row2Upload(row: Row): Upload =
    this(
    Some(row[Long]("uid")),
    row[String]("title"),
    row[String]("author"),
    row[Long]("tid"),
    row[Long]("did"),
    row[String]("filename")
    )

  def find(uid: Long): Option[Upload] =
    DB.withConnection {
      implicit conn =>
        SQL(
          "select \"uid\", \"title\", \"author\", \"tid\", \"did\", \"filename\" " +
          "from \"Upload\" " +
          "where uid={uid}"
        ).on("uid" -> uid).apply().headOption.map(Row2Upload _)
    }

  def findByTeacherId(tid: Long): List[Upload] =
    DB.withConnection {
      implicit conn =>
        SQL(
          "select \"uid\", \"title\", \"author\", \"tid\", \"did\", \"filename\" " +
            "from \"Upload\" " +
          "where tid={tid}"
        ).on("tid" -> tid).apply().map(Row2Upload _).toList
    }

  def findAll: List[Upload] =
    DB.withConnection {
      implicit conn =>
        SQL(
        "select \"uid\", \"title\", \"author\", \"tid\", \"did\", \"filename\" " +
        "from \"Upload\""
        ).apply().map(Row2Upload _).toList
    }
}
