package models.teacher

/**
 * Created by Ravis on 23/02/15.
 */
object Grade extends Enumeration {
  type Grade = Value

  val Candidate = Value("Candidate")
  val Doctor = Value("Doctor")

  def allValues = List("Candidate", "Doctor")
}
