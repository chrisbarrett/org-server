package store

import models.{ Nat, Todo }
import scala.concurrent.Future
import scala.util.control.NoStackTrace

case class NotFound(key: Nat)
  extends Exception(s"No document for key: $key")
  with NoStackTrace

trait Store {
  def deleteAll(): Future[Unit]
  def getAllFromId(id: Nat): Future[Iterable[Todo]]
  def getById(id: Nat): Future[Todo]
  def insert(todo: Todo): Future[Nat]
}

class InMemoryStore extends Store {
  val list = new collection.mutable.ListBuffer[Todo]

  def deleteAll() =
    Future.successful(list.clear())

  def getAllFromId(n: Nat) = {
    val res =
      list.zipWithIndex.collect {
        case (todo, k) if n <= k ⇒
          todo.copy(id = Some(Nat(k.toLong)))
      }
    Future.successful(res)
  }

  def getById(n: Nat) = {
    val res = list.lift(n.toInt).getOrElse {
      throw NotFound(n)
    }
    Future.successful(res)
  }

  def insert(todo: Todo): Future[Nat] = {
    list.append(todo.copy(id = None))
    val index = list.size - 1L
    Future.successful(Nat(index))
  }
}
