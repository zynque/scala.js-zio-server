import zio.console._
import zio._
import org.http4s._
import org.http4s.implicits._
import org.http4s.dsl.Http4sDsl
import zio.interop.catz._
import org.http4s.server.Router

object Main extends zio.App {

  def run(args: List[String]) =
    appLogic.exitCode

  val httpApp = Router("/" -> routes).orNotFound

  val appLogic =
    for {
      _    <- putStrLn("Starting ZIO App")
      _    <- Server.start("localhost", 8080, httpApp)
    } yield ()
}
