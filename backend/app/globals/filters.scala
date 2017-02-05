package globals

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import akka.stream.Materializer
import javax.inject.Inject
import models.ApiMessage
import play.api.Logger
import play.api.http.DefaultHttpFilters
import play.api.http.Status.INTERNAL_SERVER_ERROR
import play.api.libs.json._
import play.api.mvc.{ Filter, RequestHeader, Result }
import play.api.mvc.Results.InternalServerError
import play.filters.gzip.GzipFilter

class Filters @Inject() (gzipFilter: GzipFilter, serverErrors: ServerErrorsFilter)
  extends DefaultHttpFilters(serverErrors, gzipFilter)

class ServerErrorsFilter @Inject() (implicit val mat: Materializer) extends Filter {
  val logger = Logger(getClass)

  def apply(nextFilter: RequestHeader ⇒ Future[Result])(header: RequestHeader): Future[Result] = {
    nextFilter(header)
      .recover {
        case ex: Throwable ⇒
          val message = ex.getMessage
          val response = ApiMessage(message, statusCode = INTERNAL_SERVER_ERROR)
          val js = Json.toJson(response)
          logger.error(message, ex)
          InternalServerError(js)
      }
  }
}
