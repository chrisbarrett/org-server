package services

import scala.util.Try

import configuration.JwtConfig
import javax.inject.{ Inject, Singleton }
import models.{ EncodedToken, JwtToken, Secret }
import pdi.jwt.{ JwtAlgorithm, JwtJson }
import play.api.libs.json.{ JsObject, Json }

@Singleton
class JwtEncoding @Inject() (config: JwtConfig) {
  def encode(jwt: JwtToken) = JwtEncoding.encode(jwt, config.secret)
  def decode(encoded: EncodedToken) = JwtEncoding.decode(encoded, config.secret)
}

object JwtEncoding {
  val algorithm = JwtAlgorithm.HS256

  def encode(jwt: JwtToken, secret: Secret): EncodedToken = {
    val json = Json.toJson(jwt).as[JsObject]
    val encoded = JwtJson.encode(json, secret.value, algorithm)
    EncodedToken(encoded)
  }

  def decode(encoded: EncodedToken, secret: Secret): Try[JwtToken] =
    JwtJson.decodeJson(encoded.value, secret.value, Seq(algorithm)).map(_.as[JwtToken])
}
