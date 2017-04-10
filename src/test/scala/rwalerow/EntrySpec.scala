package rwalerow

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.elementList
import org.scalatest.{Matchers, WordSpec}


class EntrySpec extends WordSpec with Matchers {

  val browser = JsoupBrowser()
  val document = browser.parseInputStream(getClass().getResourceAsStream("/bash.html"))

  "Entry" should {
    "properly get one element from html" in {

      val posts = document >> elementList(".post")
      val expected = Entry(4862636, -8, "Testowa pierwsza wiadomosc")

      posts.size should be > 1
      Entry.fromElement(posts(0)) should contain (expected)
    }

    "fallback to None with invalid posts count" in {
      val posts = document >> elementList(".post")

      posts.size should be > 2
      Entry.fromElement(posts(1)) shouldBe empty
    }
  }

}
