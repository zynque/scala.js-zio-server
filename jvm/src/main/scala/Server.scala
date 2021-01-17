import zio.console._
import zio._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import zio.interop.catz._
import zio.interop.catz.implicits._
import org.http4s.server.blaze.BlazeServerBuilder

object Server {
  def start(host: String, port: Int, httpApp: HttpApp[Task]): Task[Unit] =
    for {
      runtime <- ZIO.runtime
      descriptor <- ZIO.descriptor
      _ <- {
        implicit val rt = runtime
        val executionContext = descriptor.executor.asEC
        BlazeServerBuilder[Task](executionContext)
          .bindHttp(port, host)
          .withHttpApp(httpApp)
          .serve
          .compile
          .drain
      }
    } yield ()
}
