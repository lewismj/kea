package kea
package tests

import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import kea.implicits._


class CollectionReaderTest extends KeaSuite  {

  private val config = ConfigFactory.load

  test("can read list of string from configuration.") {
    val xs = config.as[List[String]]("example.some-strings")
    xs.map(_.toSet).toOption should be (Some(Set("abc","def","ghi")))
  }

  test("can read sequence of string from configuration.") {
    val xs = config.as[Seq[String]]("example.some-strings")
    xs.toOption should be (Some(Seq("abc","def","ghi")))
  }

  test("can read vector of string from configuration.") {
    val xs = config.as[Vector[String]]("example.some-strings")
    xs.toOption should be (Some(Vector("abc","def","ghi")))
  }

  test("can read list of ints from configuration.") {
    val xs = config.as[List[Int]]("example.some-ints")
    xs.map(_.toSet).toOption should be (Some(Set(1,2,3,4)))
  }

  test("can read list of doubles from configuration.") {
    val xs = config.as[List[Double]]("example.some-doubles")
    xs.map(_.toSet).toOption should be (Some(Set(1.1,1.2,1.3)))
  }

  test("can read sequence of booleans from configuration.") {
    val xs = config.as[Seq[Boolean]]("example.some-bools")
    xs.toOption should be (Some(Seq(true,false,true)))
  }

  test("can read list of long values from configuration.") {
    val xs = config.as[List[Long]]("example.some-longs")
    xs.map(_.toSet).toOption should be (Some(Set(1234,456,789)))
  }

  test("can read list of duration values from configuration.") {
    val xs = config.as[List[Duration]]("example.some-durations")
    xs.map(_.toSet).toOption should be (Some(Set(1 second,2 second,3 second)))
  }
}
