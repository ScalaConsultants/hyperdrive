package hyperdrive.cj

case class Foo(x: String, y: Int)
case class Bar(str: String, vInt: Int, vDouble: Double, boolean: Boolean)

object Main extends App {
  val converter = implicitly[DataConverter[Foo]]
  val data = Foo("cos", 5)
  val data2 = Bar("string jakis", 5, 1.5, true)
  println(converter.toData(data))
  println(implicitly[DataConverter[Bar]].toData(data2))
}
