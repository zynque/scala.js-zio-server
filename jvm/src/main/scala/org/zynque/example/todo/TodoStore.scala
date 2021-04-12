package org.zynque.example.todo

import zio._

enum TodoStoreError {
  case ItemDoesNotExist(itemId: String)
}

type TodoStore = Has[TodoStore.Service]

object TodoStore {
  
  trait Service {
    val getItems: UIO[List[IdentifiedTodoItem]]
    def getItem(id: String): UIO[Option[IdentifiedTodoItem]]
    def createItem(request: TodoItem): UIO[IdentifiedTodoItem]
    def updateItem(id: String, request: TodoItemPatch): IO[TodoStoreError, Unit]
    def removeItem(id: String): UIO[Unit]
  }

  def inMemory(initialItems: Map[String, IdentifiedTodoItem] = Map()): UIO[Service] = {
    for {
      itemsRef <- Ref.make(initialItems)
      idsRef <- Ref.make(initialItems.size)
    } yield new InMemoryTodoStore(itemsRef, idsRef)    
  }

  val sampleItems = Map[String, IdentifiedTodoItem](
      "0" -> IdentifiedTodoItem("0", "Milk", "Get milk", false),
      "1" -> IdentifiedTodoItem("1", "Chocolate", "Get chocolate", false)
    )
}

class InMemoryTodoStore(itemsRef: Ref[Map[String, IdentifiedTodoItem]], nextIdRef: Ref[Int]) extends TodoStore.Service {
  
  val getItems: UIO[List[IdentifiedTodoItem]] =
    for {
      items <- itemsRef.get
    } yield items.values.toList
  
  def getItem(id: String): UIO[Option[IdentifiedTodoItem]] =
    for {
      items <- itemsRef.get
    } yield items.get(id)

  def createItem(request: TodoItem): UIO[IdentifiedTodoItem] =
    for {
      nextId <- nextIdRef.get
      id = nextId.toString
      items <- itemsRef.get
      newItem = IdentifiedTodoItem(id, request.title, request.description, request.completed)
      _ <- itemsRef.set(items + (id -> newItem))
      _ <- nextIdRef.set(nextId + 1)
    } yield newItem

  def updateItem(id: String, request: TodoItemPatch): IO[TodoStoreError, Unit] =
    for {
      items <- itemsRef.get
      _ <- items.get(id) match {
        case Some(item) =>
          val updatedItem = IdentifiedTodoItem(id, request.title.getOrElse(item.title),
                                                   request.description.getOrElse(item.description),
                                                   request.completed.getOrElse(item.completed))
          itemsRef.set(items + (id -> updatedItem))
        case None =>
          IO.fail(TodoStoreError.ItemDoesNotExist(id))
      }
    } yield ()

  def removeItem(id: String): UIO[Unit] =
    for {
      items <- itemsRef.get
      _ <- itemsRef.set(items - id)
    } yield ()

}
