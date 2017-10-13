package kea

import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import cats.syntax.either._
import com.typesafe.config.Config


object config {

  /**
    * Config reader type class.
    *
    * @tparam A The type of the item to be read from configuration.
    */
  trait ConfigReader[A] {
    /**
      * Given a config and path, return validated A.
      *
      * @param config the underlying configuration.
      * @param path   the path of the config item within
      *               the configuration.
      * @return a `ValidatedNel[A]`
      */
    def get(config: Config, path: String): ValidatedNel[A]
  }

  /**
    * Conf is a wrapper around the Typelevel `Config` object.
    * It provides a typed-checked read of the the configuration.
    *
    * @param config the underlying configuration object.
    */
  case class Conf(config: Config) {

    /**
      * Read the path, using a `ConfigReader` instance (see instances for examples).
      *
      * @param path     the path of the item to be read within the config file.
      * @param reader   the implicit configuration reader.
      * @tparam A       the type of the item to be read.
      * @return         a `ValidatedNel[A]`.
      */
    def as[A](path: String)(implicit reader: ConfigReader[A]): ValidatedNel[A]
      = reader.get(config,path)
  }

  /** Implicit converter to the `Conf` from Typelevel config. */
  implicit def toKeaConf(config: Config): Conf = Conf(config)

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

}
