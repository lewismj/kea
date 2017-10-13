package kea
package instances


import scala.collection.JavaConverters._
import scala.collection.breakOut
import cats.Semigroup
import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import com.typesafe.config.Config
import cats.syntax.either._
import scala.concurrent.duration.Duration


trait ConfigInstances {

  /**
    * Utility method (can be used to put Validation around library code etc.
    * Takes a function f: => A and returns `ValidatedNel[A]`.
    *
    * @param f    the function to validate.
    * @tparam A   the result type of the function.
    * @return     a `ValidatedNel[A]`.
    */
  def validated[A](f: => A): ValidatedNel[A] = {
    Either.catchOnly[Throwable](f) match {
      case Left(t) => Invalid(NonEmptyList.of(t))
      case Right(v) => Valid(v)
    }
  }

  /** Implicit converter to the `Conf` from Typelevel config. */
  implicit def toKeaConf(config: Config): Conf = Conf(config)

  /**
    * Define how to combine validation results.
    */
  implicit val semiGroup = new Semigroup[ThrowableNel] {
    override def combine(x: ThrowableNel, y: ThrowableNel) = x.concat(y)
  }

  /** Readers for primitive types. */
  implicit val stringReader: ConfigReader[String] = (c: Config, p: String) => validated(c.getString(p))
  implicit val boolReader: ConfigReader[Boolean] = (c: Config, p: String) => validated(c.getBoolean(p))
  implicit val intReader: ConfigReader[Int] = (c: Config, p: String) => validated(c.getInt(p))
  implicit val longReader: ConfigReader[Long] = (c: Config, p: String) => validated(c.getLong(p))
  implicit val doubleReader: ConfigReader[Double] = (c: Config, p: String) => validated(c.getDouble(p))
  implicit val durationReader: ConfigReader[Duration] = (c: Config, p: String)
    => validated(Duration.fromNanos(c.getDuration(p).toNanos))

  /** List readers. */
  implicit val doubleListReader: ConfigReader[List[Double]] = (c: Config, p: String) => {
    validated(c.getDoubleList(p).asScala.map(_.doubleValue)(breakOut))
  }

  implicit val intListReader: ConfigReader[List[Int]] = (c: Config, p: String) => {
    validated(c.getIntList(p).asScala.map(_.intValue)(breakOut))
  }

  implicit val longListReader: ConfigReader[List[Long]] = (c: Config, p: String) => {
    validated(c.getIntList(p).asScala.map(_.longValue)(breakOut))
  }


  implicit val boolListReader: ConfigReader[List[Boolean]] = (c: Config, p: String) => {
    validated(c.getBooleanList(p).asScala.map(_.booleanValue)(breakOut))
  }

  implicit val stringListReader: ConfigReader[List[String]] = (c: Config, p: String) => {
    validated(c.getStringList(p).asScala.toList)
  }

  implicit val durationListReader: ConfigReader[List[Duration]] = (c: Config, p: String) => {
    validated(c.getDurationList(p).asScala.map(d => Duration.fromNanos(d.toNanos))(breakOut))
  }


}
