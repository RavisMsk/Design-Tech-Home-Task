package models.teacher

/**
 * Created by Ravis on 23/02/15.
 */
object Job extends Enumeration {
  type Job = Value

  val Assistant = Value("Assistant")
  val AssociateProfessor = Value("AssocProf")
  val Professor = Value("Professor")

  def allValues = List("Assistant", "AssocProf", "Professor")
}
