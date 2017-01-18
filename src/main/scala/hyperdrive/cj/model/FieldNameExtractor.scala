package hyperdrive.cj.model

import shapeless.labelled.FieldType
import shapeless.{::, HList, HNil, LabelledGeneric, Lazy, Witness}

trait FieldNameExtractor[T] {
  def getFieldNames: Seq[Symbol]
}

object FieldNameExtractor {
  
  def apply[T](implicit fieldNameExtractor: FieldNameExtractor[T]): FieldNameExtractor[T] = fieldNameExtractor
  
  def makeExtractor[T](extractor: => Seq[Symbol]): FieldNameExtractor[T] = new FieldNameExtractor[T] {
    override def getFieldNames = extractor
  }

  implicit val hNilExtractor = makeExtractor[HNil](Seq.empty)
  implicit def hConsExtractor[K <: Symbol, V, L <: HList](implicit keyWitness: Witness.Aux[K],
                                                          tailExtractor: FieldNameExtractor[L]) =
    makeExtractor[FieldType[K, V] :: L]{
      keyWitness.value +: tailExtractor.getFieldNames
    }

  implicit def genericExtractor[T, Repr <: HList](implicit labelledGeneric: LabelledGeneric.Aux[T, Repr],
                                                  fieldNameExtractor: Lazy[FieldNameExtractor[Repr]]) =
    makeExtractor[T](fieldNameExtractor.value.getFieldNames)
}
