package hyperdrive.cj.http

import scala.concurrent.Future

trait CollectionJsonService[Ent, Service] {
  def getAll(service: Service): Future[Seq[Ent]]
  def getById(service: Service, id: String): Future[Option[Ent]]
}
