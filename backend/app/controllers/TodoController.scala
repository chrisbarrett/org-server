package controllers

import javax.inject.{ Inject, Singleton }
import models.Nat
import play.api.libs.json.Json
import scala.concurrent.ExecutionContext.Implicits.global
import store.Store
import play.api.mvc._

@Singleton
class TodoController @Inject() (store: Store) extends Controller {
  def getAll(n: Nat) = Action.async { request ⇒
    store.getAllFromId(n)
      .map { res ⇒ Ok(Json.toJson(res)) }
  }
}
