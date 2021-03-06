package hyperdrive.cj.model

import java.net.URI

import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.Uri.Path

import shapeless.{::, HNil, Generic}

object CollectionJson {

  def getValue(dv: DataValue): String = dv match {
    case BigDecimalDataValue(v) => v.toString
    case StringDataValue(v) => v
    case BooleanDataValue(v) => v.toString
  }

  def itemPath(baseUri: Uri, id: String): Path =
    if (baseUri.path.endsWithSlash)
      baseUri.path + id
    else
      baseUri.path / id

  def apply[Ent : DataConverter : IdNamesExtractor, NewEnt : TemplateConverter](uri: Uri, items: Seq[Ent]): CollectionJson = {
    val baseUri = new URI(uri.toString)

    val data = items map { item => 
      val idFieldName = implicitly[IdNamesExtractor[Ent]].getIds.head.name
      val data = implicitly[DataConverter[Ent]].toData(item)
      val idValue = data.find(_.name == idFieldName).flatMap(_.value).get
      val itemUri = uri.withPath(itemPath(uri, getValue(idValue)))
      Item(href = new URI(itemUri.toString), data = data)
    }
      
    val template = implicitly[TemplateConverter[NewEnt]].toTemplate
    CollectionJson(Collection(href = baseUri, items = data, template = Some(template)))
  }

  def withError(uri: Uri, error: Error): CollectionJson =
    CollectionJson(Collection(href = new URI(uri.toString), error = Some(error)))

  def withError(uri: Uri, message: String): CollectionJson = 
    withError(uri, Error(message = Some(message)))
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
    
case class Error(title: Option[String] = None, code: Option[String] = None, message: Option[String])

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
  def convert(value: T): Option[DataValue]
}

object DataValue {

  import shapeless.tag._

  implicit def optionConverter[T]
    (implicit enc: DataValueConverter[T]): DataValueConverter[Option[T]] = new DataValueConverter[Option[T]] {
    override def convert(value: Option[T]): Option[DataValue] = value.flatMap(enc.convert)
  }

  implicit val intIdConverter = new DataValueConverter[Int @@ Id] {
    override def convert(value: Int @@ Id): Option[DataValue] = Some(BigDecimalDataValue(value))
  }

  implicit val longIdConverter = new DataValueConverter[Long @@ Id] {
    override def convert(value: Long @@ Id): Option[DataValue] = Some(BigDecimalDataValue(value))
  }

  implicit val stringIdConverter = new DataValueConverter[String @@ Id] {
    override def convert(value: String @@ Id): Option[DataValue] = Some(StringDataValue(value))
  }
  
  implicit val intConverter = new DataValueConverter[Int] {
    override def convert(value: Int): Option[DataValue] = Some(BigDecimalDataValue(value))
  }

  implicit val doubleConverter = new DataValueConverter[Double] {
    override def convert(value: Double): Option[DataValue] = Some(BigDecimalDataValue(value))
  }

  implicit val longConverter = new DataValueConverter[Long] {
    override def convert(value: Long): Option[DataValue] = Some(BigDecimalDataValue(value))
  }

  implicit val floatConverter = new DataValueConverter[Float] {
    override def convert(value: Float): Option[DataValue] = Some(BigDecimalDataValue(BigDecimal.decimal(value)))
  }

  implicit val stringConverter = new DataValueConverter[String] {
    override def convert(value: String): Option[DataValue] = Some(StringDataValue(value))
  }

  implicit val booleanConverter = new DataValueConverter[Boolean] {
    override def convert(value: Boolean): Option[DataValue] = Some(BooleanDataValue(value))
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
