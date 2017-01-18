package hyperdrive.cj.model

import hyperdrive.util.MustMatchersExtended
import org.scalatest.WordSpec
import shapeless.HNil
import shapeless.tag._

class DataConverterSpec extends WordSpec with MustMatchersExtended {

  "DataConverter" should {
    "convert all fields to seq of Data" in {

      import Taggers._
      import hyperdrive.cj.model.DataValue._

      case class TestClass(
        oneId: Long @@ Id,
        twoId: String @@ Id,
        threeId: Int @@ Id, 
        intValue: Int, 
        doubleValue: Double, 
        longValue: Long, 
        floatValue: Float, 
        stringValue: String, 
        boolValue: Boolean)
      
      val instance = TestClass(
        oneId = 0L, 
        twoId = "stringId", 
        threeId = 1, 
        intValue = 2, 
        doubleValue = 3.0, 
        longValue = 4L, 
        floatValue = 5, 
        stringValue = "stringValue", 
        boolValue = true)

      val dataConverter = DataConverter[TestClass]
      
      val result = dataConverter.toData(instance)
      val expectedValue = Seq(
        Data(name = "oneId", value = Some(BigDecimalDataValue(instance.oneId))),
        Data(name = "twoId", value = Some(StringDataValue(instance.twoId))),
        Data(name = "threeId", value = Some(BigDecimalDataValue(instance.threeId))),
        Data(name = "intValue", value = Some(BigDecimalDataValue(instance.intValue))),
        Data(name = "doubleValue", value = Some(BigDecimalDataValue(instance.doubleValue))),
        Data(name = "longValue", value = Some(BigDecimalDataValue(instance.longValue))),
        Data(name = "floatValue", value = Some(BigDecimalDataValue(BigDecimal.decimal(instance.floatValue)))),
        Data(name = "stringValue", value = Some(StringDataValue(instance.stringValue))),
        Data(name = "boolValue", value = Some(BooleanDataValue(instance.boolValue)))
      )
      
      mustBeVeryEqual(result, expectedValue)
    }

    "convert HNil to empty Seq of Data" in {
      val dataConverter = DataConverter[HNil]

      val result = dataConverter.toData(HNil)
      val expectedValue = Seq.empty[Data]

      mustBeVeryEqual(result, expectedValue)
    }
  }
  
}
