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


## Dependency Information

```scala
libraryDependencies += "com.waioeka" %% "kea-core" % "0.0.2"
```

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
 import kea.implicits._
 
 val config = ConfigFactory.load

 config.as[String]("example.foo.some-string")
 config.as[Int]("example.foo.some-int")
```

These return a `ValidatedNel`, see [cats](https://typelevel.org/cats/datatypes/validated.html) for background details.
This allow the composition of config functions as follows:

```scala
  case class Foo(s: String, i: Int, b: Boolean, d: Double, l: Long)
  object Foo {
    def apply(config: Config): ValidatedNel[Foo] =
      (config.as[String]("example.foo.some-string") |@|
        config.as[Int]("example.foo.some-int") |@|
        config.as[Boolean]("example.foo.some-boolean") |@|
        config.as[Double]("example.foo.some-double") |@|
        config.as[Long]("example.foo.some-long")).map(Foo.apply)
  }
```

Any errors are accumulated as a non empty list of `Throwable`. For example, given:

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


