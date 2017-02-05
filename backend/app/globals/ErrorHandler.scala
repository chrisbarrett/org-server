package globals

import scala.concurrent.Future

import javax.inject.Singleton
import models.ApiMessage
import play.api.http.HttpErrorHandler
import play.api.http.Status.INTERNAL_SERVER_ERROR
import play.api.libs.json.Json
import play.api.mvc.{ RequestHeader, Result }
import play.api.mvc.Results.Status

// Custom error handler returns JSON for errors over the API, rather than the
// HTML web page that Play returns by default.

@Singleton
class ErrorHandler extends HttpErrorHandler {
  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    val payload = ApiMessage(message, statusCode)
    Future.successful(Status(statusCode)(Json.toJson(payload)))
  }
  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    val payload = ApiMessage("Internal server error", INTERNAL_SERVER_ERROR)
    Future.successful(Status(INTERNAL_SERVER_ERROR)(Json.toJson(payload)))
  }
}
