# kea 
## Summary
<p align="left">
<img src="https://travis-ci.org/lewismj/kea.svg?branch=master"/>
<a class="badge-align" href="https://www.codacy.com/app/lewismj/kea?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=lewismj/kea&amp;utm_campaign=Badge_Grade"><img src="https://api.codacy.com/project/badge/Grade/8c5bd884421f40cf8936511208262be9"/></a>
<img src="https://codecov.io/gh/lewismj/kea/branch/master/graph/badge.svg" alt="Codecov"/>
<img src="https://maven-badges.herokuapp.com/maven-central/com.waioeka/kea-core_2.12/badge.svg"/>
</p>

This library provides a type-safe (_validated_),  way to query [Typelevel configuration](https://github.com/typesafehub/config).
The goal is to provide as minimal a wrapper as necessary, avoiding the use of macros, compiler plugins or a large number of
dependencies.

Configuration values are returned as a `ValidatedNel[A]`, which is defined as `Validated[NonEmptyList[Throwable],A]`.
So, any errors in your configuration may be accumulated.

Shapeless is used to read configuration into case classes, without the requirement for the library to use macros.
See 'generic reader' below.


## Dependency Information
```scala
libraryDependencies += "com.waioeka" %% "kea-core" % "0.0.5"
```

## Issues/Tasks 
Waffle [board](https://waffle.io/lewismj/tiki).

## Example
Suppose we have some configuration:
```
example {

  foo {
    some-string = "hello world"
    some-int = 4
    some-boolean = true
    some-double = 1.1
    some-long = 1234
  }
  
}
```
We can specify the type of each configuration element, for example,
```scala
 import com.typesafe.config.{Config, ConfigFactory}
 import kea._
 import kea.implicits._
 
 val config = ConfigFactory.load

 config.as[String]("example.foo.some-string")
 config.as[Int]("example.foo.some-int")
```
These return a `ValidatedNel`, see [cats](https://typelevel.org/cats/datatypes/validated.html) for background details.

If we have a case class of the form:
```scala
case class Foo(s: String, i: Int, b: Boolean, d: Double, l: Long)
```
Then we can either compose the configuration functions manually:
```scala
  /** Example,Manually composing configuration functions. */
  object Foo {
    def apply(config: Config): ValidatedNel[Foo] =
      (config.as[String]("example.foo.some-string") |@|
        config.as[Int]("example.foo.some-int") |@|
        config.as[Boolean]("example.foo.some-boolean") |@|
        config.as[Double]("example.foo.some-double") |@|
        config.as[Long]("example.foo.some-long")).map(Foo.apply)
  }
```
Or, we can use the generic schema reader, given a configuration:
```
    example {
      bar {
        a: "hello world"
        b: true
        c: 1234
      }
    }
```
This can be read directly using the `GenericInstance` as follows,
```scala
   /** Example, Generic reader, no need to write boilerplate for case classes. */
   import kea.implicits._
   case class Bar(a: String, b: Boolean, c: Int)
   val result = config.as[Bar]("example.bar")
   // result: "Valid(Bar(hello world,true,1234))"
```
Note, by convention, given a field name `abcDef` the configuration expected is `abc-def`. This
is enforced at present, but could be parameterised in a future version.

Using a custom or generic configuration reader, any errors are accumulated as a non empty list of `Throwable`. 
For example, given:
```scala
    val f = (config.as[String]("example.foo.some-string") |@|
             config.as[Int]("first error") |@|
             config.as[Boolean]("example.foo.some-boolean") |@|
             config.as[Double]("second error") |@|
             config.as[Long]("example.foo.some-long")).map(Foo.apply)
    println(f)
```
Then `f` will accumulate two errors:
```scala
Invalid(
  NonEmptyList(
    com.typesafe.config.ConfigException$Missing: No configuration setting found for key '"first error"', 
    com.typesafe.config.ConfigException$Missing: No configuration setting found for key '"second error"'
  )
)
```
## Optional values
Reading an optional value of type `A`, for example,
```scala
config.as[Option[String]]("example.foo-somestring")
```
Returns a `ValidatedNel[Option[A]]`, this will be:
* `Valid(None)`, if the path is missing (absence of the optional value).
* `Validated(Some(a))`, where `a` is the instance of `A` at the path.
* `Invalid(_)`, if the path exists but value could not be read (e.g. incorrect type).

## Types

Types are supported by implementing a `ConfigReader` instance. An example implementation for `ZonedDateTime` is shown below:
```scala
  implicit val loadDateReader: ConfigReader[LocalDate] = (c: Config, p: String) =>
      validated(LocalDate.parse(c.getString(p)))
```
The library itself implements `ConfigReader` instances for the following types:

* primitives: String, Boolean, Int, Double, Long, BigInt, BigDecimal.
* configuration (reading inner configuration block): Config.
* date-time: ZonedDateTime, LocalData and LocalDateTime.

Together with `List` and `Option` of the above.