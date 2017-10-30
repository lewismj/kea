package kea
package instances

import java.time.{LocalDate, LocalDateTime, Period, ZonedDateTime}
import java.time.format.DateTimeFormatter


import com.typesafe.config.Config

trait DateTimeInstances {

  private val isoFormatter = DateTimeFormatter.ISO_DATE_TIME

  implicit val zonedDateTimeReader: ConfigReader[ZonedDateTime] = (c: Config, p: String) =>
      validated(ZonedDateTime.parse(c.getString(p),isoFormatter))

  implicit val loadDateReader: ConfigReader[LocalDate] = (c: Config, p: String) =>
      validated(LocalDate.parse(c.getString(p)))

  implicit val localDateTimeReader: ConfigReader[LocalDateTime] = (c: Config, p: String) =>
      validated(LocalDateTime.parse(c.getString(p)))

  implicit val periodReader: ConfigReader[Period] = (c: Config, p: String) =>
      validated(Period.parse(c.getString(p)))

}

object DateTimeInstances extends DateTimeInstances
