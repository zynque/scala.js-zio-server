import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.ext._
import com.raquo.laminar.api.L._
import com.raquo.airstream.web._
import scala.util._
import zio._
import zio.console._
import io.circe.scalajs._
import org.zynque.example.todo._
import TodoItemCodec._
import io.circe._
import io.circe.syntax._
import io.circe.parser._

object TodoApp extends zio.App {

  def run(args: List[String]) =
    appLogic.exitCode

  val appLogic =
    for {
      _ <- putStrLn("Starting")
      todoRequest <- IO.fromFuture(ec => Ajax.get("/todo"))
      todosString = todoRequest.responseText
      _ <- putStrLn(s"Unparsed Response: $todosString")
      todos <- IO.fromEither(decode[List[TodoItem]](todosString))
      _ <- putStrLn(s"Parsed Response: $todos")
      renderedTodos = renderTodoItems(todos)
      _ <- IO.effect(render(document.body, renderedTodos))
    } yield ()

  def renderTodoItems(items: List[TodoItem]) =
    div(items.map(renderTodoItem))

  def renderTodoItem(item: TodoItem) =
    div(
      span(item.id),
      input(
        value := item.title
      ),
      input(
        value := item.description
      ),
      input(
        typ := "checkbox",
        checked := item.completed
      )
    )
}
