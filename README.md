# scala.js-zio-server

A starter project containing an http server back-end and scala.js front-end leveraging zio, fs2, and http4s

## Getting Started

### Prerequisites

Install jvm (I use: https://www.azul.com/downloads/zulu-community/?package=jdk)

Install sbt (https://www.scala-sbt.org/download.html)

### Build and run

Build the javascript

    sbt fastLinkJS

Run the server:

    sbt serverJVM/run

Open index.html in a browser:

    http://localhost:8080/index.html
