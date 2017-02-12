package controllers

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import javax.inject.{ Inject, Singleton }
import models.{ ApiMessage, Nat, RequestInfo, Todo }
import play.api.libs.json.{ JsError, JsSuccess, Json, Reads }
import play.api.mvc._
import store.{ NotFoundException, Store }

@Singleton
class TodoController @Inject() (authenticated: Authenticated, store: Store) extends Controller {
  def getAll(n: Nat) = authenticated.async { request ⇒
    store.getAllFromId(n)
      .map { res ⇒ Ok(Json.toJson(res)) }
  }

  def getById(n: Nat) = authenticated.async {
    store.getById(n)
      .map { res ⇒ Ok(Json.toJson(res)) }
      .recover {
        case ex: NotFoundException ⇒
          val message = ApiMessage(ex.getMessage, NOT_FOUND, None)
          NotFound(Json.toJson(message))
      }
  }

  def updateById(n: Nat) = authenticated.async { request ⇒
    withJsonBody[Todo](request) { todo ⇒
      store.updateById(n, todo)
        .map { updated ⇒
          Ok(Json.toJson(updated))
        }
        .recover {
          case ex: NotFoundException ⇒
            val message = ApiMessage(ex.getMessage, NOT_FOUND, None)
            NotFound(Json.toJson(message))
        }
    }
  }

  def deleteById(n: Nat) = authenticated.async {
    store.deleteById(n)
      .map { _ ⇒
        val message = ApiMessage("Deleted", OK, None)
        Ok(Json.toJson(message))
      }
      .recover {
        case ex: NotFoundException ⇒
          val message = ApiMessage(ex.getMessage, NOT_FOUND, None)
          NotFound(Json.toJson(message))
      }
  }

  def create() = authenticated.async { request ⇒
    withJsonBody[Todo](request) { todo ⇒
      store.insert(todo).map { res ⇒ Created(Json.toJson(res)) }
    }
  }

  private def withJsonBody[A: Reads](request: Request[AnyContent])(f: A ⇒ Future[Result]): Future[Result] = {
    request.body.asJson
      .map { json ⇒
        json.validate[A] match {
          case JsSuccess(x, _) ⇒
            f(x)
          case JsError(errs) ⇒
            val info = RequestInfo(request)
            val errors = errs.map {
              case (path, e) ⇒ Json.obj(
                "path" → path.toJsonString,
                "errors" → Json.toJson(e.map(_.message))
              )
            }
            val message = ApiMessage("JSON validation failed.", BAD_REQUEST, Some(info), errors)
            val result = BadRequest(Json.toJson(message))
            Future.successful(result)
        }
      }
      .getOrElse {
        val info = RequestInfo(request)
        val message = ApiMessage("Expected a JSON body.", UNSUPPORTED_MEDIA_TYPE, Some(info))
        val result = UnsupportedMediaType(Json.toJson(message))
        Future.successful(result)
      }
  }
}
