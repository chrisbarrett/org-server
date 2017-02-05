import scala.concurrent.Future

import com.softwaremill.quicklens._
import models.{ ApiMessage, Nat, Todo }
import org.scalatest.{ Inspectors, Matchers, WordSpec }
import org.scalatest.concurrent.ScalaFutures
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import store._

trait EndpointTest extends WordSpec with Matchers with ScalaFutures with Inspectors {
  lazy val store = new InMemoryStore

  val app: Application =
    new GuiceApplicationBuilder()
      .overrides(bind[Store].toInstance(store))
      .build

  def withEmptyStore[A](f: ⇒ A): A = {
    store.deleteAll()
    f
  }

  def withSeededStore[A](f: ⇒ A): A = withEmptyStore {
    todos.foreach(store.insert(_))
    f
  }

  def okay(response: ⇒ Future[Result]) = "return 200 OK" in {
    status(response) shouldBe OK
  }

  def created(response: ⇒ Future[Result]) = "return 201 Created" in {
    status(response) shouldBe CREATED
  }

  def badRequest(response: ⇒ Future[Result]) = "return 400 Bad Request" in {
    status(response) shouldBe BAD_REQUEST
  }

  def notFound(response: ⇒ Future[Result]) = "return 404 Not Found" in {
    status(response) shouldBe NOT_FOUND
  }

  def unsupportedMediaType(response: ⇒ Future[Result]) = "return 415 Unsupported Media Type" in {
    status(response) shouldBe UNSUPPORTED_MEDIA_TYPE
  }

  def jsonErrorMessage(response: ⇒ Future[Result]) = "should return a JSON error message" in {
    contentAsJson(response).asOpt[ApiMessage] shouldBe defined
  }

  // Sample data for seeding the store.
  val todos = Seq(
    Todo(keyword = "NEXT", headline = "Sharpen tusks"),
    Todo(keyword = "NEXT", headline = "Eat fish"),
    Todo(keyword = "TODO", headline = "Avoid bears"),
    Todo(keyword = "TODO", headline = "Stare at ice")
  )
}

class GetAll extends EndpointTest {

  def getAll(minimumId: Option[Any] = None): Future[Result] = {
    val query = Map("minimumId" → minimumId)
      .collect { case (k, Some(v)) ⇒ s"$k=$v" }
      .mkString("?", "&", "")

    val url = "/todos" + query
    route(app, FakeRequest(GET, url)).get
  }

  "no todos have been added" should {
    lazy val response = withEmptyStore {
      getAll()
    }

    behave like okay(response)

    "return an empty result set" in {
      contentAsJson(response).as[Seq[Todo]] shouldBe empty
    }
  }

  "no minimum ID is supplied" should {
    lazy val response = withSeededStore {
      getAll()
    }

    behave like okay(response)

    "return documents" which {
      lazy val docs = contentAsJson(response).as[Seq[Todo]]
      "have the expected content" in {
        docs.modify(_.each.id).setTo(None) should contain theSameElementsInOrderAs (todos)
      }
      "have their IDs set" in {
        val ids = docs.flatMap(_.id.map(_.value))
        ids should contain inOrderOnly (0, 1, 2, 3)
      }
    }
  }

  "minimum ID is not a number" should {
    lazy val response = withSeededStore {
      getAll(minimumId = Some("foo"))
    }
    behave like badRequest(response)
    behave like jsonErrorMessage(response)
  }

  "minimum ID is negative" should {
    lazy val response = withSeededStore {
      getAll(minimumId = Some(-1))
    }
    behave like badRequest(response)
    behave like jsonErrorMessage(response)
  }

  "no records exceed the minimum ID" should {
    lazy val response = withSeededStore {
      getAll(minimumId = Some(100))
    }

    behave like okay(response)

    "return an empty result set" in {
      val docs = contentAsJson(response).as[Seq[Todo]]
      docs shouldBe empty
    }
  }

  "some records exceed the minimum ID" should {
    lazy val response = withSeededStore {
      getAll(minimumId = Some(2))
    }

    behave like okay(response)

    "return documents" which {
      lazy val docs = contentAsJson(response).as[Seq[Todo]]

      "have the expected content" in {
        val expectedSubset = todos.drop(2)
        docs.modify(_.each.id).setTo(None) should contain theSameElementsInOrderAs (expectedSubset)
      }
      "have their IDs set" in {
        val ids = docs.flatMap(_.id.map(_.value))
        ids should contain inOrderOnly (2, 3)
      }
    }
  }
}

class GetById extends EndpointTest {

  def getById(id: Any): Future[Result] = {
    val url = s"/todos/$id"
    route(app, FakeRequest(GET, url)).get
  }

  "store is empty" should {

    lazy val response = withEmptyStore {
      getById(0)
    }

    behave like notFound(response)
    behave like jsonErrorMessage(response)
  }

  "todo does not exist for that ID" should {
    lazy val response = withSeededStore {
      getById(100)
    }

    behave like notFound(response)
    behave like jsonErrorMessage(response)
  }

  "todo exists for that ID" should {
    lazy val response = withSeededStore {
      getById(2)
    }

    behave like okay(response)

    "return the expected todo entry" in {
      val todo = contentAsJson(response).as[Todo]

      // format: OFF
      todo should have(
        'id       (Some(Nat(2))),
        'keyword  (todos(2).keyword),
        'headline (todos(2).headline)
      )
      // format: ON
    }
  }
}

class Create extends EndpointTest {

  def create(body: Option[JsValue]): Future[Result] = {
    val url = "/todos"
    body match {
      case None ⇒
        route(app, FakeRequest(POST, url)).get
      case Some(body) ⇒
        route(app, FakeRequest(POST, url).withJsonBody(body)).get
    }
  }

  "no body" should {
    lazy val response = withEmptyStore {
      create(body = None)
    }

    behave like unsupportedMediaType(response)
    behave like jsonErrorMessage(response)
  }

  "malformed body" should {
    lazy val response = withEmptyStore {
      val json = Json.obj("title" → "foo")
      create(body = Some(json))
    }

    behave like badRequest(response)
    behave like jsonErrorMessage(response)
  }

  "valid todo" should {
    val todo = Todo(
      id = None,
      keyword = "CANCELLED",
      headline = "foo"
    )
    lazy val response = withSeededStore {
      create(body = Some(Json.toJson(todo)))
    }

    behave like created(response)

    "insert the todo" in {
      val contents =
        store.getAllFromId(Nat.Zero)
          .futureValue
          .modify(_.each.id).setTo(None)

      contents should contain(todo)
    }
  }
}
