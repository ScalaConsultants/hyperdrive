package hyperdrive.cj

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import hyperdrive.cj.CollectionJsonProtocol._
import hyperdrive.cj.MediaTypes._
import java.net.URI
import scala.concurrent.{ExecutionContext, Future}

abstract class CollectionJsonRoute[Ent : DataConverter](basePath: String)(implicit val executionContext: ExecutionContext) { 

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

  private[this] def getCollection(baseHref: URI): Future[CollectionJson] =
    getAll map { items => 
      val data = items map { item => 
        Item(href = baseHref, data = implicitly[DataConverter[Ent]].toData(item))
      }
      CollectionJson(Collection(href = baseHref, items = data))
    }

  def getAll: Future[Seq[Ent]]

}