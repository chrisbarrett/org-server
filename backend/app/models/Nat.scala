package models

import play.api.libs.json._
import play.api.data.validation.ValidationError
import play.api.mvc.QueryStringBindable

import scala.language.implicitConversions

case class Nat(value: Int) {
  require(value >= 0)
}

object Nat {
  val Zero = Nat(0)

  implicit def fromInt(n: Int) = Nat(n)

  implicit val jsonWriter = Writes { m: Nat ⇒ JsNumber(m.value) }

  implicit val jsonReads =
    Reads.of[Int]
      .filter(ValidationError("Must be 0 or greater")) { _ >= 0 }
      .map { s ⇒ Nat(s) }

  implicit def queryStringBindable(implicit intBinder: QueryStringBindable[Int]) = new QueryStringBindable[Nat] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Nat]] =
      intBinder.bind(key, params).map(_.right.map(Nat(_)))

    override def unbind(key: String, x: Nat): String = intBinder.unbind(key, x.value)
  }
}
