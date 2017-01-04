package hyperdrive.util

import org.scalatest.{Assertion, MustMatchers}

trait MustMatchersExtended extends MustMatchers {

  def mustBeVeryEqual[A, B](a: A, b: B)(implicit ev: A =:= B): Assertion = a mustEqual b

}
