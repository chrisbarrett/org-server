package models

import play.api.libs.json.Json

case class JwtToken(
  user: User,
  exp:  Option[Long]   = None,
  iss:  Option[String] = None,
  sub:  Option[String] = None
)

object JwtToken {
  implicit val jsonFormat = Json.format[JwtToken]
}
