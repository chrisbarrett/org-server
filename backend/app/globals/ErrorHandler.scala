package globals

import scala.concurrent.Future

import javax.inject.Singleton
import models.{ ApiMessage, RequestInfo }
import play.api.http.HttpErrorHandler
import play.api.http.Status.{ INTERNAL_SERVER_ERROR, NOT_FOUND }
import play.api.libs.json.Json
import play.api.mvc.{ RequestHeader, Result }
import play.api.mvc.Results.Status

// Custom error handler returns JSON for errors over the API, rather than the
// HTML web page that Play returns by default.

@Singleton
class ErrorHandler extends HttpErrorHandler {
  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    val message2 =
      if (message.isEmpty && statusCode == NOT_FOUND)
        "Not found"
      else
        message

    val info = Some(RequestInfo(request))
    val payload = ApiMessage(message, statusCode, info)
    val response = Status(statusCode)(Json.toJson(payload))
    Future.successful(response)
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    val payload = ApiMessage("Internal server error", INTERNAL_SERVER_ERROR, None)
    Future.successful(Status(INTERNAL_SERVER_ERROR)(Json.toJson(payload)))
  }
}
