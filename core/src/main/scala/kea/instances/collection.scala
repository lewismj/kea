package kea
package instances


import cats.implicits._
import cats.data.Validated.{Invalid, Valid}
import com.typesafe.config.Config

import scala.jdk.CollectionConverters._
import scala.collection.Factory



trait CollectionInstances {

  implicit def collectionReader[T[_], A](implicit reader: ConfigReader[A],
                                         builder: Factory[A, T[A]]): ConfigReader[T[A]]
    = (c: Config, p: String) => {

    validated(c.getList(p).asScala).andThen(xs => {
      val validations = xs.map(x => {
        val elem = x.atPath("dummy")
        reader.get(elem, "dummy")
      }).toList

      validations.sequence match {
        case i@Invalid(_) => i
        case Valid(ys) =>
          val build = builder.newBuilder
          build.sizeHint(ys.size)
          ys.foreach(build += _)
          Valid(build.result())
      }
    })

  }
}

object CollectionInstances extends CollectionInstances
