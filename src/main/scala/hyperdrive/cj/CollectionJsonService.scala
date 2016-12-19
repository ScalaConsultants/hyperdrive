package hyperdrive.cj

import scala.concurrent.Future

trait CollectionJsonService[Ent, Service] {
  def getAll(service: Service): Future[Seq[Ent]]
}