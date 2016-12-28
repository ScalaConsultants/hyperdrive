package hyperdrive.cj.http

import java.net.URI
import akka.http.scaladsl.server.Directives._
import hyperdrive.cj.http.CollectionJsonProtocol._
import hyperdrive.cj.http.SprayCollectionJsonSupport._
import hyperdrive.cj.model.{CollectionJson, DataConverter, IdDataExtractor, TemplateConverter}

import scala.concurrent.{ExecutionContext, Future}

class CollectionJsonRoute[Ent : DataConverter : TemplateConverter : IdDataExtractor, Service](basePath: String, service: Service)(implicit executionContext: ExecutionContext, ev : CollectionJsonService[Ent, Service]) { 

  lazy val route =
    (extractScheme & extractHost) { (sName, hName) =>
        lazy val baseHref = new URI(s"$sName://$hName/$basePath")
        pathPrefix(basePath) {
          (get & pathEnd) {
            complete {
              getCollection(baseHref)
            }
          }
        }
    }

  private[this] def getCollection(baseHref: URI): Future[CollectionJson] = 
    ev.getAll(service).map(items => CollectionJson(baseHref, items))

}