package kea
package tests

import org.scalatest.prop.{Checkers, GeneratorDrivenPropertyChecks}
import org.scalatest.{BeforeAndAfterAll, FunSuite, Matchers}

/**
  * Base definition for Kea test suites.
  */
trait  KeaSuite extends FunSuite
  with BeforeAndAfterAll
  with Checkers
  with Matchers
  with GeneratorDrivenPropertyChecks