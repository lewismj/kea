package kea
package tests

import scala.concurrent.duration._
import cats.data.Validated
import cats.implicits._
import com.typesafe.config.{Config, ConfigFactory}
import kea.implicits._


class SimpleConfigReaderTest extends KeaSuite {

  private val config = ConfigFactory.load

  /** Example, using validation to build a Validated case class. */
  case class Foo(s: String, i: Int, b: Boolean, d: Double, l: Long)
  object Foo {
    def apply(config: Config): ValidatedNel[Foo] =
      (config.as[String]("example.foo.some-string") |@|
        config.as[Int]("example.foo.some-int") |@|
        config.as[Boolean]("example.foo.some-boolean") |@|
        config.as[Double]("example.foo.some-double") |@|
        config.as[Long]("example.foo.some-long")).map(Foo.apply)
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

  test("can read list of string from configuration.") {
    val xs = config.as[List[String]]("example.some-strings")
    xs.map(_.toSet).toOption should be (Some(Set("abc","def","ghi")))
  }

  test("can read list of ints from configuration.") {
    val xs = config.as[List[Int]]("example.some-ints")
    xs.map(_.toSet).toOption should be (Some(Set(1,2,3,4)))
  }

  test("can read list of doubles from configuration.") {
    val xs = config.as[List[Double]]("example.some-doubles")
    xs.map(_.toSet).toOption should be (Some(Set(1.1,1.2,1.3)))
  }

  test("can read list of booleans from configuration.") {
    val xs = config.as[List[Boolean]]("example.some-bools")
    xs.toOption should be (Some(List(true,false,true)))
  }

  test("can read list of long values from configuration.") {
    val xs = config.as[List[Long]]("example.some-longs")
    xs.map(_.toSet).toOption should be (Some(Set(1234,456,789)))
  }

  test("can read list of duration values from configuration.") {
    val xs = config.as[List[Duration]]("example.some-durations")
    xs.map(_.toSet).toOption should be (Some(Set(1 second,2 second,3 second)))
  }

  test("missing element should return invalid.") {
    val e = config.as[String]("example.missing")
    e.isInvalid should be (true)
  }

  test("wrong type will return invalid.") {
    val i = config.as[Int]("example.foo.some-string")
    i.isInvalid should be (true)
  }


}
