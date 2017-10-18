package kea

import cats.data.{NonEmptyList, Validated}

object types {

  /** A non-empty list of throwable. */
  type ThrowableNel = NonEmptyList[Throwable]

  /** Validated 'A', Invalid items are a Nel of throwable. */
  type Result[A] = Validated[ThrowableNel, A]

}
