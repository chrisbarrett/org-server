package models

import play.api.libs.json._
import play.api.data.validation.ValidationError
import play.api.mvc.QueryStringBindable

import scala.language.implicitConversions

case class Nat(value: Long) {
  require(value >= 0)
  def +(other: Nat) = value + other.value
  def *(other: Nat) = value * other.value
  def >(other: Nat) = value > other.value
  def <(other: Nat) = value < other.value
  def >=(other: Nat) = value >= other.value
  def <=(other: Nat) = value <= other.value
}

object Nat {
  val Zero = Nat(0)

  implicit def toLong(n: Nat) = n.value

  implicit val jsonWriter = Writes { m: Nat ⇒ JsNumber(m.value) }

  implicit val jsonReads =
    Reads.of[Long]
      .filter(ValidationError("Must be 0 or greater")) { _ >= 0 }
      .map { s ⇒ Nat(s) }

  implicit def queryStringBindable(implicit intBinder: QueryStringBindable[Long]) = new QueryStringBindable[Nat] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Nat]] =
      intBinder.bind(key, params).map(_.right.map(Nat(_)))

    override def unbind(key: String, x: Nat): String = intBinder.unbind(key, x.value)
  }
}
