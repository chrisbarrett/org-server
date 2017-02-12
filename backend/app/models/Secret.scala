package models

import play.api.libs.json.{ Json, Reads, Writes }

case class Secret(value: String) extends AnyVal

object Secret {
  implicit val jsonReads = Reads.of[String].map(Secret(_))
  implicit val jsonWrites = Writes[Secret](s ⇒ Json.toJson(s.value))
}
