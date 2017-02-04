package globals

import com.google.inject.AbstractModule
import store.{ InMemoryStore, Store }

class AppModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Store]).toInstance(new InMemoryStore)
  }
}
