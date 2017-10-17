package kea

import cats.data.Validated.Valid
import com.typesafe.config.Config
import shapeless.labelled.FieldType
import shapeless._
import labelled._
import cats.syntax.cartesian._
import kea.implicits._
import kea.instances._

/**
  * This is an adaption of the gist:
  *   https://gist.github.com/SystemFw/03d66d65e471c98f02ba27d7180465b1
  */

object generic {

  /** Adopt convention that abcDef should be abc-def in the configuration. */
  private lazy val r = "((?<=[a-z0-9])[A-Z]|(?<=[a-zA-Z])[0-9]|(?!^)[A-Z](?=[a-z]))".r
  private def toConfigName(fieldName: String): String = {
    r.replaceAllIn(fieldName, m => s"-${m.group(1)}").toLowerCase
  }

  object ConfigReader
    extends PrimitiveInstances
      with CollectionInstances
      with ConfigInstances
      with OptionInstances
      with UrlInstances {

    def to[V](c: Config, p: String)(implicit C: ConfigReader[V]): Result[V] = C.get(c, p)

    def instance[V](f: (Config, String) => Result[V]): ConfigReader[V] = (c: Config, p: String) => f(c, p)
  }

  sealed trait Schema[A] {
    def from(c: Config, p: String): Result[A]
  }

  object Schema {

    def of[A](implicit s: Schema[A]): Schema[A] = s

    private def instance[A](f: (Config, String) => Result[A]): Schema[A] = new Schema[A] {
      def from(c: Config, p: String): Result[A] = f(c, p)
    }

    implicit val noOp: Schema[HNil] = new Schema[HNil] {
      override def from(c: Config, p: String): Result[HNil] = Valid(HNil)
    }

    implicit def classes[A, R <: HList](implicit repr: LabelledGeneric.Aux[A, R], schema: Schema[R]): Schema[A] =
      Schema.instance { (config, path) => schema.from(config, path).map(x => repr.from(x)) }

    implicit def parsing[K <: Symbol, V: ConfigReader, T <: HList](
                                                                    implicit key: Witness.Aux[K],
                                                                    next: Schema[T]): Schema[FieldType[K, V] :: T] = {
      Schema.instance { (config, path) =>
        val fieldName = key.value.name
        val f = ConfigReader.to[V](config, path + "." + toConfigName(fieldName)).map(f => field[K](f))
        (f |@| next.from(config, path)).map(_ :: _)
      }
    }
  }

}
