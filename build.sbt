val scala3Version = "3.0.0-RC1"
val http4sVersion = "0.22.0-M6"
val circeVersion = "0.14.0-M4"

lazy val root = project
  .in(file("."))
  .aggregate(example.js, example.jvm)
  .settings(
    publish := {},
    publishLocal := {},
  )

lazy val example = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .in(file("."))
  .settings(
    name := "scala.js-zio-server",
    version := "0.0.1",
    scalaVersion := scala3Version,
    scalacOptions ++= Seq(
      "-Xfatal-warnings"
    ),
    libraryDependencies ++= Seq(
      "org.scalameta" %%% "munit" % "0.7.22" % Test,
      "io.circe" %%% "circe-core" % circeVersion,
      "io.circe" %%% "circe-parser" % circeVersion
    ),
    testFrameworks += new TestFramework("munit.Framework")
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "dev.zio" %%% "zio" % "1.0.5",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "dev.zio" %% "zio-interop-cats" % "2.4.0.0",
      "org.http4s" %% "http4s-circe" % http4sVersion,
    )
  )
  .jsSettings(
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      ("org.scala-js" %%% "scalajs-dom" % "1.1.0").withDottyCompat(scalaVersion.value),
      ("com.raquo" %%% "laminar"   % "0.12.1").withDottyCompat(scalaVersion.value),
      ("com.raquo" %%% "airstream" % "0.12.0").withDottyCompat(scalaVersion.value)
    ),
    Test / scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) }
  )
