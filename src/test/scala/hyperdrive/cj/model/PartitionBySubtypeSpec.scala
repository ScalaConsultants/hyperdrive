package hyperdrive.cj.model

import hyperdrive.util.MustMatchersExtended
import org.scalatest.WordSpec
import shapeless.{::, HNil}

class PartitionBySubtypeSpec extends WordSpec with MustMatchersExtended {

  sealed trait TestType
  class Test1 extends TestType
  class Test2 extends TestType
  class Test3
  
  val test1Instance = new Test1
  val test2Instance = new Test2
  val test3Instance = new Test3
  val intValue = 1
  val stringValue = "value"
  val doubleValue = 2.0
  val boolValue = true

  type testedValueType = Int :: Test3 :: String :: Test2 :: Double :: Test1 :: Boolean :: HNil
  val testedValue: testedValueType = intValue :: test3Instance :: stringValue :: test2Instance :: doubleValue :: test1Instance :: boolValue :: HNil

  val partition = PartitionBySubType[testedValueType, TestType]

  "PartitionBySubtype" should {
    "filter all subtypes" in {
      
      val result = partition.filter(testedValue)
      val expectedValue = test2Instance :: test1Instance :: HNil
      
      mustBeVeryEqual(result, expectedValue)
    }

    "filter all which are not subtypes" in {
      
      val result = partition.filterNot(testedValue)
      val expectedValue = intValue :: test3Instance :: stringValue :: doubleValue :: boolValue :: HNil

      mustBeVeryEqual(result, expectedValue)
    }
  }
  
}
