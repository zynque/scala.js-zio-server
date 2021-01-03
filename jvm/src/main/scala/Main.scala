import zio.console._

object Main extends zio.App {

  def run(args: List[String]) =
    appLogic.exitCode

  val appLogic =
    for {
      _    <- putStrLn("Starting ZIO App")
    } yield ()
}
