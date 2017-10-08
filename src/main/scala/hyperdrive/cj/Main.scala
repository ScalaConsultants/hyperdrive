package hyperdrive.cj

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import shapeless._
import shapeless.labelled._
import shapeless.tag._
import hyperdrive.cj.http.{CollectionJsonRoute, CollectionJsonService}
import hyperdrive.cj.model.{Id, Taggers}
import hyperdrive.cj.model.DataValue._

import scala.concurrent.Future

case class Foo(id: Long @@ Id, x: String, y: Int)
case class NewFoo(x: String, y: Int)

class FooService {
  import Taggers._

  private[this] var all = Seq(Foo(1L, "one", 11), Foo(2L, "two", 22))

  def maxId = all.map(_.id).reduceLeft(_ max _)

  def add(newFoo: NewFoo): Future[String] = {
    val id = maxId + 1
    all = Foo(id, newFoo.x, newFoo.y) +: all
    Future.successful(id.toString)
  }

  def getAllFoos: Future[Seq[Foo]] = Future.successful(all)

  def getById(id: Long): Future[Option[Foo]] = Future.successful(all.find(_.id == id))
}

object Main extends App {

  implicit val actorSystem = ActorSystem("collection-json-example")
  implicit val actorMaterializer = ActorMaterializer()

  implicit val ec = actorSystem.dispatcher

  implicit val serviceEvidence = new CollectionJsonService[Foo, NewFoo, FooService] {
    override def add(service: FooService, newEnt: NewFoo) = service.add(newEnt)
    override def getAll(service: FooService) = service.getAllFoos
    override def getById(service: FooService, id: String) = service.getById(id.toLong)
  }

  //implicit val fooDataReader = DataReader[Foo]
  val path = "foos"
  val service = new FooService
  val route = new CollectionJsonRoute[Foo, NewFoo, FooService](path, service)

  Http().bindAndHandle(route.route, "localhost", 9080)
}
