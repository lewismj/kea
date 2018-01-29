package kea
package instances


import cats.Semigroup
import com.typesafe.config.Config
import kea.types._

trait ConfigInstances {

  /** Implicit converter to the `Conf` from Typelevel config. */
  implicit def toConf(config: Config): Conf = Conf(config)

  /**
    * Define how to combine validation results.
    */
  implicit val semiGroup = new Semigroup[ThrowableNel] {
    override def combine(x: ThrowableNel, y: ThrowableNel) = x.concatNel(y)
  }


  /** Read a sub-config. */
  implicit val subConfigReader: ConfigReader[Config] = (c: Config, p: String) =>
      validated(c.getConfig(p))

}

object ConfigInstances extends ConfigInstances