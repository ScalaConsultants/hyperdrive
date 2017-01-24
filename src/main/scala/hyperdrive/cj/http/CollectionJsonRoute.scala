package hyperdrive.cj.http

import akka.http.scaladsl.model._
import StatusCodes._
import cats.data.OptionT
import cats.Functor
import cats.implicits._
//import cats.std.future._
//import cats.std.future._

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.server.Directives._
import hyperdrive.cj.http.CollectionJsonProtocol._
import hyperdrive.cj.http.SprayCollectionJsonSupport._
import hyperdrive.cj.model.{CollectionJson, DataConverter, DataValueReader, DataReader, IdNamesExtractor, Template, TemplateConverter}

import scala.concurrent.{ExecutionContext, Future}

class CollectionJsonRoute[Ent : DataConverter : TemplateConverter : IdNamesExtractor, NewEnt : DataReader, Service](basePath: String, service: Service)(implicit executionContext: ExecutionContext, ev : CollectionJsonService[Ent, NewEnt, Service]) { 

  lazy val route =
    extractUri { uri =>
      pathPrefix(basePath) {
        (get & pathEnd) {
          complete {
            getCollection(uri)
          }
        } ~
        (post & pathEnd & entity(as[AddEntityRequest])) { req =>
          complete {
            add(uri, req.template).map(locationUri => {
              val locationHeader = headers.Location(locationUri)
              HttpResponse(Created, headers = List(locationHeader))
            })
          }
        } ~
        (get & path(Segment)) { id =>
          complete {
            getSingle(uri, id)
          }
        }
      }
    }

  private[this] def add(uri: Uri, template: Template): Future[Uri] = {//Future.successful("1234")
    val resultT = for {
      newEnt <- OptionT(Future.successful(implicitly[DataReader[NewEnt]].readData(template.data)))
      id <- OptionT.liftF(ev.add(service, newEnt))
    } yield uri.withPath(CollectionJson.itemPath(uri, id))

    resultT.getOrElse(uri)
  }

  private[this] def getCollection(uri: Uri): Future[CollectionJson] = 
    ev.getAll(service).map(items => CollectionJson(uri, items))

  private[this] def getSingle(uri: Uri, id: String): Future[CollectionJson] = {
    val basePath = uri.path.reverse.tail.reverse
    val baseUri = uri.withPath(basePath)
    ev.getById(service, id).map(item => CollectionJson(baseUri, item.toSeq))
  }
}
