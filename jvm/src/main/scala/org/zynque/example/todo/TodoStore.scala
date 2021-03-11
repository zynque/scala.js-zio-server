package org.zynque.example.todo

import zio._

type TodoStore = Has[TodoStore.Service]

object TodoStore {
  
  trait Service {
    def addItem(request: CreateTodoItem): UIO[TodoItem]
    val getItems: UIO[List[TodoItem]]
    def removeItem(id: String): UIO[Unit]
  }

  def inMemory: UIO[Service] = {
    val initialItems = Map[String, TodoItem](
      "0" -> TodoItem("0", "Milk", "Get milk", false),
      "1" -> TodoItem("1", "Chocolate", "Get chocolate", false)
    )

    for {
      itemsRef <- Ref.make(initialItems)
      idsRef <- Ref.make(2)
    } yield new InMemoryTodoStore(itemsRef, idsRef)    
  }

}

class InMemoryTodoStore(itemsRef: Ref[Map[String, TodoItem]], nextIdRef: Ref[Int]) extends TodoStore.Service {

  def addItem(request: CreateTodoItem): UIO[TodoItem] =
    for {
      nextId <- nextIdRef.get
      id = nextId.toString
      items <- itemsRef.get
      newItem = TodoItem(id, request.title, request.description, false)
      _ <- itemsRef.set(items + (id -> newItem))
      _ <- nextIdRef.set(nextId + 1)
    } yield newItem

  val getItems: UIO[List[TodoItem]] =
    for {
      items <- itemsRef.get
    } yield items.values.toList
  
  def removeItem(id: String): UIO[Unit] =
    for {
      items <- itemsRef.get
      _ <- itemsRef.set(items - id)
    } yield ()

}
