package org.zynque.example.todo

import zio._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import zio.interop.catz._
import io.circe._
import org.http4s.circe._
import CirceEntityCodec._
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
    case GET -> Root / "todo" / itemId =>
      for {
        maybeItem <- todoStore.getItem(itemId)
        response <- maybeItem match {
          case Some(item) => Ok(item)
          case None => NotFound(())
        }
      } yield response
    case request @ POST -> Root / "todo" =>
      for {
        todoItem <- request.as[TodoItem]
        createdItem <- todoStore.createItem(todoItem)
        response <- Ok(createdItem)
      } yield response
    case request @ PATCH -> Root / "todo" / itemId =>
      for {
        todoItem <- request.as[TodoItemPatch]
        errorOrUnit <- todoStore.updateItem(itemId, todoItem).either
        response <- errorOrUnit match {
          case Left(TodoStoreError.ItemDoesNotExist(id)) => NotFound(())
          case _ => Ok(())
        }
      } yield response
    case DELETE -> Root / "todo" / itemId =>
      for {
        _ <- todoStore.removeItem(itemId)
        response <- Ok(())
      } yield response
  }
}
