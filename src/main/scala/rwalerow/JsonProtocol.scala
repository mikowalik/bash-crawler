package rwalerow

import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait JsonProtocol extends DefaultJsonProtocol {
  implicit val entryProtocol: RootJsonFormat[Entry] = jsonFormat3(Entry.apply)
}

