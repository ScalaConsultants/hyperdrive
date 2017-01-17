package hyperdrive.cj.http

import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.server.Directives._
import hyperdrive.cj.http.CollectionJsonProtocol._
import hyperdrive.cj.http.SprayCollectionJsonSupport._
import hyperdrive.cj.model.{CollectionJson, DataConverter, IdNamesExtractor, TemplateConverter}

import scala.concurrent.{ExecutionContext, Future}

class CollectionJsonRoute[Ent : DataConverter : TemplateConverter : IdNamesExtractor, Service](basePath: String, service: Service)(implicit executionContext: ExecutionContext, ev : CollectionJsonService[Ent, Service]) { 

  lazy val route =
    extractUri { uri =>
      pathPrefix(basePath) {
        (get & pathEnd) {
          complete {
            getCollection(uri)
          }
        }
      }
    }

  private[this] def getCollection(uri: Uri): Future[CollectionJson] = 
    ev.getAll(service).map(items => CollectionJson(uri, items))

}
