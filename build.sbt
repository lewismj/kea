import sbtassembly.AssemblyPlugin.autoImport._
import com.typesafe.sbt.pgp.PgpKeys._
import sbt.addCompilerPlugin



lazy val commonScalacOptions = Seq(
  "-feature",
  "-deprecation",
  "-encoding", "utf8",
  "-language:postfixOps",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xcheckinit",
  "-Xfuture",
  //"-Xlint",
  "-Xfatal-warnings",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-value-discard")

lazy val buildSettings = Seq(
  name := "kea",
  organization in Global := "com.waioeka",
  scalaVersion in Global := "2.12.3"
//  resolvers += Resolver.sonatypeRepo("releases"),
//  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
)

lazy val noPublishSettings = Seq(
  publish := (),
  publishLocal := (),
  publishArtifact := false,
  publishSigned := ()
)

lazy val credentialSettings = Seq(
  credentials ++= (for {
    username <- Option(System.getenv().get("SONATYPE_USERNAME"))
    password <- Option(System.getenv().get("SONATYPE_PASSWORD"))
  } yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password)).toSeq
)

lazy val scoverageSettings = Seq(
  coverageMinimum := 75,
  coverageFailOnMinimum := false,
  coverageExcludedPackages := "instances"
)

lazy val commonSettings = Seq(
  scalacOptions ++= commonScalacOptions,
  libraryDependencies ++= Seq(
    "com.chuusai" %% "shapeless" % "2.3.2",
    "org.typelevel" %% "cats" % "0.9.0",
    "com.typesafe" % "config" % "1.3.1"
  ),
  fork in test := true
)


lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := Function.const(false),
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("Snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("Releases" at nexus + "service/local/staging/deploy/maven2")
  },
  homepage := Some(url("https://github.com/lewismj/kea")),
  licenses := Seq("BSD-style" -> url("http://www.opensource.org/licenses/bsd-license.php")),
  scmInfo := Some(ScmInfo(url("https://github.com/lewismj/kea"), "scm:git:git@github.com:lewismj/kea.git")),
  autoAPIMappings := true,
  pomExtra := (
    <developers>
      <developer>
        <name>Michael Lewis</name>
        <url>@lewismj</url>
      </developer>
    </developers>
  )
) ++ credentialSettings


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
      "org.typelevel" %% "cats-laws" % "0.9.0",
      "org.scalatest"  %% "scalatest" % "3.0.0" % "test",
      "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"
    )
  )
