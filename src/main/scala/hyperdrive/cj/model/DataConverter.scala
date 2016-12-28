package hyperdrive.cj.model

import shapeless.labelled.FieldType
import shapeless.tag.Tagged
import shapeless.{::, HList, HNil, LabelledGeneric, Lazy, Witness}

trait DataConverter[T] {
  def toData(obj: T): Seq[Data]
}

object DataConverter {

  def makeConverter[T](converter: T => Seq[Data]): DataConverter[T] = (obj: T) => converter(obj)

  implicit val hNilConverter = makeConverter[HNil](_ => Seq.empty)
  implicit def hConsConverter[K <: Symbol, V, L <: HList](implicit keyWitness: Witness.Aux[K],
                                                          headConverter: DataValueConverter[V], 
                                                          tailConverter: DataConverter[L]) =
    makeConverter[FieldType[K, V] :: L]{ hList =>
      val label = keyWitness.value.name
      Data(name = label, value = Some(headConverter.convert(hList.head))) +: tailConverter.toData(hList.tail)
    }

  implicit def genericConverter[T, Repr](implicit generic: LabelledGeneric.Aux[T, Repr], 
                                         encoder: Lazy[DataConverter[Repr]]) =
    makeConverter[T](value => encoder.value.toData(generic.to(value)))
  
}

trait TemplateConverter[T] {
  def toTemplate: Template
}

object TemplateConverter {
  def makeConverter[T](converter: => Template): TemplateConverter[T] = new TemplateConverter[T] {
    override def toTemplate = converter
  }

  implicit val hNilConverter = makeConverter[HNil](Template())
  implicit def hConsConverter[K <: Symbol, V, L <: HList](implicit keyWitness: Witness.Aux[K],
                                                          tailConverter: TemplateConverter[L]) =
    makeConverter[FieldType[K, V] :: L] {
      val label = keyWitness.value.name
      Data(name = label, value = None) +: tailConverter.toTemplate
    }

  implicit def genericConverter[T, Repr <: HList, Left <: HList, Right <: HList](implicit generic: LabelledGeneric.Aux[T, Repr], 
                                         partitionBySubType: PartitionBySubType.Aux[Repr, Tagged[Id], Left, Right],
                                         encoder: Lazy[TemplateConverter[Right]]) =
    makeConverter[T](encoder.value.toTemplate)
}
