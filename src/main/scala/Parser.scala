import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import rwalerow.{Entry, JsonProtocol}
import spray.json._

object Parser extends App with JsonProtocol {

  val browser = JsoupBrowser()
  val document = browser.get("http://bash.org.pl/latest/")
  val entries = document >> elementList(".post")

  entries
      .flatMap(Entry.fromElement)
      .map(_.toJson)
    .foreach(x => println(x))

}
