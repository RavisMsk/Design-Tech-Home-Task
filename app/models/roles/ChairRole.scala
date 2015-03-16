package models.roles

/**
 * Created by Ravis on 23/02/15.
 */

sealed trait ChairRole {
  val value: String
  override def toString = this.value
}
protected class RoleImpl(val value: String) extends ChairRole
case object Administrator extends RoleImpl("Admin")
case object Teacher extends RoleImpl("teacher")
case object Student extends RoleImpl("Student")