package hyperdrive.cj.model

import hyperdrive.util.MustMatchersExtended
import org.scalatest.WordSpec
import shapeless.HNil
import shapeless.tag._

class TemplateConverterSpec extends WordSpec with MustMatchersExtended {

  "TemplateConverter" should {
    "convert all non-id fields to proper Template" in {

      case class TestClass(
        one: Long @@ Id,
        two: String,
        three: Int)

      val templateConverter = TemplateConverter[TestClass]
      
      val result = templateConverter.toTemplate
      val expectedValue = Template(
        Seq(
          Data(name = "two"), 
          Data(name = "three")
        )
      )
      
      mustBeVeryEqual(result, expectedValue)
    }

    "convert HNil to empty Template" in {
      val templateConverter = TemplateConverter[HNil]

      val result = templateConverter.toTemplate
      val expectedValue = Template()

      mustBeVeryEqual(result, expectedValue)
    }
  }
  
}
