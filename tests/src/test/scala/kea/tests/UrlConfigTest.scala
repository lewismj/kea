package kea
package tests

import java.net.{URI, URL}

import com.typesafe.config.ConfigFactory
import kea.implicits._


class UrlConfigTest extends KeaSuite {

  private val config = ConfigFactory.load

  test("can read url from configuration.") {
    val url = config.as[URL]("example.some-url")
    url.toOption should be (Some(new URL("http://www.waioeka.com")))
  }

  test("can read uri from configuration.") {
    val uri= config.as[URI]("example.some-uri")
    uri.toOption should be (Some(new URI("file://2049/k.txt")))
  }

}
