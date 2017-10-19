package kea
package instances

import com.typesafe.config.Config

import scala.collection.JavaConverters._
import scala.collection.breakOut
import scala.concurrent.duration.Duration

trait CollectionInstances {

  /*
    Note, we could use CanBuildFrom for generalized containers.
    Usually we want either List, that can be converted.
    */

  /** Primitive List readers. */
  implicit val doubleListReader: ConfigReader[List[Double]] = (c: Config, p: String) =>
    validated(c.getDoubleList(p).asScala.map(_.doubleValue)(breakOut))

  implicit val intListReader: ConfigReader[List[Int]] = (c: Config, p: String) =>
    validated(c.getIntList(p).asScala.map(_.intValue)(breakOut))

  implicit val longListReader: ConfigReader[List[Long]] = (c: Config, p: String) =>
    validated(c.getIntList(p).asScala.map(_.longValue)(breakOut))

  implicit val boolListReader: ConfigReader[List[Boolean]] = (c: Config, p: String) =>
    validated(c.getBooleanList(p).asScala.map(_.booleanValue)(breakOut))

  implicit val stringListReader: ConfigReader[List[String]] = (c: Config, p: String) =>
    validated(c.getStringList(p).asScala.toList)

  implicit val durationListReader: ConfigReader[List[Duration]] = (c: Config, p: String) =>
    validated(c.getDurationList(p).asScala.map(d => Duration.fromNanos(d.toNanos))(breakOut))


}
