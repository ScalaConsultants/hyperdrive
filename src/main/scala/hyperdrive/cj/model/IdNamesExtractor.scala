package hyperdrive.cj.model

import shapeless.labelled.FieldType
import shapeless.{::, HList, HNil, Witness}

trait IdNamesExtractor[T] {
  def getIds: Seq[Symbol]
}

object IdNamesExtractor {
  
  def apply[T](implicit idNamesExtractor: IdNamesExtractor[T]): IdNamesExtractor[T] = idNamesExtractor
  
  def makeExtractor[T](extractor: => Seq[Symbol]): IdNamesExtractor[T] = new IdNamesExtractor[T] {
    override def getIds = extractor
  }

  implicit val hNilExtractor = makeExtractor[HNil](Seq.empty)
  implicit def hConsExtractor[K <: Symbol, V, L <: HList](implicit keyWitness: Witness.Aux[K],
                                                          tailExtractor: IdNamesExtractor[L]) =
    makeExtractor[FieldType[K, V] :: L]{
      keyWitness.value +: tailExtractor.getIds
    }

  implicit def genericExtractor[T, Repr <: HList](implicit idExtractor: IdExtractor.Aux[T, Repr], 
                                                  fieldNameExtractor: FieldNameExtractor[Repr]) =
    makeExtractor[T](fieldNameExtractor.getFieldNames)
}
