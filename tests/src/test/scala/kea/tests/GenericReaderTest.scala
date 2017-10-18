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

  test("can parse inner case classes.") {
    /* first {
      a: 1
      b: "hello"
      c {
        d: "world"
      }
    } */
    case class Second(d: String)
    case class First(a: Int, b: String, c: Second)
    val result  = config.as[First]("example.first")
    result should be (Valid(First(1,"hello",Second("world"))))
  }


  test("can parse inner case classes foo.") {

    /** works. */
//    case class Second(c: String, d: Int)
//    case class First(a: Second, b: Int)
//    val compilesOk  = config.as[First]("example.first")

    /** divergent implicit expansion. */
//    case class SecondX(c: String, d: Int)
//    case class FirstX(a: Second)
//    val failesToCompile = config.as[FirstX]("example.first")

    true
  }



}
