package org.zynque.example

import zio._
import zio.console._
import zio.interop.catz._
import cats.implicits._
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.Router
import fileserver._
import todo._

object Main extends zio.App {

  def run(args: List[String]) =
    appLogic.exitCode

  val appLogic =
    for {
      _ <- putStrLn("Loading Files")
      indexHtmlRoute <- StaticFileRoute.buildRouteForFile(FilesToBeServed.indexHtml)
      mainJsRoute <- StaticFileRoute.buildRouteForFile(FilesToBeServed.mainJs)
      mainJsSourceMapRoute <- StaticFileRoute.buildRouteForFile(FilesToBeServed.mainJsSourceMap)
      fileRoutes = indexHtmlRoute <+> mainJsRoute <+> mainJsSourceMapRoute
      
      _ <- putStrLn("Building Server")
      todoStore <- TodoStore.inMemory
      todoRoutes = TodoRoutes(todoStore)
      httpApp = Router("/" -> (Routes.routes <+> fileRoutes <+> todoRoutes.routes)).orNotFound
      
      _ <- putStrLn("Starting Server")
      _ <- Server.start("localhost", 8080, httpApp)
    } yield ()
}