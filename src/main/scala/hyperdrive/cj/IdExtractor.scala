package hyperdrive.cj

import shapeless.tag.Tagged
import shapeless.{HList, LabelledGeneric}

trait IdExtractor[T] {
  type Repr <: HList

  def apply(obj: T): Repr
}

object IdExtractor {

  type Aux[T, Repr0 <: HList] = IdExtractor[T] { type Repr = Repr0 }
  
  implicit def instance[T, Repr <: HList](implicit lgen: LabelledGeneric.Aux[T, Repr], partition: PartitionBySubType[Repr, Tagged[Id]]) = 
    new IdExtractor[T] {
      override type Repr = partition.Left

      override def apply(obj: T): Repr = partition.filter(lgen.to(obj))
    }
}