package hyperdrive.cj.http

import scala.concurrent.Future

trait CollectionJsonService[Ent, NewEnt, Service] {
  def add(service: Service, newEnt: NewEnt): Future[String]
  def getAll(service: Service): Future[Seq[Ent]]
  def getById(service: Service, id: String): Future[Option[Ent]]
}
