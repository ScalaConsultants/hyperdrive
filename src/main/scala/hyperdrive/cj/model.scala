package hyperdrive.cj

import java.net.URI

object CollectionJson {

  def getValue(dv: DataValue): String = dv match {
    case BigDecimalDataValue(v) => v.toString
    case StringDataValue(v) => v
    case BooleanDataValue(v) => v.toString
  }

  def apply[Ent : DataConverter : TemplateConverter : IdProvider](baseHref: URI, items: Seq[Ent]): CollectionJson = { 
    val data = items map { item => 
      val idFieldName = implicitly[IdProvider[Ent]].idField
      val data = implicitly[DataConverter[Ent]].toData(item)
      val idValue = data.find(_.name == idFieldName).flatMap(_.value).get
      Item(href = baseHref.resolve(getValue(idValue)), data = data)
    }
      
    val template = implicitly[TemplateConverter[Ent]].toTemplate
    CollectionJson(Collection(href = baseHref, items = data, template = Some(template)))
  }
}

case class CollectionJson(collection: Collection)

case class Collection(
    version: String = "1.0", 
    href: URI,
    links: Seq[Link] = Seq.empty,
    items: Seq[Item] = Seq.empty,
    queries: Seq[Query] = Seq.empty,
    template: Option[Template] = None,
    error: Option[Error] = None)
    
case class Error(title: String, code: String, message: String)

case class Template(data: Seq[Data]) {
  def +:(data: Data): Template = this.copy(data +: this.data)
}

object Template {
  def apply(): Template = new Template(Seq.empty)
}

case class Item(href: URI, data: Seq[Data] = Seq.empty, links: Seq[Link] = Seq.empty)

case class Data(prompt: Option[String] = None, name: String, value: Option[DataValue] = None)

sealed trait DataValue
case class BigDecimalDataValue(value: BigDecimal) extends DataValue
case class StringDataValue(value: String) extends DataValue
case class BooleanDataValue(value: Boolean) extends DataValue

trait DataValueConverter[T] {
  def convert(value: T): DataValue
}

object DataValue {
  implicit val IntConverter = new DataValueConverter[Int] {
    override def convert(value: Int): DataValue = BigDecimalDataValue(value)
  }

  implicit val DoubleConverter = new DataValueConverter[Double] {
    override def convert(value: Double): DataValue = BigDecimalDataValue(value)
  }

  implicit val LongConverter = new DataValueConverter[Long] {
    override def convert(value: Long): DataValue = BigDecimalDataValue(value)
  }

  implicit val FloatConverter = new DataValueConverter[Float] {
    override def convert(value: Float): DataValue = BigDecimalDataValue(BigDecimal.decimal(value))
  }

  implicit val StringConverter = new DataValueConverter[String] {
    override def convert(value: String): DataValue = StringDataValue(value)
  }

  implicit val BooleanConverter = new DataValueConverter[Boolean] {
    override def convert(value: Boolean): DataValue = BooleanDataValue(value)
  }
}

case class Query(
    href: URI, 
    rel: String,
    name: Option[String] = None, 
    prompt: Option[String] = None, 
    data: Seq[QueryData] = Seq.empty)

case class QueryData(name: String, value: String)

case class Link(
    href: URI, 
    rel: String, 
    name: Option[String] = None, 
    render: Option[String] = None, 
    prompt: Option[String] = None)
