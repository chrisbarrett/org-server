package controllers

import models.Nat
import play.api.mvc._

class TodoController extends Controller {
  def getAll(n: Nat) = Action { request ⇒
    Ok("Hello, world")
  }
}
