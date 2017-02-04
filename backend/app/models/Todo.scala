package models

import play.api.libs.json.Json

case class Todo(
  id:        Option[Nat]         = None,
  keyword:   String,
  headline:  String,
  scheduled: Option[IsoDateTime] = None,
  deadline:  Option[IsoDateTime] = None,
  notes:     Option[String]      = None
)

object Todo {
  implicit val jsonFormatInstance = Json.format[Todo]
}
