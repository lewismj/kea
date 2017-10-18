package kea
package instances

import com.typesafe.config.Config

import scala.concurrent.duration.{Duration, FiniteDuration}

trait PrimitiveInstances {

  /** Readers for primitive types. */
  implicit val stringReader: ConfigReader[String] = (c: Config, p: String) => validated(c.getString(p))

  implicit val boolReader: ConfigReader[Boolean] = (c: Config, p: String) => validated(c.getBoolean(p))

  implicit val intReader: ConfigReader[Int] = (c: Config, p: String) => validated(c.getInt(p))

  implicit val longReader: ConfigReader[Long] = (c: Config, p: String) => validated(c.getLong(p))

  implicit val doubleReader: ConfigReader[Double] = (c: Config, p: String) => validated(c.getDouble(p))

  implicit val durationReader: ConfigReader[Duration] = (c: Config, p: String)
    => validated(Duration.fromNanos(c.getDuration(p).toNanos))

  implicit val bigDecimalReader: ConfigReader[BigDecimal] = (c: Config, p: String)
    => validated(BigDecimal(c.getString(p)))

  implicit val bigIntReader: ConfigReader[BigInt] = (c: Config, p: String) => validated(BigInt(c.getString(p)))

  implicit val finiteDuration: ConfigReader[FiniteDuration] = (c: Config, p:String) => validated {
    val duration = Duration.fromNanos(c.getDuration(p).toNanos)
    Some(duration).collect { case fd: FiniteDuration => fd } match {
      case Some(fd) => fd
      case None => throw new RuntimeException(s"duration is not finite: $duration")
    }
  }


}
