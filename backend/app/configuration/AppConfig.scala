package configuration

import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import models.Secret
import com.typesafe.config.ConfigFactory
import scala.util.Try

case class JwtConfig(secret: Secret)

case class AppConfig(jwt: JwtConfig)

object AppConfig {
  def load(): Try[AppConfig] = Try {
    ConfigFactory.load().as[AppConfig]("app")
  }
}
