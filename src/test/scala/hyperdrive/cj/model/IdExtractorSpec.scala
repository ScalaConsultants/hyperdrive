package hyperdrive.cj.model

import hyperdrive.util.MustMatchersExtended
import org.scalatest.WordSpec
import shapeless.HNil
import shapeless.syntax.singleton._
import shapeless.tag._

class IdExtractorSpec extends WordSpec with MustMatchersExtended {

  case class TestClass(
    id: Long @@ Id, 
    value: String, 
    secondId: String @@ Id, 
    secondValue: Int, 
    thirdId: Int @@ Id)

  val idExtractor = IdExtractor[TestClass]

  "IdExtractor" should {
    "filter all fields which are tagged by Id" in {
      import Taggers._
      val longId: Long @@ Id = 1L
      val stringId: String @@ Id = "id"
      val intId: Int @@ Id = 1
      val instance = TestClass(longId, "value", stringId, 0, intId)
      
      val result = idExtractor(instance)
      val expectedValue = 'id ->> longId :: 'secondId ->> stringId :: 'thirdId ->> intId :: HNil
      
      mustBeVeryEqual(result, expectedValue)
    }
  }
  
}
