import cats.data.{NonEmptyList, Validated}

package object kea {

  /** A non-empty list of throwable. */
  type ThrowableNel = NonEmptyList[Throwable]

  /** Validated 'A', Invalid items are a Nel of throwable. */
  type ValidatedNel[A] = Validated[ThrowableNel, A]

}
