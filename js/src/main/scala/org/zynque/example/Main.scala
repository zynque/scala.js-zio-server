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

  val renderedItemStream: EventStream[Seq[Node]] = todoService.responses.collect { case ItemResponse.GotItems(items) => items.map(renderTodoItem) }
  val renderedItemSignal: Signal[Seq[Node]] = renderedItemStream.startWith(Seq())

  def renderCirceError(error: io.circe.Error) =
    List(div(span(error.getMessage)))

  def renderTodoItem(item: IdentifiedTodoItem) =
    div(
      span(item.id),
      input(
        value := item.title,
        onChange.mapToValue
          .map(title => ItemCommand.UpdateItem(item.id, TodoItem(title, item.description, item.completed))) -->
            todoService.requests
      ),
      input(
        value := item.description,
        onChange.mapToValue
          .map(description => ItemCommand.UpdateItem(item.id, TodoItem(item.title, description, item.completed))) -->
            todoService.requests
      ),
      input(
        typ := "checkbox",
        checked := item.completed,
        onClick.mapToChecked
          .map(checked => ItemCommand.UpdateItem(item.id, TodoItem(item.title, item.description, checked))) -->
            todoService.requests
      ),
      button(
        "X",
        borderRadius("40%"),
        onClick.map(_ => ItemCommand.Delete(item.id)) --> todoService.requests
      )
    )
}
