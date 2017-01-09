package hyperdrive.cj.http

import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.server.Directives._
import hyperdrive.cj.http.CollectionJsonProtocol._
import hyperdrive.cj.http.SprayCollectionJsonSupport._
import hyperdrive.cj.model.{CollectionJson, DataConverter, IdDataExtractor, TemplateConverter}

import scala.concurrent.{ExecutionContext, Future}

class CollectionJsonRoute[Ent : DataConverter : TemplateConverter : IdDataExtractor, Service](basePath: String, service: Service)(implicit executionContext: ExecutionContext, ev : CollectionJsonService[Ent, Service]) { 

  lazy val route =
    extractUri { uri =>
      pathPrefix(basePath) {
        (get & pathEnd) {
          complete {
            getCollection(uri)
          }
        } ~
        (get & path(Segment)) { id =>
          complete {
            getSingle(uri, id)
          }
        }
      }
    }

  private[this] def getCollection(uri: Uri): Future[CollectionJson] = 
    ev.getAll(service).map(items => CollectionJson(uri, items))

  private[this] def getSingle(uri: Uri, id: String): Future[CollectionJson] = {
    val basePath = uri.path.reverse.tail.reverse
    val baseUri = uri.withPath(basePath)
    ev.getById(service, id).map(item => CollectionJson(baseUri, item.toSeq))
  }
}
