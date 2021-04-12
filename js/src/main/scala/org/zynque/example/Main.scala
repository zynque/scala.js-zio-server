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

  def main(args: Array[String]): Unit = {
    documentEvents.onDomContentLoaded.foreach { _ =>
      render(document.body, renderTodoItems)
      todoService.requests.emit(ItemCommand.Refresh)
    }(unsafeWindowOwner)
  }

  val todoService = TodoService.service()

  def renderTodoItems =
    div(
      button(
        "+",
        borderRadius("40%"),
        onClick.map(_ => ItemCommand.Create) --> todoService.requests
      ),
      children <-- renderedItemSignal,
    )

  val itemsStream: Signal[Map[String, IdentifiedTodoItem]] =
    todoService.responses.foldLeft(Map[String, IdentifiedTodoItem]()) { (items, response) =>
      response match {
        case ItemResponse.GotItems(gotItems) => gotItems.map(i => (i.id, i)).toMap
        case ItemResponse.CreatedItem(item) => items.updated(item.id, item)
        case ItemResponse.DeletedItem(id) => items.removed(id)
        case _ => items
      }
    }

  val renderedItemSignal = itemsStream.map(_.values.toList.sortBy(_.id)).split(_.id)(renderTodoItem)

  def renderCirceError(error: io.circe.Error) =
    List(div(span(error.getMessage)))

  def renderTodoItem(id: String, initialItem: IdentifiedTodoItem, itemStream: Signal[IdentifiedTodoItem]): Div =
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
