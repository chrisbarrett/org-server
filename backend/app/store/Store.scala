package store

import scala.concurrent.Future
import scala.util.control.NoStackTrace

import com.softwaremill.quicklens._
import models.{ Nat, Todo }

case class NotFoundException(id: Nat)
  extends Exception(s"No document for ID: ${id.value}")
  with NoStackTrace

trait Store {
  def deleteAll(): Future[Unit]
  def getAllFromId(id: Nat): Future[Seq[Todo]]
  def getById(id: Nat): Future[Todo]
  def insert(todo: Todo): Future[Todo]
  def deleteById(id: Nat): Future[Unit]
  def updateById(id: Nat, todo: Todo): Future[Todo]
}

class InMemoryStore extends Store {
  var map = Map.empty[Nat, Todo]

  def deleteAll(): Future[Unit] = {
    map.synchronized { map = Map.empty }
    Future.successful(())
  }

  def getAllFromId(id: Nat): Future[Seq[Todo]] = {
    val results = map.synchronized {
      map.collect {
        case (key, todo) if id <= key ⇒
          key → todo.copy(id = Some(key))
      }
    }

    Future.successful(
      results.toSeq
        .sortBy { case (k, v) ⇒ k.toLong }
        .map(_._2)
    )
  }

  def deleteById(id: Nat): Future[Unit] = map.synchronized {
    if (map.contains(id)) {
      map = map - id
      Future.successful(())
    } else {
      Future.failed(NotFoundException(id))
    }
  }

  def updateById(id: Nat, todo: Todo): Future[Todo] = map.synchronized {
    if (map.contains(id)) {
      val updated = todo.copy(id = Some(id))
      map = map + (id → updated)
      Future.successful(updated)
    } else {
      Future.failed(NotFoundException(id))
    }
  }

  def getById(id: Nat): Future[Todo] = {
    val lookup = map.synchronized { map.get(id) }
    lookup
      .map { todo ⇒
        val updated = todo.copy(id = Some(id))
        Future.successful(updated)
      }
      .getOrElse {
        Future.failed(NotFoundException(id))
      }
  }

  def insert(todo: Todo): Future[Todo] = {
    val id = map.synchronized {
      val key = if (map.isEmpty) Nat.Zero else Nat(map.keySet.map(_.toLong).max + 1)
      map = map + (key → todo.copy(id = None))
      Nat(key)
    }
    val res = todo.modify(_.id).setTo(Some(id))
    Future.successful(res)
  }
}
