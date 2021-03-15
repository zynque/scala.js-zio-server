package org.zynque.example.todo

import zio._

type TodoStore = Has[TodoStore.Service]

object TodoStore {
  
  trait Service {
    def addItem(request: CreateTodoItem): UIO[TodoItem]
    def getItem(id: String): UIO[Option[TodoItem]]
    val getItems: UIO[List[TodoItem]]
    def removeItem(id: String): UIO[Unit]
  }

  def inMemory(initialItems: Map[String, TodoItem] = Map()): UIO[Service] = {
    for {
      itemsRef <- Ref.make(initialItems)
      idsRef <- Ref.make(initialItems.size)
    } yield new InMemoryTodoStore(itemsRef, idsRef)    
  }

  val sampleItems = Map[String, TodoItem](
      "0" -> TodoItem("0", "Milk", "Get milk", false),
      "1" -> TodoItem("1", "Chocolate", "Get chocolate", false)
    )
}

class InMemoryTodoStore(itemsRef: Ref[Map[String, TodoItem]], nextIdRef: Ref[Int]) extends TodoStore.Service {
  
  val getItems: UIO[List[TodoItem]] =
    for {
      items <- itemsRef.get
    } yield items.values.toList
  
  def getItem(id: String): UIO[Option[TodoItem]] =
    for {
      items <- itemsRef.get
    } yield items.get(id)

  def addItem(request: CreateTodoItem): UIO[TodoItem] =
    for {
      nextId <- nextIdRef.get
      id = nextId.toString
      items <- itemsRef.get
      newItem = TodoItem(id, request.title, request.description, false)
      _ <- itemsRef.set(items + (id -> newItem))
      _ <- nextIdRef.set(nextId + 1)
    } yield newItem

  def removeItem(id: String): UIO[Unit] =
    for {
      items <- itemsRef.get
      _ <- itemsRef.set(items - id)
    } yield ()

}
