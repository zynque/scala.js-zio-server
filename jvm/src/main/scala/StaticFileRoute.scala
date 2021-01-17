import zio.blocking._
import zio.{blocking => _, _}
import org.http4s._
import org.http4s.dsl.Http4sDsl
import zio.interop.catz._
import java.io.File
import cats.effect.Blocker
import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService}
import cats.implicits._
import org.http4s.headers.`Content-Type`

object StaticFileRoute {
  val dsl = Http4sDsl[Task]
  import dsl._

  def buildRouteForFile(fileDescriptor: FileToBeServed): Task[HttpRoutes[Task]] =
    for {
      fileBytes <- FileStreaming.loadFileBytes(fileDescriptor.filePathOnDisk)
    } yield HttpRoutes.of[Task] {
      case GET -> Root / fileDescriptor.fileNameOnServer =>
        Ok(FileStreaming.streamFileContent(fileBytes)).map(_.withContentType(`Content-Type`(MediaType.text.html)))
    }
}
