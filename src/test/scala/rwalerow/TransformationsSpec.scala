package rwalerow

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._
import scala.concurrent.Await

class TransformationsSpec extends WordSpec with Matchers with Transformations {

  "Extract and transform entries" should {

    implicit val system = ActorSystem("test")
    implicit val materializer = ActorMaterializer()

    "transform single entry" in {
      val document = JsoupBrowser().parseInputStream(getClass().getResourceAsStream("/single-entry.html"))

      val futureResult = Source.single(document)
        .via(extractAndTransformEntries)
        .runWith(Sink.fold("")(_ + _))

      val result = Await.result(futureResult, 2.seconds)

      result shouldBe "{\n  \"id\": 4862636,\n  \"points\": 10,\n  \"content\": \"Testowa pierwsza wiadomosc\"\n}"
    }

    "not transform invalid entry" in {
      val document = JsoupBrowser().parseInputStream(getClass().getResourceAsStream("/invalid-single-entry.html"))

      val futureResult = Source.single(document)
        .via(extractAndTransformEntries)
        .runWith(Sink.fold(List[String]())(_ ++ List(_)))

      val result = Await.result(futureResult, 2.seconds)

      result shouldBe List()
    }
  }

}
