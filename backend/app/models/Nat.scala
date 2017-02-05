package models

import play.api.libs.json._
import play.api.data.validation.ValidationError
import play.api.mvc.{ PathBindable, QueryStringBindable }

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

  implicit def pathStringBindable(implicit longBinder: PathBindable[Long]) = new PathBindable[Nat] {
    override def bind(key: String, value: String): Either[String, Nat] = {
      longBinder.bind(key, value).right.map { Nat(_) }
    }

    override def unbind(key: String, x: Nat): String = longBinder.unbind(key, x.value)
  }

  implicit def queryStringBindable(implicit longBinder: QueryStringBindable[Long]) = new QueryStringBindable[Nat] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Nat]] =
      longBinder.bind(key, params).map(_.right.map(Nat(_)))

    override def unbind(key: String, x: Nat): String = longBinder.unbind(key, x.value)
  }
}
