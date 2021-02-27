package org.zynque.example

import zio._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import zio.interop.catz._

object Routes {
  val dsl = Http4sDsl[Task]
  import dsl._

  val routes = HttpRoutes.of[Task] {
    case GET -> Root / "test" / pathVariable =>
      Ok(s"ok $pathVariable")
  }
}
