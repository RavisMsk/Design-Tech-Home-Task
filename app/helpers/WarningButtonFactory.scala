package helpers

import scala.xml.Elem

/**
 * Created by Ravis on 27/02/15.
 */
class WarningButtonFactory extends ButtonFactory {
  def createSmallButton(text: String): Elem = <a class="warning small button">{text}</a>
  def createNormalButton(text: String): Elem = <a class="warning normal button">{text}</a>
  def createLargeButton(text: String): Elem = <a class="warning large button">{text}</a>
}
