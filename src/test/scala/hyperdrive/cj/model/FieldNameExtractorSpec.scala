package hyperdrive.cj.model

import hyperdrive.util.MustMatchersExtended
import org.scalatest.WordSpec
import shapeless.HNil

class FieldNameExtractorSpec extends WordSpec with MustMatchersExtended {

  "FieldNameExtractor" should {
    "extract all field names of case class" in {

      case class TestClass(
        one: Long,
        two: String,
        three: Int)

      val fieldNameExtractor = FieldNameExtractor[TestClass]
      
      val result = fieldNameExtractor.getFieldNames
      val expectedValue = Seq('one, 'two, 'three)
      
      mustBeVeryEqual(result, expectedValue)
    }

    "extract field names of HNil should be empty seq" in {
      val fieldNameExtractor = FieldNameExtractor[HNil]

      val result = fieldNameExtractor.getFieldNames
      val expectedValue = Seq.empty[Symbol]

      mustBeVeryEqual(result, expectedValue)
    }
  }
  
}
