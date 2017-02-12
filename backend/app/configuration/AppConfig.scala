package configuration

import scala.util.Try

import com.typesafe.config.ConfigFactory
import models.Secret
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

case class JwtConfig(secret: Secret)

case class AppConfig(jwt: JwtConfig)

object AppConfig {
  def load(): Try[AppConfig] = Try {
    ConfigFactory.load().as[AppConfig]("app")
  }
}
