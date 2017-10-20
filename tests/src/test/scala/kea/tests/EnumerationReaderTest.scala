package kea
package tests

import com.typesafe.config.ConfigFactory
import kea.implicits._


object FooBarBaz extends Enumeration {
  val FOO= Value("FOO")
  val BAR = Value("BAR")
  val BAZ = Value("BAZ")
}


class EnumerationReaderTest extends KeaSuite {

  private val config = ConfigFactory.load

  test("can read enumeration.") {
    val xs = config.as[Seq[FooBarBaz.Value]]("example.some-enum")
    xs.toOption should be (Some(Seq(FooBarBaz.FOO,FooBarBaz.BAR, FooBarBaz.BAZ)))
  }

}
