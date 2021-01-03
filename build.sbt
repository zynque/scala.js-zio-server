val scala3Version = "3.0.0-M3"

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
    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",
    libraryDependencies += ("dev.zio" %% "zio" % "1.0.3").withDottyCompat(scalaVersion.value)
  )
  .jsSettings(
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies += ("org.scala-js" %%% "scalajs-dom" % "1.1.0").withDottyCompat(scalaVersion.value),
    libraryDependencies += ("com.lihaoyi" %%% "scalatags" % "0.8.5").withDottyCompat(scalaVersion.value)
  )
