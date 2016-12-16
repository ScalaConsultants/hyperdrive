package hyperdrive.cj

import scala.concurrent.Future

trait CollectionJsonService[Ent] {
  def getAll: Future[Seq[Ent]]
}