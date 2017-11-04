package kea


import com.typesafe.config.Config
import kea.types.ValidatedConfig

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
    * @return a `ValidatedConfig[A]`
    */
  def get(config: Config, path: String): ValidatedConfig[A]
}

/**
  * Used to support automatically loading a resource from
  * a source such as URL, File, etc. Where the source is given
  * in a top-level configuration object. See `Conf.from`.
  *
  * @tparam T the type of Source.
  */
trait ConfigFrom[T] {
  def from(config: Conf, source: String): ValidatedConfig[Config]
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
    * @return         a `ValidatedConfig[A]`.
    */
  def as[A](path: String)(implicit reader: ConfigReader[A]): ValidatedConfig[A]
    = reader.get(config,path)


  /**
    * Utility method for reading configuration indirectly via some source, for example,
    *
    * {{{
    *    val cfg =
    *  """
    *    |consul = "http://127.0.0.1:8500/v1/kv/config/dev?raw=true"
    *  """.stripMargin
    *
    * val config = ConfigFactory.parseString(cfg)
    *
    * case class AppConfig(a: String, b: Boolean, c: Int)
    *
    * val appConfig = config.from[URL,AppConfig]("consul","example.bar")
    * }}}
    */
  def from[A,B](sourcePath: String, path: String)(implicit  source: ConfigFrom[A],
                                                            reader: ConfigReader[B]): ValidatedConfig[B] =
    source.from(this,sourcePath).andThen(cf => Conf(cf).as[B](path))

}

/**
  * Map between field names of case classes and their configuration names.
  * Typically a field name will be 'abcDef' and the config 'abc-def'.
  */
trait FieldNameMapper {
  def replace(fieldName: String): String
}

/** Default field name mapper. */
case object DefaultFieldNameMapper extends FieldNameMapper {
  private lazy val r = "((?<=[a-z0-9])[A-Z]|(?<=[a-zA-Z])[0-9]|(?!^)[A-Z](?=[a-z]))".r
  override def replace(fieldName: String): String
    = r.replaceAllIn(fieldName, m => s"-${m.group(1)}").toLowerCase
}