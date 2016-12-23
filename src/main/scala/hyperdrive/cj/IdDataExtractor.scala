package hyperdrive.cj

import shapeless.labelled.FieldType
import shapeless.{::, HList, HNil, Lazy, Witness}

case class IdData(fieldName: String)

trait IdDataExtractor[T] {
  def getIdData(list: T): Seq[IdData]
}

object IdDataExtractor {
  def makeConverter[T](converter: T => Seq[IdData]): IdDataExtractor[T] = (obj: T) => converter(obj)

  implicit val hNilExtractor = makeConverter[HNil](_ => Seq.empty)
  implicit def hConsExtractor[K <: Symbol, V, L <: HList](implicit keyWitness: Witness.Aux[K],
                                                          tailConverter: IdDataExtractor[L]) =
    makeConverter[FieldType[K, V] :: L]{ hList =>
      val fieldName = keyWitness.value.name
      IdData(fieldName) +: tailConverter.getIdData(hList.tail)
    }

  implicit def genericExtractor[T, Repr <: HList](implicit idExtractor: IdExtractor.Aux[T, Repr],
                                                  dataExtractor: Lazy[IdDataExtractor[Repr]]) =
    makeConverter[T](value => dataExtractor.value.getIdData(idExtractor(value)))
}