package hyperdrive.cj.model

import java.net.URI

import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.Uri.Path

object CollectionJson {

  def getValue(dv: DataValue): String = dv match {
    case BigDecimalDataValue(v) => v.toString
    case StringDataValue(v) => v
    case BooleanDataValue(v) => v.toString
  }

  def apply[Ent : DataConverter : TemplateConverter : IdDataExtractor](uri: Uri, items: Seq[Ent]): CollectionJson = {
    val baseUri = new URI(uri.toString)

    val itemPath: String => Path = id => 
      if (uri.path.endsWithSlash)
        uri.path + id
      else
        uri.path / id

    val data = items map { item => 
      val idFieldName = implicitly[IdDataExtractor[Ent]].getIdData(item).head.fieldName
      val data = implicitly[DataConverter[Ent]].toData(item)
      val idValue = data.find(_.name == idFieldName).flatMap(_.value).get
      val itemUri = uri.withPath(itemPath(getValue(idValue)))
      Item(href = new URI(itemUri.toString), data = data)
    }
      
    val template = implicitly[TemplateConverter[Ent]].toTemplate
    CollectionJson(Collection(href = baseUri, items = data, template = Some(template)))
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

  import shapeless.tag._

  implicit val intIdConverter = new DataValueConverter[Int @@ Id] {
    override def convert(value: Int @@ Id): DataValue = BigDecimalDataValue(value)
  }

  implicit val longIdConverter = new DataValueConverter[Long @@ Id] {
    override def convert(value: Long @@ Id): DataValue = BigDecimalDataValue(value)
  }

  implicit val stringIdConverter = new DataValueConverter[String @@ Id] {
    override def convert(value: String @@ Id): DataValue = StringDataValue(value)
  }
  
  implicit val intConverter = new DataValueConverter[Int] {
    override def convert(value: Int): DataValue = BigDecimalDataValue(value)
  }

  implicit val doubleConverter = new DataValueConverter[Double] {
    override def convert(value: Double): DataValue = BigDecimalDataValue(value)
  }

  implicit val longConverter = new DataValueConverter[Long] {
    override def convert(value: Long): DataValue = BigDecimalDataValue(value)
  }

  implicit val floatConverter = new DataValueConverter[Float] {
    override def convert(value: Float): DataValue = BigDecimalDataValue(BigDecimal.decimal(value))
  }

  implicit val stringConverter = new DataValueConverter[String] {
    override def convert(value: String): DataValue = StringDataValue(value)
  }

  implicit val booleanConverter = new DataValueConverter[Boolean] {
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
