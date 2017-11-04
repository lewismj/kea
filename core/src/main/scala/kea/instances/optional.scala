package kea
package instances

import cats.data.Validated.{Invalid, Valid}
import com.typesafe.config.Config


trait OptionInstances {

  /** Optional reader. */
  implicit def optionReader[A](implicit reader: ConfigReader[A]): ConfigReader[Option[A]] = (c: Config, p: String) => {
    if (c.hasPath(p)) {
      reader.get(c,p) match {
        case Valid(a) => Valid(Some(a))
        case i@Invalid(_) => i
      }
    } else {
      Valid(None)
    }
  }

}

object OptionInstances extends OptionInstances