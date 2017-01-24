package hyperdrive.cj.model

import shapeless._
import shapeless.labelled._

trait DataValueReader[T] {
  def readDataValue(value: DataValue): Option[T]
}

object DataValueReader {
  
  implicit val stringDVR: DataValueReader[String] = new DataValueReader[String] {
    def readDataValue(value: DataValue): Option[String] = value match {
      case StringDataValue(s) => Some(s)
      case _ => None
    }
  }

  implicit val boolDVR: DataValueReader[Boolean] = new DataValueReader[Boolean] {
    def readDataValue(value: DataValue): Option[Boolean] = value match {
      case BooleanDataValue(b) => Some(b)
      case _ => None
    }
  }
  
  implicit val intDVR: DataValueReader[Int] = new DataValueReader[Int] {
    def readDataValue(value: DataValue): Option[Int] = value match {
      case BigDecimalDataValue(b) => Some(b.intValue)
      case _ => None
    }
  }

  implicit val longDVR: DataValueReader[Long] = new DataValueReader[Long] {
    def readDataValue(value: DataValue): Option[Long] = value match {
      case BigDecimalDataValue(b) => Some(b.longValue)
      case _ => None
    }
  }

  implicit val floatDVR: DataValueReader[Float] = new DataValueReader[Float] {
    def readDataValue(value: DataValue): Option[Float] = value match {
      case BigDecimalDataValue(b) => Some(b.floatValue)
      case _ => None
    }
  }

  implicit val doubleDVR: DataValueReader[Double] = new DataValueReader[Double] {
    def readDataValue(value: DataValue): Option[Double] = value match {
      case BigDecimalDataValue(b) => Some(b.doubleValue)
      case _ => None
    }
  }
}

trait DataReader[T] {
  def readData(values: Seq[Data]): Option[T]
}

object DataReader {
  
  def apply[T](implicit dataReader: DataReader[T]): DataReader[T] = dataReader

  implicit val hnilDR: DataReader[HNil] = new DataReader[HNil] {
    def readData(values: Seq[Data]): Option[HNil] = Some(HNil)
  }
  
  implicit def hconsDR[K <: Symbol, V, L <: HList](implicit wit: Witness.Aux[K],
                                                   dvr: DataValueReader[V],
                                                   tail: DataReader[L]): DataReader[FieldType[K, V] :: L] = new DataReader[FieldType[K, V] :: L] {
    def readData(values: Seq[Data]): Option[FieldType[K, V] :: L] = {
      
      val fieldName = wit.value.name
      for {
        hd <- values.collect {
            case Data(_, name, Some(dv)) if name == fieldName =>
              dv
          }.flatMap(dvr.readDataValue(_)).headOption
        tl <- tail.readData(values)
      } yield hd.asInstanceOf[FieldType[K, V]] :: tl
    }
  }
  
  implicit def genDR[T, Repr <: HList](implicit lg: LabelledGeneric.Aux[T, Repr],
                                       dr: DataReader[Repr]): DataReader[T] = new DataReader[T] {
    def readData(values: Seq[Data]): Option[T] = {
      dr.readData(values).map(lg.from)
    }                                       
  }
}
