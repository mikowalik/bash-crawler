import akka.actor.ActorSystem

import scala.util.Success
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import com.typesafe.config.ConfigFactory
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import rwalerow.Transformations

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Try}

object Parser extends Transformations {

  def main(args: Array[String]): Unit = {

    if(args.length > 1) println("You should pass only one argument")
    else if(args.length == 0) {
      println("You didn't pass any arguments")
      println("Please try one more time")
    }
    else {

      Try(args(0).toInt) match {
        case Success(n) if n < 1 => {
          println(s"Value passed($n) is 0 or lower then 0")
          println("Please pass positive value")
        }
        case Success(n) => {
          println(s"Running parser for $n latest pages")
          runParsing(n)
        }
        case Failure(err) => {
          println(s"Unable to parse ${args(0)} value to number")
          println(s"Got error: $err")
          println("Please try one more time")
        }
      }
    }
  }

  def runParsing(n: Int = 5): Unit = {
    val browser = JsoupBrowser()
    val config = ConfigFactory.load()
    val outputFile = config.getString("parser.output.file")
    val bashLink = config.getString("parser.link")

    implicit val system = ActorSystem("BashParser")
    implicit val materializer = ActorMaterializer()

    val runnable =
      (pagesIds(n) via downloadPage(bashLink) via extractAndTransformEntries())
        .toMat(lineSink(outputFile))(Keep.right)

    runnable.run().onComplete(_ => {
      println("finished")
      system.terminate()
    })
  }
}
