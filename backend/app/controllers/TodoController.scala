package controllers

import models.ApiMessage
import play.api.libs.json.{ JsError, Reads }
import scala.concurrent.ExecutionContext.Implicits.global

import javax.inject.{ Inject, Singleton }
import models.{ Nat, Todo }
import play.api.libs.json.Json
import play.api.mvc._
import play.api.libs.json.Json
import scala.concurrent.Future
import store.{ NotFoundException, Store }

@Singleton
class TodoController @Inject() (store: Store) extends Controller {
  def getAll(n: Nat) = Action.async { request ⇒
    store.getAllFromId(n)
      .map { res ⇒ Ok(Json.toJson(res)) }
  }

  def getById(n: Nat) = Action.async {
    store.getById(n)
      .map { res ⇒ Ok(Json.toJson(res)) }
      .recover {
        case ex: NotFoundException ⇒
          val message = ApiMessage(ex.getMessage, NOT_FOUND, None)
          NotFound(Json.toJson(message))
      }
  }

  def create() = Action.async(parse.json[Todo]) { request ⇒
    store.insert(request.body)
      .map { id ⇒ Created(Json.toJson(id)) }
  }
}
