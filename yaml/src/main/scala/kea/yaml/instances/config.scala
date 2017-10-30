package kea
package yaml
package instances

import com.typesafe.config.{Config, ConfigFactory}
import org.yaml.snakeyaml.Yaml


trait ConfigInstances {



  implicit class RichConfig(cf: Config) {

    private lazy val yaml = new Yaml()

    def withYamlFallback(ys: String): Config =
      cf.withFallback(ConfigFactory.parseMap(yaml.load[java.util.Map[String, Object]](ys)))
  }



}

object ConfigInstances extends ConfigInstances