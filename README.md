# KEA

## Summary
<p align="left">
<img src="https://travis-ci.org/lewismj/kea.svg?branch=master"/>
<a href="https://www.codacy.com/app/lewismj/kea?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=lewismj/kea&amp;utm_campaign=Badge_Grade"></a>
</p>

This library provides a type-safe (_validated_),  way to query Typelevel configuration.

Configuration values are returned as a `ValidatedNel[T]`. So, any errors in your
configuration may be accumulated.

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
```

We can now get individual elements as:

```scala
  config.as[String]("example.foo.some-string")
```

These return a `Validation`, so we can compose config funtions as follows:

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

Any errors are accumulated as a list of `Throwable`. For example, given

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


