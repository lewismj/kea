# kea 
## Summary
<p align="left">
<a href="https://travis-ci.org/lewismj/kea">
<img src="https://travis-ci.org/lewismj/kea.svg?branch=master"/>
</a>
<a class="badge-align" href="https://www.codacy.com/app/lewismj/kea?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=lewismj/kea&amp;utm_campaign=Badge_Grade"><img src="https://api.codacy.com/project/badge/Grade/8c5bd884421f40cf8936511208262be9"/></a>
<a href="https://codecov.io/gh/lewismj/kea">
<img src="https://codecov.io/gh/lewismj/kea/branch/master/graph/badge.svg" alt="Codecov"/>
</a>
<a href="https://search.maven.org/#search%7Cga%7C1%7Ca%3A%22kea-core_2.12%22">
<img src="https://maven-badges.herokuapp.com/maven-central/com.waioeka/kea-core_2.12/badge.svg"/>
</a>
<a href="https://waffle.io/lewismj/kea">
<img src="https://badge.waffle.io/lewismj/kea.svg?columns=In%20Progress,Done&style=flat-square">
</a>
</p>

This library provides a type-safe (_validated_) way to query [Typesafe configuration](https://github.com/typesafehub/config).
The goal is to provide as minimal a wrapper as necessary, avoiding the direct use of macros, compiler plugins or a large number of
dependencies.

Configuration values are returned as a `ValidatedConfig[A]`, which is defined as `Validated[NonEmptyList[Throwable],A]`.
So, any errors in your configuration may be accumulated.

[Shapeless](https://github.com/milessabin/shapeless) is used to read configuration into case classes, without 
the requirement for the library to directly use macros.

## Dependency Information
```scala
libraryDependencies += "com.waioeka" %% "kea-core" % "0.0.8"
```
## Example

We can specify the type of each configuration element, for example,
```scala
 import com.typesafe.config.{Config, ConfigFactory}
 import kea.implicits._
 
 val config = ConfigFactory.load

 config.as[String]("example.foo.some-string")
 config.as[Int]("example.foo.some-int")
```
These return a `ValidatedNel`, see [cats](https://typelevel.org/cats/datatypes/validated.html) for background details.

If we have configuration of the form:
```scala
  adt {
    a {
      c: "hello"
      d: 1
      e: "world"
      f: 2
    }
    b: 12
  }
```
Then this can be read directly into the case class structure as follows:
```scala
    case class Foo(c: String, d: Int, e: String, f: Int)
    case class Bar(a: Foo, b: Int)
    val result = config.as[Bar]("adt")
    // result: (Valid(Bar(Foo(hello, 1, world, 2), 12)))
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
Invalid(NonEmptyList(
com.typesafe.config.ConfigException$Missing: No configuration setting found for key '"first error"', 
com.typesafe.config.ConfigException$Missing: No configuration setting found for key '"second error"'
))
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

* __primitives__: String, Boolean, Int, Double, Long, BigInt, BigDecimal, Uuid, Url, Uri.
* __configuration__ (reading inner configuration block): Config.
* __date-time__: ZonedDateTime, LocalData and LocalDateTime.
* __enumerations__: See the `EnumerationReaderTest` for a simple example.
* __case classes__: support for algebraic data types.

Together with collection types (e.g. `List`, `Vector`, etc.) and `Option` of the above.

## Example

Loading config from Consul, below have the contents of `application.conf` as the value for a single key.
It would be possible to write a Consul wrapper that created a `Conf` object over a set of keys.
However, most applications prefer the config in a single json or hocon document.

```scala
  val cfg =
    """
      |consul = "http://127.0.0.1:8500/v1/kv/config/dev?raw=true"
    """.stripMargin
    
  val config = ConfigFactory.parseString(cfg)
  
  case class AppConfig(a: String, b: Boolean, c: Int)

  val appConfig = config.as[URL]("consul").andThen(url => {
    val consulConf = ConfigFactory.parseString(s"${Source.fromURL(url).mkString}")
    consulConf.as[AppConfig]("example.bar")
  })
```
