package controllers

/**
 * Created by Ravis on 06/04/15.
 */

import play.api.mvc._
import models.db.{SearchQueries, SearchResult}

object SearchController extends Controller {
  def search(title: Option[String], disc: Option[String], fio: Option[String]) = Action {
    val result: List[SearchResult] = SearchQueries.queryWithParams(title, disc, fio)
    Ok(views.html.Search.search(result, title.getOrElse(""), disc.getOrElse(""), fio.getOrElse("")))
  }
}
