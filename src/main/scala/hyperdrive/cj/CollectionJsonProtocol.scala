package hyperdrive.cj

import java.net.URI
import spray.json._

object CollectionJsonProtocol extends DefaultJsonProtocol {

  implicit val uriFormat = new JsonFormat[URI] {
    def write(o:URI) = JsString(o.toString())
    def read(value:JsValue) = new URI(value.toString())
  }
  
  implicit val dataValueFormat = new JsonFormat[DataValue] {
    
    override def write(obj: DataValue): JsValue = obj match {
      case BigDecimalDataValue(v) => JsNumber(v)
      case StringDataValue(v) => JsString(v)
      case BooleanDataValue(v) => JsBoolean(v)
    }

    override def read(json: JsValue): DataValue = json match {
      case JsString(v) => StringDataValue(v)
      case JsNumber(v) => BigDecimalDataValue(v)
      case JsBoolean(v) => BooleanDataValue(v)
      case JsNull => null
    }
  }
  
  implicit val queryDataFormat = jsonFormat2(QueryData)
  implicit val queryFormat = jsonFormat5(Query) 
  implicit val linkFormat = jsonFormat5(Link)
  implicit val dataFormat = jsonFormat3(Data)
  implicit val itemFormat = jsonFormat3(Item)
  implicit val errorFormat = jsonFormat3(Error)
  implicit val templateFormat = jsonFormat1(Template.apply)
  implicit val collectionFormat = jsonFormat7(Collection)
  implicit val collectionJsonFormat = jsonFormat1(CollectionJson.apply)
  
}
