package kea
package instances

import com.typesafe.config.Config
import kea.generic._

trait GenericInstances {

  /** Provide a generic reader for case classes. */
  implicit def genericReader[A](implicit s: Schema[A]): ConfigReader[A]
    = (config: Config, path: String) => Schema.of[A].from(config, path)

}