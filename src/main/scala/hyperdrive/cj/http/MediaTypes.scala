package hyperdrive.cj.http

import akka.http.scaladsl.model.MediaType
import akka.http.scaladsl.model.HttpCharsets.`UTF-8`

object MediaTypes {

  val `application/vnd.collection+json`: MediaType.WithFixedCharset =
    MediaType.customWithFixedCharset("application", "vnd.collection+json", `UTF-8`)
}
