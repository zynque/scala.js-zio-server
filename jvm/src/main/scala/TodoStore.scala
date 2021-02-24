
import zio._

class InMemoryTodoStore(itemsRef: Ref[Map[String, TodoItem]], nextIdRef: Ref[Int]) extends TodoStore.Service {

  def addItem(request: CreateTodoItem): UIO[TodoItem] =
    for {
      nextId <- nextIdRef.get
      id = nextId.toString
      items <- itemsRef.get
      newItem = TodoItem(id, request.title, request.description)
      _ <- itemsRef.set(items + (id -> newItem))
      _ <- nextIdRef.set(nextId + 1)
    } yield newItem

  def getItems(): UIO[List[TodoItem]] =
    for {
      items <- itemsRef.get
    } yield items.values.toList
  
  def removeItem(id: String): UIO[Unit] =
    for {
      items <- itemsRef.get
      _ <- itemsRef.set(items - id)
    } yield ()

}

object TodoStore {
  
  trait Service {
    def addItem(request: CreateTodoItem): UIO[TodoItem]
    def getItems(): UIO[List[TodoItem]]
    def removeItem(id: String): UIO[Unit]
  }

  def inMemory: UIO[Service] = {
    val initialItems = Map[String, TodoItem](
      "1" -> TodoItem("1", "Milk", "Get milk"),
      "2" -> TodoItem("2", "Chocolate", "Get chocolate")
    )

    for {
      itemsRef <- Ref.make(initialItems)
      idsRef <- Ref.make(0)
    } yield new InMemoryTodoStore(itemsRef, idsRef)    
  }

}