import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList}
import cats.syntax.either._
import kea.types.Result


package object kea {

  /**
    * Utility method (can be used to put Validation around library code etc.
    * Takes a function f: => A and returns `Result[A]`.
    *
    * @param f    the function to validate.
    * @tparam A   the result type of the function.
    * @return     a `Result[A]`.
    */
  def validated[A](f: => A): Result[A] = {
    Either.catchOnly[Throwable](f) match {
      case Left(t) => Invalid(NonEmptyList.of(t))
      case Right(v) => Valid(v)
    }
  }

}
