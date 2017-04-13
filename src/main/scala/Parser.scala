import java.nio.file.Paths

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl.{FileIO, Source}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import rwalerow.{Entry, JsonProtocol}
import spray.json._
import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.util.ByteString

import scala.concurrent._
import scala.concurrent.duration._
import java.nio.file.Paths

import akka.stream._
import akka.stream.scaladsl._
import com.typesafe.config.ConfigFactory
import net.ruippeixotog.scalascraper.model.Document

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Parser extends App with JsonProtocol {

  val bashLink = "http://bash.org.pl/latest/?page="
  val browser = JsoupBrowser()
  val config = ConfigFactory.load()
  val outputFile = config.getString("parser.output.file")

  implicit val system = ActorSystem("BashParser")
  implicit val materializer = ActorMaterializer()

  val n = 3

  val runnable =
    (pagesIds(n) via downloadPage(bashLink) via extractAndTransformEntries())
      .toMat(lineSink(outputFile))(Keep.right)

  runnable.run().onComplete(_ => {
    println("done")
    system.terminate()
  })

  def pagesIds(last: Int): Source[Int, NotUsed] =
    Source(1 to last)

  def downloadPage(bashLink: String): Flow[Int, Document, NotUsed] =
    Flow[Int]
    .map(s => bashLink + s)
    .map(link => JsoupBrowser().get(link))

  def extractAndTransformEntries(): Flow[Document, String, NotUsed] =
    Flow[Document]
      .mapConcat(_ >> elementList(".post"))
      .map(Entry.fromElement)
      .map(_.toJson)
      .map(_.prettyPrint)

  def lineSink(filename: String): Sink[String, Future[IOResult]] =
    Flow[String]
      .map(s => ByteString(s + "\n"))
      .toMat(FileIO.toPath(Paths.get(filename)))(Keep.right)
}
