package kea
package tests

import com.typesafe.config.ConfigFactory
import kea.yaml.implicits._

class YamlConfigTest extends KeaSuite {

  private val ys =
    """
      |seed_provider:
      |    # Addresses of hosts that are deemed contact points.
      |    # Cassandra nodes use this list of hosts to find each other and learn
      |    # the topology of the ring.  You must change this if you are running
      |    # multiple nodes!
      |    - class_name: org.apache.cassandra.locator.SimpleSeedProvider
      |      parameters:
      |          # seeds is actually a comma-delimited list of addresses.
      |          # Ex: "<ip1>,<ip2>,<ip3>"
      |          - seeds: "127.0.0.1"
    """.stripMargin


  test("can read yaml string") {

    val config = ConfigFactory.empty.withYamlFallback(ys)
    val _ = config

  }

}
