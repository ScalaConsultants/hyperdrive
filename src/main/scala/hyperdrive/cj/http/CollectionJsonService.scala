package hyperdrive.cj.http

import scala.concurrent.Future

trait CollectionJsonService[FullItemData, PartItemData, Service] {
  def add(service: Service, newEnt: PartItemData): Future[String]
  def getAll(service: Service): Future[Seq[FullItemData]]
  def getById(service: Service, id: String): Future[Option[FullItemData]]
  def update(service: Service, id: String, ent: PartItemData): Future[Option[FullItemData]]
}
