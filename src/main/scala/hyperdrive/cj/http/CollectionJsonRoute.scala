package hyperdrive.cj.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.server.Directives._
import cats.data.{OptionT, ValidatedNel, NonEmptyList}
import cats.data.Validated
import cats.implicits._
import hyperdrive.cj.http.CollectionJsonProtocol._
import hyperdrive.cj.http.SprayCollectionJsonSupport._
import hyperdrive.cj.model.{CollectionJson, DataConverter, DataReader, IdNamesExtractor, Template, TemplateConverter}
import spray.json._

import scala.concurrent.{ExecutionContext, Future}

class CollectionJsonRoute[
    FullItemData : DataConverter : IdNamesExtractor, 
    PartItemData : DataReader : TemplateConverter, 
    Service](basePath: String, service: Service)(implicit executionContext: ExecutionContext, ev : CollectionJsonService[FullItemData, PartItemData, Service]) { 

  lazy val route =
    extractUri { uri =>
      pathPrefix(basePath) {
        (get & pathEnd) {
          complete {
            getCollection(uri)
          }
        } ~
        (post & pathEnd & entity(as[AddItemRequest])) { req =>
          complete {
            add(uri, req.template).map { validatedLocation => 
              errorResponseOr(
                uri,
                validatedLocation, 
                (locationUri: Uri) => HttpResponse(Created, headers = List(Location(locationUri)))
              )
            }
          }
        } ~
        (get & path(Segment)) { id =>
          complete {
            getItem(uri, id)
          }
        } ~
        (put & path(Segment) & entity(as[UpdateItemRequest])) { (id, req) =>
          complete {
            val res = update(id, req.template)
            res.map { validatedItem => 
              errorResponseOr(
                uri,
                validatedItem, 
                (item: FullItemData) => HttpResponse(OK)
              )
            }
          }
        } 
      }
    }

  private[this] def errorResponseOr[T](baseUri: Uri, validated: ValidatedNel[String, T], responseFunc: T => HttpResponse): HttpResponse =
    validated.fold(
      error => 
        HttpResponse(
          status = BadRequest, 
          entity = HttpEntity(
            MediaTypes.`application/vnd.collection+json`, 
            CollectionJson.withError(baseUri, error.head).toJson.prettyPrint)),
      valid => responseFunc(valid))

  private[this] def add(uri: Uri, template: Template): Future[ValidatedNel[String, Uri]] = {//Future.successful("1234")
    val resultT: OptionT[Future, Uri] = for {
      newItem <- OptionT(Future.successful(implicitly[DataReader[PartItemData]].readData(template.data)))
      id <- OptionT.liftF(ev.add(service, newItem))
    } yield uri.withPath(CollectionJson.itemPath(uri, id))

    resultT.value.map { opt => 
      Validated.fromOption(opt, NonEmptyList.of("Could not create a new item."))
    }
  }

  private[this] def getCollection(uri: Uri): Future[CollectionJson] = 
    ev.getAll(service).map(items => CollectionJson(uri, items))

  private[this] def getItem(uri: Uri, id: String): Future[CollectionJson] = {
    val basePath = uri.path.reverse.tail.reverse
    val baseUri = uri.withPath(basePath)
    ev.getById(service, id).map(item => CollectionJson(baseUri, item.toSeq))
  }

  private[this] def update(id: String, template: Template): Future[ValidatedNel[String, FullItemData]] = {
    val resultT: OptionT[Future, FullItemData] = for {
      itemData <- OptionT(Future.successful(implicitly[DataReader[PartItemData]].readData(template.data)))
      updatedItem <- OptionT(ev.update(service, id, itemData))
    } yield updatedItem

    resultT.value.map { opt =>
      Validated.fromOption(opt, NonEmptyList.of(s"Could not update the item with id=$id."))
    }
  }

}
