import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, Validated}
import cats.syntax.either._


package object kea {

  /** A non-empty list of throwable. */
  type ThrowableNel = NonEmptyList[Throwable]

  /** Validated 'A', Invalid items are a Nel of throwable. */
  type ValidatedNel[A] = Validated[ThrowableNel, A]

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
