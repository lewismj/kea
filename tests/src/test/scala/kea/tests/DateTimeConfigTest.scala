package kea
package tests

import java.time.{LocalDate, LocalDateTime, ZonedDateTime}

import com.typesafe.config.ConfigFactory
import kea.implicits._



class DateTimeConfigTest extends KeaSuite {

  private val config = ConfigFactory.load

  test("can read local date from configuration.") {
    val ld = config.as[LocalDate]("example.some-local-date")
    ld.toOption should be (Some(LocalDate.parse("2049-01-01")))
  }

  test("can read local date time configuration.") {
    val ldt = config.as[LocalDateTime]("example.some-local-date-time")
    ldt.toOption should be (Some(LocalDateTime.parse("2049-01-01T00:00")))
  }

  test("can read zoned date time configuration.") {
    val zdt = config.as[ZonedDateTime]("example.some-zoned-date-time")
    zdt.toOption should be (Some(ZonedDateTime.parse("2049-01-01T00:00:00.000Z")))
  }

}
