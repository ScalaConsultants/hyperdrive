package hyperdrive.cj

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import hyperdrive.cj.CollectionJsonProtocol._
import hyperdrive.cj.MediaTypes._
import java.net.URI
import scala.concurrent.{ExecutionContext, Future}

class CollectionJsonRoute[Ent : DataConverter : TemplateConverter : CollectionJsonService](basePath: String)(implicit val executionContext: ExecutionContext) { 

  lazy val route =
    (extractScheme & extractHost) { (sName, hName) =>
        lazy val baseHref = new URI(s"$sName://$hName/$basePath")
        pathPrefix(basePath) {
          //respondWithMediaType(`application/vnd.collection+json`) {
            (get & pathEnd) {
              complete {
                getCollection(baseHref)
              }
            }
          //}
        }
    }

  private[this] def getCollection(baseHref: URI): Future[CollectionJson] = {
    val service = implicitly[CollectionJsonService[Ent]]
    service.getAll map { items => 
      val data = items map { item => 
        Item(href = baseHref, data = implicitly[DataConverter[Ent]].toData(item))
      }
      
      val template = implicitly[TemplateConverter[Ent]].toTemplate
      CollectionJson(Collection(href = baseHref, items = data, template = Some(template)))
    }
  }

}