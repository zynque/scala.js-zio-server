import zio.console._
import zio._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import zio.interop.catz._
import zio.interop.catz.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import scala.concurrent.ExecutionContext.global

object Server {
  implicit val runtime: Runtime[ZEnv] = Runtime.default

  def start(host: String, port: Int, httpApp: HttpApp[Task]): Task[Unit] =
    BlazeServerBuilder[Task](global)
      .bindHttp(port, host)
      .withHttpApp(httpApp)
      .serve
      .compile
      .drain
}
