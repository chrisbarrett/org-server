package globals

import configuration._
import com.google.inject.AbstractModule
import store.{ InMemoryStore, Store }

class AppModule extends AbstractModule {
  override def configure(): Unit = {
    val config = AppConfig.load().get
    bind(classOf[JwtConfig]).toInstance(config.jwt)
    bind(classOf[Store]).toInstance(new InMemoryStore)
  }
}
