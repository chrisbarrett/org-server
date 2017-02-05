package models

import play.api.libs.json.Json

case class ApiMessage(message: String, statusCode: Int)

object ApiMessage {
  implicit val jsonFormat = Json.format[ApiMessage]
}
