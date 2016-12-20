package hyperdrive.cj

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import scala.concurrent.Future
import hyperdrive.cj.DataValue._

case class Foo(id: Long, x: String, y: Int)

class FooService {
  def getAllFoos: Future[Seq[Foo]] = Future.successful(Seq(Foo(1, "one", 11), Foo(2, "two", 22)))
}

object Main extends App {

  implicit val actorSystem = ActorSystem("collection-json-example")
  implicit val actorMaterializer = ActorMaterializer()

  implicit val ec = actorSystem.dispatcher

  implicit val serviceEvidence = new CollectionJsonService[Foo, FooService] {
    override def getAll(service: FooService) = service.getAllFoos
  }

  implicit val idProviderEvidence = new IdProvider[Foo] {
    override def idField = "id"
  }

  val path = "foos"
  val service = new FooService
  val route = new CollectionJsonRoute[Foo, FooService](path, service)

  Http().bindAndHandle(route.route, "localhost", 9080)
}