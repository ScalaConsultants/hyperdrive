package hyperdrive.cj

import java.net.URI
import akka.http.scaladsl.server.Directives._
import hyperdrive.cj.CollectionJsonProtocol._
import hyperdrive.cj.SprayCollectionJsonSupport._

import scala.concurrent.{ExecutionContext, Future}

class CollectionJsonRoute[Ent : DataConverter : TemplateConverter, Service](basePath: String, service: Service)(implicit executionContext: ExecutionContext, ev : CollectionJsonService[Ent, Service]) { 

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

  private[this] def getCollection(baseHref: URI): Future[CollectionJson] = {
    ev.getAll(service) map { items => 
      val data = items map { item => 
        Item(href = baseHref, data = implicitly[DataConverter[Ent]].toData(item))
      }
      
      val template = implicitly[TemplateConverter[Ent]].toTemplate
      CollectionJson(Collection(href = baseHref, items = data, template = Some(template)))
    }
  }

}
