package org.zynque.example.todo

import com.raquo.laminar.api.L._
import com.raquo.airstream.web._
import io.circe._
import io.circe.syntax._
import io.circe.parser._
import TodoItemCodec._
import org.scalajs.dom.ext._


trait AirstreamService[TRequest, TResponse] {
  def requests: EventBus[TRequest]
  def responses: EventStream[TResponse] 
}

object TodoService {

  def service() = new AirstreamService[ItemCommand, ItemResponse] {
    val requests = new EventBus[ItemCommand]
    
    val responses = for {
      event <- requests.events
      result <- event match {
        case ItemCommand.Refresh => getItems()
        case ItemCommand.Create => createItem()
        case ItemCommand.Delete(id) => deleteItem(id)
        case ItemCommand.UpdateTitle(id, title) => updateTitle(id, title)
        case ItemCommand.UpdateDescription(id, description) => updateTitle(id, description)
        case ItemCommand.UpdateCompleted(id, completed) => updateCompleted(id, completed)
        case _ => EventStream.empty
      }
    } yield result
  }

  private def getItems(): EventStream[ItemResponse] = for {
    httpResponse <- AjaxEventStream.get("/todo")
    decoded = decode[List[IdentifiedTodoItem]](httpResponse.responseText)
  } yield decoded match {
    case Left(err) => ItemResponse.Error(err.toString)
    case Right(items) => ItemResponse.GotItems(items)
  }

  private val newTodoString = TodoItem("", "", false).asJson.noSpaces
  private val newTodoAjaxData = Ajax.InputData.str2ajax(newTodoString)

  private def createItem(): EventStream[ItemResponse] = for {
    httpResponse <- AjaxEventStream.post(url = "/todo", data = newTodoAjaxData)
    decoded = decode[IdentifiedTodoItem](httpResponse.responseText)
  } yield decoded match {
    case Left(err) => ItemResponse.Error(err.toString)
    case Right(item) => ItemResponse.UpdatedItem(item)
  }

  private def deleteItem(id: String): EventStream[ItemResponse] = for {
    httpResponse <- AjaxEventStream.delete(url = s"/todo/$id")
    decoded = decode[Unit](httpResponse.responseText)
  } yield decoded match {
    case Left(err) => ItemResponse.Error(err.toString)
    case Right(_) => ItemResponse.Ok
  }

  private def updateTitle(id: String, title: String): EventStream[ItemResponse] =
    patchItem(id, Json.obj(("title", Json.fromString(title))))

  private def updateDescription(id: String, description: String): EventStream[ItemResponse] =
    patchItem(id, Json.obj(("description", Json.fromString(description))))

  private def updateCompleted(id: String, completed: Boolean): EventStream[ItemResponse] =
    patchItem(id, Json.obj(("completed", Json.fromBoolean(completed))))

  private def patchItem(id: String, json: Json): EventStream[ItemResponse] = {
    val todoString = json.noSpaces
    val ajaxData = Ajax.InputData.str2ajax(todoString)
    for {
      httpResponse <- AjaxEventStream.patch(url = s"/todo/$id", data = ajaxData)
      decoded = decode[Unit](httpResponse.responseText)
    } yield decoded match {
      case Left(err) => ItemResponse.Error(err.toString)
      case Right(_) => ItemResponse.Ok
    }
  }

}
