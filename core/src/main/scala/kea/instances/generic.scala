package kea
package instances

import com.typesafe.config.Config
import kea.generic._
import shapeless.Lazy

trait GenericInstances {

  /** Provide a generic reader for case classes. */
  implicit def genericReader[A](implicit s: Lazy[Schema[A]]): ConfigReader[A]
    = (config: Config, path: String) =>  s.value.from(config, path)

}
