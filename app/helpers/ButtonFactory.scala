package helpers

import ButtonType._

import scala.xml.Elem

/**
 * Created by Ravis on 27/02/15.
 */

abstract class ButtonFactory {
  def createSmallButton(text: String): Elem
  def createNormalButton(text: String): Elem
  def createLargeButton(text: String): Elem
}

object ButtonFactory {
  def getFactory(factoryType: ButtonType): ButtonFactory =
    factoryType match {
      case ButtonType.primary => new PrimaryButtonFactory
      case ButtonType.warning => new WarningButtonFactory
    }
}