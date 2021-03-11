package org.zynque.example.fileserver

import zio._
import zio.interop.catz._
import org.http4s._
import org.http4s.dsl.Http4sDsl

object StaticFileRoute {
  val dsl = Http4sDsl[Task]
  import dsl._

  def routeForFile(fileDescriptor: FileToBeServed): Task[HttpRoutes[Task]] =
    for {
      fileStringContent <- FileStreaming.loadFileString(fileDescriptor.filePathOnDisk)
    } yield HttpRoutes.of[Task] {
      case GET -> Root / fileDescriptor.fileNameOnServer =>
        Ok(fileStringContent).map(_.withContentType(fileDescriptor.contentType))
    }
}
