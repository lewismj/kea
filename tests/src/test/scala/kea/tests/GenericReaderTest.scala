package kea
package tests

import cats.data.Validated.Valid
import com.typesafe.config.ConfigFactory
import kea.implicits._

class GenericReaderTest extends KeaSuite {

  private val config = ConfigFactory.load

  test("can read generic case class.") {
    case class Bar(a: String, b: Boolean, c: Int)
    val result = config.as[Bar]("example.bar")
    result should be (Valid(Bar("hello world",true,1234)))
  }

  test("can read generic case class using naming convention") {
    case class Baz(a: String, b: Boolean, c: Int, abcDef: String)
    val result = config.as[Baz]("example.baz")
    result should be (Valid(Baz("hello world",true,1234,"hello world")))
  }

}
