package hyperdrive.cj.http

import scala.concurrent.Future

trait CollectionJsonService[Ent, Service] {
  def getAll(service: Service): Future[Seq[Ent]]
}
