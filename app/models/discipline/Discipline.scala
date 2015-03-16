package models.discipline

/**
 * Created by Ravis on 23/02/15.
 */

import anorm._
import play.api.db.DB
import play.api.Play.current

class Discipline(val did: Long, val title: String)

object Discipline {
  def apply(did: Long, title: String): Discipline =
    new Discipline(did, title)

  def Row2Discipline(row: Row): Discipline =
    this(
    row[Long]("did"),
    row[String]("title")
    )

  def find(did: Long): Option[Discipline] =
    DB.withConnection {
      implicit conn =>
        SQL(
        "select * from \"Discipline\" " +
          "where \"did\"={did}"
        ).on("did" -> did).apply().headOption.map(Row2Discipline _)
    }

  def findAll: List[Discipline] =
    DB.withConnection {
      implicit conn =>
        SQL(
        "select * from \"Discipline\""
        ).apply().map(Row2Discipline _).toList
    }
}