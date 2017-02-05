package store

import scala.concurrent.Future
import scala.util.control.NoStackTrace

import models.{ Nat, Todo }

case class NotFoundException(id: Nat)
  extends Exception(s"No document for ID: ${id.value}")
  with NoStackTrace

trait Store {
  def deleteAll(): Future[Unit]
  def getAllFromId(id: Nat): Future[Iterable[Todo]]
  def getById(id: Nat): Future[Todo]
  def insert(todo: Todo): Future[Nat]
}

class InMemoryStore extends Store {
  val list = new collection.mutable.ListBuffer[Todo]

  def deleteAll() = Future.successful {
    list.clear()
  }

  def getAllFromId(id: Nat) = Future.successful {
    list.zipWithIndex.collect {
      case (todo, key) if id <= key ⇒
        todo.copy(id = Some(Nat(key.toLong)))
    }
  }

  def getById(n: Nat) =
    list.lift(n.toInt)
      .map { todo ⇒
        val updated = todo.copy(id = Some(n))
        Future.successful(updated)
      }
      .getOrElse(Future.failed(NotFoundException(n)))

  def insert(todo: Todo): Future[Nat] = Future.successful {
    list.append(todo.copy(id = None))
    val index = list.size - 1L
    Nat(index)
  }
}
