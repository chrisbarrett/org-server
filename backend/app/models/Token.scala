package models

import play.api.libs.json.{ Json, Reads, Writes }

case class EncodedToken(value: String) extends AnyVal

object EncodedToken {
  implicit val jsonReads = Reads.of[String].map(EncodedToken(_))
  implicit val jsonWrites = Writes[EncodedToken](t ⇒ Json.toJson(t.value))
}
