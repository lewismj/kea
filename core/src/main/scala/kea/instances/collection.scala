package kea
package instances

import cats.implicits._
import cats.data.Validated.{Invalid, Valid}
import com.typesafe.config.Config

import scala.collection.JavaConverters._
import scala.collection.generic.CanBuildFrom
import scala.language.postfixOps
import scala.language.higherKinds


trait CollectionInstances {

  implicit def collectionReader[T[_], A](implicit reader: ConfigReader[A],
                                         builder: CanBuildFrom[Nothing, A, T[A]]): ConfigReader[T[A]]
    = (c: Config, p: String) => {

    val xs = c.getList(p).asScala

    val validations = xs.map(x => {
      val elem = x.atPath("dummy")
      reader.get(elem, "dummy")
    }).toList

    validations.sequenceU match {
      case i@Invalid(_) => i
      case Valid(ys) =>
        val build = builder()
        build.sizeHint(ys.size)
        ys.foreach(build += _)
        Valid(build.result())
    }
  }

}
