package kea

import com.typesafe.config.Config


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
    * @return a `Result[A]`
    */
  def get(config: Config, path: String): Result[A]
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
    * @return         a `Result[A]`.
    */
  def as[A](path: String)(implicit reader: ConfigReader[A]): Result[A]
    = reader.get(config,path)
}


