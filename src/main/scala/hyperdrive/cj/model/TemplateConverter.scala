package hyperdrive.cj.model

import shapeless.labelled.FieldType
import shapeless.tag.Tagged
import shapeless.{::, HList, HNil, LabelledGeneric, Lazy, Witness}

trait TemplateConverter[T] {
  def toTemplate: Template
}

object TemplateConverter {

  def apply[T](implicit templateConverter: TemplateConverter[T]): TemplateConverter[T] = templateConverter
  
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
