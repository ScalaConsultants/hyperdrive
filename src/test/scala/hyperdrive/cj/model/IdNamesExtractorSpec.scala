package hyperdrive.cj.model

import hyperdrive.util.MustMatchersExtended
import org.scalatest.WordSpec
import shapeless.HNil
import shapeless.tag._

class IdNamesExtractorSpec extends WordSpec with MustMatchersExtended {

  "IdNamesExtractor" should {
    "extract all id names of case class" in {

      case class TestClass(
        one: Long @@ Id,
        two: String @@ Id,
        three: Int)

      val idNamesExtractor = IdNamesExtractor[TestClass]
      
      val result = idNamesExtractor.getIds
      val expectedValue = Seq('one, 'two)
      
      mustBeVeryEqual(result, expectedValue)
    }

    "extract id names of HNil should be empty seq" in {
      val idNamesExtractor = IdNamesExtractor[HNil]

      val result = idNamesExtractor.getIds
      val expectedValue = Seq.empty[Symbol]

      mustBeVeryEqual(result, expectedValue)
    }
  }
  
}
