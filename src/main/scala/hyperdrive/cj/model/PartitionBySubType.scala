package hyperdrive.cj.model

import shapeless.{::, <:!<, HList, HNil}

trait PartitionBySubType[L <: HList, U] {
  type Left <: HList
  type Right <: HList
  
  def filter(l: L): Left
  def filterNot(l: L): Right

}

object PartitionBySubType {
  type Aux[L <: HList, U, Left0 <: HList, Right0 <: HList] = PartitionBySubType[L, U] {
    type Left = Left0
    type Right = Right0
  }

  implicit def hlistPartitionBySubTypeNil[U]: Aux[HNil, U, HNil, HNil] = new PartitionBySubType[HNil, U] {
    type Left = HNil
    type Right = HNil

    def filter(l: HNil): HNil = HNil
    def filterNot(l: HNil): HNil = HNil
  }

  implicit def hlistPartitionBySubTypeRight[H, L <: HList, U, Left0 <: HList, Right0 <: HList](
    implicit p: Aux[L, U, Left0, Right0], e: H <:!< U
  ): Aux[H :: L, U, Left0, H :: Right0] = new PartitionBySubType[H :: L, U] {
    type Left = Left0
    type Right = H :: Right0

    def filter(l: H :: L): Left = p.filter(l.tail)
    def filterNot(l: H :: L): Right = l.head :: p.filterNot(l.tail)
  }

  implicit def hlistPartitionBySubTypeLeft[H, L <: HList, U, Left0 <: HList, Right0 <: HList](
    implicit p: Aux[L, U, Left0, Right0], e: H <:< U
  ): Aux[H :: L, U, H :: Left0, Right0] = new PartitionBySubType[H :: L, U] {
    type Left = H :: Left0
    type Right = Right0

    def filter(l: H :: L): Left = l.head :: p.filter(l.tail)
    def filterNot(l: H :: L): Right = p.filterNot(l.tail)
  }
}
