val scala3Version = "3.0.0-M3"
val http4sVersion = "1.0.0-M10"

lazy val root = project
  .in(file("."))
  .aggregate(server.js, server.jvm)
  .settings(
    publish := {},
    publishLocal := {},
  )

lazy val server = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .in(file("."))
  .settings(
    name := "scala.js-zio-server",
    version := "0.0.1",
    scalaVersion := scala3Version
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "com.novocode" % "junit-interface" % "0.11" % "test",

      "ch.qos.logback" % "logback-classic" % "1.2.3",
      ("dev.zio" %% "zio" % "1.0.3").withDottyCompat(scalaVersion.value),
      ("org.http4s" %% "http4s-dsl" % http4sVersion).withDottyCompat(scalaVersion.value),
      ("org.http4s" %% "http4s-blaze-server" % http4sVersion).withDottyCompat(scalaVersion.value),
      ("dev.zio" %% "zio-interop-cats" % "2.2.0.1").withDottyCompat(scalaVersion.value)
    )
  )
  .jsSettings(
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies += ("org.scala-js" %%% "scalajs-dom" % "1.1.0").withDottyCompat(scalaVersion.value)
  )
