package hyperdrive.cj

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import scala.concurrent.Future

case class Foo(x: String, y: Int)
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

  val path = "foos"
  val route = new CollectionJsonRoute[Foo](path) {
    override def getAll =
      Future.successful(Seq(Foo("one", 1), Foo("two", 2)))
  }

  Http().bindAndHandle(route.route, "localhost", 9080)
}