val scala3Version = "3.0.0-M3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "scala.js-zio-server",
    version := "0.0.1",

    scalaVersion := scala3Version,

    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",
    libraryDependencies += ("dev.zio" %% "zio" % "1.0.3").withDottyCompat(scalaVersion.value)
  )
