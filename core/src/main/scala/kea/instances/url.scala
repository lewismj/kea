package kea
package instances

import java.net.{URI, URL}

import com.typesafe.config.Config

trait UrlInstances {

  implicit val urlReader: ConfigReader[URL] = (c: Config, p: String) => validated(new URL(c.getString(p)))
  implicit val uriReader: ConfigReader[URI] = (c: Config, p: String) => validated(new URI(c.getString(p)))

}

object UrlInstances extends UrlInstances