package controllers

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import javax.inject.{ Inject, Singleton }
import models.{ ApiMessage, EncodedToken, User }
import pdi.jwt.exceptions.JwtValidationException
import play.api.http.Status.UNAUTHORIZED
import play.api.libs.json._
import play.api.mvc.{ ActionBuilder, Request, Result, WrappedRequest }
import play.api.mvc.Results.Unauthorized
import services.JwtEncoding

case class AuthenticatedRequest[A](user: User, request: Request[A]) extends WrappedRequest[A](request)

@Singleton
class Authenticated @Inject() (encoding: JwtEncoding) extends ActionBuilder[AuthenticatedRequest] {
  val Bearer = "Bearer +(.*)".r

  def invokeBlock[A](request: Request[A], accept: AuthenticatedRequest[A] ⇒ Future[Result]): Future[Result] =
    request.headers
      .get("Authorization")
      .map {
        case Bearer(tok) ⇒
          Future
            .fromTry(encoding.decode(EncodedToken(tok)))
            .flatMap { auth ⇒
              accept(AuthenticatedRequest(auth.user, request))
            }
        case _ ⇒
          reject("Authorization header had malformed Bearer")
      }
      .getOrElse {
        reject("No Authorization header provided")
      }
      .recoverWith {
        case ex: JwtValidationException ⇒
          reject(ex.getMessage)
      }

  private def reject(reason: String): Future[Result] = {
    val err = Json.obj("message" → reason)
    val message = ApiMessage(reason, UNAUTHORIZED, None, Seq(err))
    Future.successful(Unauthorized(Json.toJson(message)))
  }
}
