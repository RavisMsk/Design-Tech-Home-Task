package models.db

/**
 * Created by Ravis on 07/04/15.
 */

import models.upload.Upload
import models.user.User
import models.discipline.Discipline

import anorm._
import play.api.db.DB
import play.api.Play.current

case class SearchResult(filename: String,
                        title: String,
                        authors: String,
                        publisher: String,
                        email: String,
                        discipline: String)

object SearchQueries {
  private def Row2SearchResult(row: Row): SearchResult = {
    SearchResult(
      row[String]("filename"),
      row[String]("Upload.title"),
      row[String]("author"),
      row[String]("name"),
      row[String]("email"),
      row[String]("Discipline.title")
    )
  }

  def queryWithParams(title: Option[String], disc: Option[String], fio: Option[String]): List[SearchResult] = {
    var query = "select \"filename\",\"Upload\".\"title\",\"author\",\"name\",\"email\",\"Discipline\".\"title\" " +
      "from \"Upload\", \"Users\", \"Discipline\" " +
      "where \"Upload\".\"tid\"=\"Users\".\"uid\" and \"Upload\".\"did\"=\"Discipline\".\"did\""
    if (title.isDefined) {
      val titleValue = title.get
      query = query + s""" and "Upload"."title" ilike '%$titleValue%'"""
    }
    if (disc.isDefined) {
      val discValue = disc.get
      query = query + s""" and "Discipline"."title" ilike '%$discValue%'"""
    }
    if (fio.isDefined) {
      val fioValue = fio.get
      query = query + s""" and "name" ilike '%$fioValue%'"""
    }
    DB.withConnection {
      implicit conn =>
        SQL(query)().map(Row2SearchResult _).toList
    }
  }
}
