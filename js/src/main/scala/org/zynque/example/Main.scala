package org.zynque.example

import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.ext._
import com.raquo.laminar.api.L._
import com.raquo.airstream.web._
import scala.util._
import zio._
import zio.console._
import io.circe.scalajs._
import io.circe._
import io.circe.syntax._
import io.circe.parser._
import org.zynque.example.todo._
import TodoItemCodec._


object Main extends zio.App {

  def run(args: List[String]) =
    appLogic.exitCode

  val appLogic =
    for {
      _ <- putStrLn("Starting")
      _ <- IO.effect(render(document.body, renderTodoItems))
    } yield ()

  def renderTodoItems =
    div(
      button(
        "Refresh",
        onClick.map(_ => ItemCommand.Refresh) --> eventBus.writer
      ),
      button(
        "+",
        borderRadius("40%"),
        onClick.map(_ => ItemCommand.Create) --> eventBus.writer
      ),
      eventBus.events --> anyLogger,
      get --> anyLogger,
      refreshes --> anyLogger,
      children <-- renderedRefreshes
    )

  val eventBus = new EventBus[ItemCommand]

  val anyLogger = Observer[Any](c => dom.console.log(c))

  val newTodoString = TodoItem("", "", false).asJson.noSpaces
  val newTodoAjaxData = Ajax.InputData.str2ajax(newTodoString)
  val postNewTodo = IO.fromFuture(_ => Ajax.post(url = "/todo", data = newTodoAjaxData))

  val get = AjaxEventStream.get("/todo").map(a => a.responseText)

  val refreshEvents = eventBus
    .events
    .filter { case ItemCommand.Refresh => true; case _ => false }
    .startWith(ItemCommand.Refresh)

  val refreshes = for {
    _ <- refreshEvents
    todosString <- AjaxEventStream.get("/todo").map(a => a.responseText)
  } yield decode[List[IdentifiedTodoItem]](todosString)

  val refreshesWithInit = refreshes.startWith(Right(List()))

  val renderedRefreshes = refreshesWithInit.map {
    case Left(err) => renderCirceError(err)
    case Right(items) => items.map(renderTodoItem)
  }

  def renderCirceError(error: io.circe.Error) =
    List(div(span(error.getMessage)))

  def renderTodoItem(item: IdentifiedTodoItem) =
    div(
      span(item.id),
      input(
        value := item.title,
        onChange.mapToValue
          .map(title => ItemCommand.UpdateTitle(item.id, title)) -->
            eventBus.writer
      ),
      input(
        value := item.description,
        onChange.mapToValue
          .map(description => ItemCommand.UpdateDescription(item.id, description)) -->
            eventBus.writer
      ),
      input(
        typ := "checkbox",
        checked := item.completed,
        onClick.mapToChecked
          .map(checked => ItemCommand.UpdateStatus(item.id, checked)) -->
            eventBus.writer
      ),
      button(
        "X",
        borderRadius("40%"),
        onClick.map(_ => ItemCommand.Delete(item.id)) --> eventBus.writer
      )
    )
}
