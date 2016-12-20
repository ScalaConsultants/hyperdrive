package hyperdrive.cj

import akka.http.scaladsl.marshalling._
import spray.json._

trait SprayCollectionJsonSupport {
  implicit def sprayCollectionJsonMarshaller(implicit writer: RootJsonWriter[CollectionJson], printer: JsonPrinter = CompactPrinter): ToEntityMarshaller[CollectionJson] =
    Marshaller.StringMarshaller.wrap(MediaTypes.`application/vnd.collection+json`)(printer) compose writer.write
}

object SprayCollectionJsonSupport extends SprayCollectionJsonSupport
