package controllers

import models.ApiMessage
import scala.concurrent.ExecutionContext.Implicits.global

import javax.inject.{ Inject, Singleton }
import models.Nat
import play.api.libs.json.Json
import play.api.mvc._
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
          val message = ApiMessage(ex.getMessage, NOT_FOUND)
          NotFound(Json.toJson(message))
      }
  }
}
