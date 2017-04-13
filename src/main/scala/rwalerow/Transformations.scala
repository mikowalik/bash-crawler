package rwalerow

import java.nio.file.Paths

import akka.NotUsed
import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Flow, Keep, Sink, Source}
import akka.util.ByteString
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Document
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.elementList
import spray.json._

import scala.concurrent.Future

trait Transformations extends JsonProtocol {

  def pagesIds(last: Int): Source[Int, NotUsed] =
    Source(1 to last)

  def downloadPage(browser: JsoupBrowser, bashLink: String): Flow[Int, Document, NotUsed] =
    Flow[Int]
      .map(s => bashLink + s)
      .map(link => browser.get(link))

  val extractAndTransformEntries: Flow[Document, String, NotUsed] =
    Flow[Document]
      .mapConcat(_ >> elementList(".post"))
      .map(Entry.fromElement)
      .filter(_.nonEmpty)
      .map(_.toJson)
      .map(_.prettyPrint)

  def lineSink(filename: String): Sink[String, Future[IOResult]] =
    Flow[String]
      .map(s => ByteString(s + "\n"))
      .toMat(FileIO.toPath(Paths.get(filename)))(Keep.right)
}
