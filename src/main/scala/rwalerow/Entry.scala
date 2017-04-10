package rwalerow

import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.element
import org.apache.commons.lang3.StringEscapeUtils.escapeJava

import scala.util.Try

case class Entry(id: Long, points: Long, content: String)

object Entry {

  val idClass = ".qid"
  val pointsClass = ".points"
  val bodyClass = ".post-body"

  def fromElement(e: Element): Option[Entry] = {
    def stringPart(s: String): String = (e >> element(s)).innerHtml
    Try(
      Entry(
        clearStringFromDash(stringPart(idClass)).toLong,
        clearStringFromDash(stringPart(pointsClass)).toLong,
        escapeJava(stringPart(bodyClass))
      )
    ).toOption
  }

  private def clearStringFromDash(str: String): String =
    if(str.size > 1 && str.charAt(0) == '#') str.substring(1)
    else str
}