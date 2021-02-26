import zio._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import zio.interop.catz._

import io.circe._
import org.http4s.circe._
import org.http4s.implicits._
import org.http4s.circe.CirceEntityCodec._
import TodoItemCodec._

class TodoRoutes(todoStore: TodoStore.Service) {
  val dsl = Http4sDsl[Task]
  import dsl._

  val routes = HttpRoutes.of[Task] {
    case GET -> Root / "todo" =>
      for {
        items <- todoStore.getItems
        response <- Ok(items)
      } yield response
    case request @ POST -> Root / "todo" =>
      Ok("todo")
    case DELETE -> Root / "todo" / itemId =>
      for {
        _ <- todoStore.removeItem(itemId)
        response <- Ok("deleted")
      } yield response
  }
}
