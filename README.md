# scala.js-zio-server

A starter project containing an http server back-end and scala.js front-end leveraging zio, fs2, and http4s, running on Scala 3.

## Getting Started

### Prerequisites

Install jvm (I use: https://www.azul.com/downloads/zulu-community/?package=jdk)

Install sbt (https://www.scala-sbt.org/download.html)

### Run tests (scala.js and jvm)

    sbt test

### Build and run

Build the javascript

    sbt fastLinkJS

Run the server:

    sbt exampleJVM/run

Open page in a browser:

    http://localhost:8080/index.html

Or open individual routes:

    http://localhost:8080/test/ping
    http://localhost:8080/todo

### Notes on dependencies

I'd like to use the scala 3 version of zio, but zio-interop-cats and http4s libraries do not seem to have scala 3 versions at the time of this writing, so in order to avoid dependency conflicts I seem to be forced to import everything in dottycompat mode. Unfortunately that means ZLayer does not work because it depends on some macro stuff.
