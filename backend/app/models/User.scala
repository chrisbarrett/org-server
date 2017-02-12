package models

import play.api.libs.json.Json

case class User(email: String)

object User {
  implicit val jsonFormat = Json.format[User]
}
