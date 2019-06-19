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
<a href="https://search.maven.org/artifact/com.waioeka/kea-core_2.12/0.1.3/jar">
<img src="https://maven-badges.herokuapp.com/maven-central/com.waioeka/kea-core_2.12/badge.svg"/>
</a>
<a href="https://typelevel.org/cats/"><img src="https://typelevel.org/cats/img/cats-badge-tiny.png" alt="Cats friendly" /></a>
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
libraryDependencies += "com.waioeka" %% "kea-core" % "0.2.1"
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

Note, by convention, given a field name `abcDef` the configuration expected is `abc-def`. 
See, `FieldNameMapper`.

Using a custom or generic configuration reader, any errors are accumulated as a non empty list of `Throwable`. 
For example, given:
```scala
    val f = (config.as[String]("example.foo.some-string"),
             config.as[Int]("first error"),
             config.as[Boolean]("example.foo.some-boolean"),
             config.as[Double]("second error"),
             config.as[Long]("example.foo.some-long")).mapN(Foo.apply)
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

## Specifying a source for a configuration block.

In the example below the `from` function reads a `URL` from the configuration and sequentially
validates the configuration sourced.
 
The first parameter is the path of the URL within the top level config, the second parameter specifies the
path to read within the configuration document.

```scala
  val cfg =
    """
      |consul = "http://127.0.0.1:8500/v1/kv/config/dev?raw=true"
    """.stripMargin
    
  val config = ConfigFactory.parseString(cfg)
  
  case class Bar(a: String, b: Boolean, c: Int)
  
  val conf = config.from[URL,Bar]("consul","example.bar")
  // Valid(AppConfig(hello world,true,1234))
```

Sources can implement the `ConfigFrom` trait, for example the library supplies a source for URL,

```scala
  implicit def fromURL: ConfigFrom[URL] = (config: Conf, source: String) =>
    config.as[URL](source).andThen(url => {
      validated({
        ConfigFactory.parseString(Source.fromURL(url).mkString)
      })
    })
```

With the `from` function just adding the subsequent lookup,

```scala
  def from[A,B](sourcePath: String, path: String)(implicit  source: ConfigFrom[A],
                                                            reader: ConfigReader[B]): ValidatedConfig[B] =
    source.from(this,sourcePath).andThen(cf => Conf(cf).as[B](path))
```

Ensuring we validate the type of the source and the underlying call to `Source`.
