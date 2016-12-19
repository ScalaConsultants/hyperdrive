package hyperdrive.cj

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import scala.concurrent.Future
import hyperdrive.cj.DataValue._

case class Foo(x: String, y: Int)

class FooService {
  def getAllFoos: Future[Seq[Foo]] = Future.successful(Seq(Foo("one", 1), Foo("two", 2)))
}
// case class Bar(str: String, vInt: Int, vDouble: Double, boolean: Boolean)

// object Main extends App {
//   val converter = implicitly[DataConverter[Foo]]
//   val data = Foo("cos", 5)
//   val data2 = Bar("string jakis", 5, 1.5, true)
//   println(converter.toData(data))
//   println(implicitly[DataConverter[Bar]].toData(data2))
// }

object Main extends App {

  implicit val actorSystem = ActorSystem("collection-json-example")
  implicit val actorMaterializer = ActorMaterializer()

  implicit val ec = actorSystem.dispatcher

  implicit val serviceEvidence = new CollectionJsonService[Foo, FooService] {
    override def getAll(service: FooService) = service.getAllFoos
  }

  val path = "foos"
  val service = new FooService
  val route = new CollectionJsonRoute[Foo, FooService](path, service)

  Http().bindAndHandle(route.route, "localhost", 9080)
}