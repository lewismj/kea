package kea
package tests

import cats.data.Validated.Valid
import com.typesafe.config.ConfigFactory
import kea.implicits._

class OptionReaderTest extends KeaSuite {

  private val config = ConfigFactory.load

  test("can read optional value.") {
    val i = config.as[Option[Int]]("example.foo.some-int")
    i should be (Valid(Some(4)))
  }

  test("missing optional values are valid.") {
    val i = config.as[Option[Int]]("example.foo.some-missing-int")
    i should be (Valid(None))
  }

  test("invalid optional value.") {
    val i = config.as[Option[Boolean]]("example.foo.some-int")
    i.isInvalid should be (true)
  }



}
