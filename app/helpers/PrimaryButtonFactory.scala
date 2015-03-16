package helpers

import scala.xml.Elem

/**
 * Created by Ravis on 27/02/15.
 */
class PrimaryButtonFactory extends ButtonFactory {
  def createSmallButton(text: String): Elem = <a class="small button">{text}</a>
  def createNormalButton(text: String): Elem = <a class="normal button">{text}</a>
  def createLargeButton(text: String): Elem = <a class="large button">{text}</a>
}
