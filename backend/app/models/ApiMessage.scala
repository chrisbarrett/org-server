package models

import play.api.libs.json.{ JsObject, Json }
import play.api.mvc.RequestHeader

case class RequestInfo(
  endpoint: String,
  query:    Map[String, Seq[String]],
  method:   String
)

object RequestInfo {
  implicit val jsonFormat = Json.format[RequestInfo]

  def apply(request: RequestHeader): RequestInfo = RequestInfo(
    endpoint = request.path,
    query = request.queryString.filterKeys(_ != ""),
    method = request.method
  )
}

case class ApiMessage(
  message:    String,
  statusCode: Int,
  request:    Option[RequestInfo],
  errors:     Seq[JsObject]
)

object ApiMessage {
  implicit val jsonFormat = Json.format[ApiMessage]
}
