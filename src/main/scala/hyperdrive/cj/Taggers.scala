package hyperdrive.cj

import shapeless._
import shapeless.tag._

import scala.language.implicitConversions

trait Id

object Taggers {

  val tagger: Tagger[Id] = tag[Id]
  
  private def tagValue[T](value: T): T @@ Id = tagger(value)

  // added only specific only ones to avoid unnecessary conversions
  implicit def intTag(value: Int): @@[Int, Id] = tagValue(value)
  implicit def longTag(value: Long): @@[Long, Id] = tagValue(value)
  implicit def stringTag(value: String): @@[String, Id] = tagValue(value)
}
