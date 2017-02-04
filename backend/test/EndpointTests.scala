import cats.Id
import models._
import org.scalatest.{ Matchers, WordSpec }
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.OneAppPerSuite
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import scala.concurrent.Future
import store._

class EndpointTests
    extends WordSpec
    with Matchers
    with OneAppPerSuite
    with ScalaFutures {

  // In-memory store for tests.

  lazy val store = new Store[Id, String, Todo] {
    val impl = collection.mutable.Map.empty[String, Todo]

    def deleteAll() = impl.clear()
  }

  def withEmptyStore[A](f: ⇒ A): A = {
    try {
      f
    } finally {
      store.deleteAll()
    }
  }

  "getting all todos" when {

    def getAll(): Future[Result] = {
      route(app, FakeRequest(GET, "/todos")).get
    }

    "no todos have been added" should {
      lazy val response = withEmptyStore {
        getAll()
      }
      "return 200 OK" in {
        status(response) shouldBe OK
      }
      "return an empty result set" in {
        contentAsJson(response).as[Seq[Todo]] shouldBe empty
      }
    }
  }
}
