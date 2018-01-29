package kea



import cats.implicits._
import cats.data.Validated.Valid
import com.typesafe.config.Config
import shapeless.labelled.FieldType
import shapeless._
import labelled._
import kea.types._

/**
  * This is an adaption of the gist:
  *   https://gist.github.com/SystemFw/03d66d65e471c98f02ba27d7180465b1
  */


object generic {

  sealed trait Schema[A] {
    def from(c: Config, p: String): ValidatedConfig[A]
  }

  object Schema {

    def of[A](implicit s: Schema[A]): Schema[A] = s

    private def instance[A](f: (Config, String) => ValidatedConfig[A]): Schema[A] = new Schema[A] {
      def from(c: Config, p: String): ValidatedConfig[A] = f(c, p)
    }

    implicit val noOp: Schema[HNil] = new Schema[HNil] {
      override def from(c: Config, p: String): ValidatedConfig[HNil] = Valid(HNil)
    }

    implicit def classes[A, R <: HList](implicit repr: LabelledGeneric.Aux[A, R], schema: Schema[R]): Schema[A] =
      Schema.instance { (config, path) => schema.from(config, path).map(x => repr.from(x)) }

    implicit def parsing[K <: Symbol, V, T <: HList](
        implicit key: Witness.Aux[K],
        next: Schema[T],
        reader: Lazy[ConfigReader[V]],
        mapper: FieldNameMapper = DefaultFieldNameMapper): Schema[FieldType[K, V] :: T] = {

      Schema.instance { (config, path) =>
        val fieldName = key.value.name
        val f = reader.value.get(config, path + "." + mapper.replace(fieldName)).map(f => field[K](f))
   //     (f |@| next.from(config, path)).map(_ :: _)
        (f,next.from(config, path)).mapN((x,y) => x :: y)
      }
    }

  }

}
