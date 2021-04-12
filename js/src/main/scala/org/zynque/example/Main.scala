package org.zynque.example

import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.ext._
import com.raquo.laminar.api.L._
import com.raquo.airstream.web._
import scala.util._
import io.circe.scalajs._
import io.circe._
import io.circe.syntax._
import io.circe.parser._
import org.zynque.example.todo._
import TodoItemCodec._


object Main {

  def main(args: Array[String]): Unit =
    render(document.body, renderTodoItems)

  val todoService = TodoService.service()

  def renderTodoItems =
    div(
      button(
        "Refresh",
        onClick.map(_ => ItemCommand.Refresh) --> todoService.requests
      ),
      button(
        "+",
        borderRadius("40%"),
        onClick.map(_ => ItemCommand.Create) --> todoService.requests
      ),
      todoService.requests --> anyLogger,
      todoService.responses --> anyLogger,
      children <-- renderedItemSignal,
    )

  val anyLogger = Observer[Any](c => dom.console.log(c))

  val renderedItemStream: EventStream[Seq[Node]] =
    todoService.responses
               .collect { case ItemResponse.GotItems(items) => items }
               .split(_.id)(renderTodoItem)
               
  val renderedItemSignal: Signal[Seq[Node]] = renderedItemStream.startWith(Seq())

  def renderCirceError(error: io.circe.Error) =
    List(div(span(error.getMessage)))

  def renderTodoItem(id: String, initialItem: IdentifiedTodoItem, itemStream: EventStream[IdentifiedTodoItem]): Div =
    div(
      span(id),
      input(
        value <-- itemStream.map(_.title),
        onChange.mapToValue
          .map(title => ItemCommand.UpdateTitle(id, title)) -->
            todoService.requests
      ),
      input(
        value <-- itemStream.map(_.description),
        onChange.mapToValue
          .map(description => ItemCommand.UpdateDescription(id, description)) -->
            todoService.requests
      ),
      input(
        typ := "checkbox",
        checked <-- itemStream.map(_.completed),
        onClick.mapToChecked
          .map(checked => ItemCommand.UpdateCompleted(id, checked)) -->
            todoService.requests
      ),
      button(
        "X",
        borderRadius("40%"),
        onClick.map(_ => ItemCommand.Delete(id)) --> todoService.requests
      )
    )
}
