import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList}
import cats.syntax.either._
import kea.types.ValidatedConfig


package object kea {

  /**
    * Utility method (can be used to put Validation around library code etc.
    * Takes a function f: => A and returns `ValidatedConfig[A]`.
    *
    * @param f    the function to validate.
    * @tparam A   the result type of the function.
    * @return     a `ValidatedConfig[A]`.
    */
  def validated[A](f: => A): ValidatedConfig[A] = {
    Either.catchOnly[Throwable](f) match {
      case Left(t) => Invalid(NonEmptyList.of(t))
      case Right(v) => Valid(v)
    }
  }

}
