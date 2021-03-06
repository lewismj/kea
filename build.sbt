
lazy val noPublishSettings = Seq(
    skip in publish := true,
    sources in doc := Seq.empty
)

lazy val commonScalacOptions = Seq(
  "-feature",
  "-deprecation",
  "-encoding", "utf8",
  "-language:postfixOps",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xcheckinit",
  "-Xlint",
  "-Xfatal-warnings",
  "-Ywarn-dead-code",
  "-Ywarn-value-discard")

lazy val buildSettings = Seq(
  name := "kea",
  organization in Global := "com.waioeka",
  scalaVersion in Global := "2.13.0"
)

lazy val scoverageSettings = Seq(
  coverageMinimum := 75,
  coverageFailOnMinimum := false,
  coverageExcludedPackages := "instances"
)

lazy val commonSettings = Seq(
  scalacOptions ++= commonScalacOptions,
  libraryDependencies ++= Seq(
    "com.chuusai" %% "shapeless" % "2.3.3",
    "org.typelevel" %% "cats-core" % "2.0.0-M4",
    "com.typesafe" % "config" % "1.3.4"
  ),
  fork in test := true
)

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := Function.const(false),
  sonatypeProfileName := "com.waioeka",
  publishTo := Some(
    if (isSnapshot.value) Opts.resolver.sonatypeSnapshots
    else Opts.resolver.sonatypeStaging
  ),
  autoAPIMappings := true,
  licenses := Seq("BSD-style" -> url("http://www.opensource.org/licenses/bsd-license.php")),
  homepage := Some(url("https://github.com/lewismj/kea")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/lewismj/kea"),
      "scm:git@github.com:lewismj/kea.git"
   )
 ),
 developers := List(
  Developer(id="lewismj", name="Michael Lewis", email="lewismj@mac.com", url=url("https://www.waioeka.com"))
 )
)

lazy val keaSettings = buildSettings ++ commonSettings ++ scoverageSettings

lazy val kea = project.in(file("."))
  .settings(moduleName := "root")
  .settings(noPublishSettings:_*)
  .aggregate(tests, core)

lazy val core = project.in(file("core"))
  .settings(moduleName := "kea-core")
  .settings(keaSettings:_*)
  .settings(publishSettings:_*)

lazy val tests = project.in(file("tests"))
  .dependsOn(core)
  .settings(moduleName := "kea-tests")
  .settings(keaSettings:_*)
  .settings(noPublishSettings:_*)
  .settings(
    coverageEnabled := false,
    testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oDF"),
    libraryDependencies ++= Seq(
      "org.scalatest"  %% "scalatest" % "3.0.8" % "test",
      "org.scalacheck" %% "scalacheck" % "1.14.0" % "test"
    )
  )
