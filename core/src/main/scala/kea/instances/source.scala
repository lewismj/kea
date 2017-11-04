package kea
package instances

import java.net.{URI, URL}

import com.typesafe.config.ConfigFactory

import scala.io.Source

trait SourceInstances extends UrlInstances {

  /** Implicits for `ConfigFrom`. */

  implicit def fromURL: ConfigFrom[URL] = (config: Conf, sourcePath: String) =>
    config.as[URL](sourcePath).andThen(url => {
      validated({
        ConfigFactory.parseString(Source.fromURL(url).mkString)}
      )})

  implicit def fromURI: ConfigFrom[URI] = (config: Conf, sourcePath: String) =>
    config.as[URI](sourcePath).andThen(uri => {
      validated({
        ConfigFactory.parseString(Source.fromURI(uri).mkString)}
      )})

}

object SourceInstances extends SourceInstances