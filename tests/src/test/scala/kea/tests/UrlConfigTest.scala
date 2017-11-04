package kea
package tests

import java.net.{URI, URL}

import com.typesafe.config.ConfigFactory
import kea.implicits._


class UrlConfigTest extends KeaSuite {

  private val config = ConfigFactory.load

  /** require embedded Consul. */
  ignore("can read config from URL.") {
    val cfg =
      """
        |consul = "http://127.0.0.1:8500/v1/kv/config/dev?raw=true"
      """.stripMargin

    val config = ConfigFactory.parseString(cfg)
    case class AppConfig(a: String, b: Boolean, c: Int)

    val appConfig = config.from[URL,AppConfig]("consul","example.bar")
    appConfig.isValid should be (true)
  }

  test("can read url from configuration.") {
    val url = config.as[URL]("example.some-url")
    url.toOption should be (Some(new URL("http://www.waioeka.com")))
  }

  test("can read uri from configuration.") {
    val uri= config.as[URI]("example.some-uri")
    uri.toOption should be (Some(new URI("file://2049/k.txt")))
  }

}
