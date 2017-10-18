package kea
package tests

import scala.concurrent.duration._
import cats.data.Validated
import cats.data.Validated.Valid
import cats.implicits._
import com.typesafe.config.{Config, ConfigFactory}
import kea.implicits._
import kea.types._


class CoreReadersTest extends KeaSuite {

  private val config = ConfigFactory.load

  /** Example, using validation to build a Validated case class. */
  case class Foo(s: String, i: Int, b: Boolean, d: Double, l: Long)
  object Foo {
    def apply(config: Config): Result[Foo] =
      (config.as[String]("example.foo.some-string") |@|
        config.as[Int]("example.foo.some-int") |@|
        config.as[Boolean]("example.foo.some-boolean") |@|
        config.as[Double]("example.foo.some-double") |@|
        config.as[Long]("example.foo.some-long")).map(Foo.apply)
  }


  test("can read sub-config.") {
    val cfg = config.as[Config]("example.foo")
    cfg.isValid should be (true)
  }


  test("can compose configuration.") {
    val foo = Foo(config)
    foo.toOption should be (Some(Foo("hello world",4,b=true,1.1,1234L)))
  }

  test("errors should accumulate.") {
    val f = (config.as[String]("example.foo.some-string") |@|
             config.as[Int]("first error") |@|
             config.as[Boolean]("example.foo.some-boolean") |@|
             config.as[Double]("second error") |@|
             config.as[Long]("example.foo.some-long")).map(Foo.apply)
    f.isInvalid should be (true)
  }

  test("can read int from configuration.") {
    val i =  config.as[Int]("example.foo.some-int")
    i should be (Validated.valid(4))
  }

  test("can read boolean from configuration.") {
    val b =  config.as[Boolean]("example.foo.some-boolean")
    b should be (Validated.valid(true))
  }

  test("can read string from configuration.") {
    val s =  config.as[String]("example.foo.some-string")
    s should be (Validated.valid("hello world"))
  }

  test("can read double from configuration.") {
    val d =  config.as[Double]("example.foo.some-double")
    d should be (Validated.valid(1.1))
  }

  test("can read long from configuration.") {
    val l =  config.as[Long]("example.foo.some-long")
    l should be (Validated.valid(1234L))
  }

  test("can read duration from configuration.") {
    val l =  config.as[Duration]("example.some-duration")
    l should be (Validated.valid(1 second))
  }

  test("can read finite duration from configuration.") {
    val fd = config.as[FiniteDuration]("example.some-duration")
    fd should be (Validated.valid(1 second))
  }

  test("missing element should return invalid.") {
    val e = config.as[String]("example.missing")
    e.isInvalid should be (true)
  }

  test("wrong type will return invalid.") {
    val i = config.as[Int]("example.foo.some-string")
    i.isInvalid should be (true)
  }

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

  test("can read big double from configuration.") {
    val bd = config.as[BigDecimal]("example.some-big-decimal")
    bd.toOption should be (Some(BigDecimal("1.0")))
  }

  test("can read big int from configuration.") {
    val bd = config.as[BigDecimal]("example.some-big-int")
    bd.toOption should be (Some(BigInt("1")))
  }

}
