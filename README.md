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
