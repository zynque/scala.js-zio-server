import org.zynque.example.todo._
import zio._
import cats.syntax.option._

class TodoStoreTest extends munit.FunSuite {

  test("Get items") {
    val items = TodoStore.sampleItems
    val expected = items.values
    
    val actual = Runtime.default.unsafeRun(
      for {
        store <- TodoStore.inMemory(items)
        items <- store.getItems
      } yield items
    )

    assertEquals(expected.toSet, actual.toSet)
  }

  test("Get item") {
    val items = TodoStore.sampleItems
    val expected = items("0")
    
    val actual = Runtime.default.unsafeRun(
      for {
        store <- TodoStore.inMemory(items)
        item <- store.getItem("0")
      } yield item
    )

    assertEquals(expected.some, actual)
  }

  test("Add item") {
    val request = CreateTodoItem("Milk", "Organic Whole Milk")
    val expectedItem = TodoItem("0", request.title, request.description, false)
    
    val actual = Runtime.default.unsafeRun(
      for {
        store <- TodoStore.inMemory()
        _ <- store.addItem(request)
        items <- store.getItems
      } yield items
    )

    assertEquals(Set(expectedItem), actual.toSet)
  }

  test("Remove item") {
    val items = TodoStore.sampleItems
    val expected = List(items("1"))
    
    val actual = Runtime.default.unsafeRun(
      for {
        store <- TodoStore.inMemory(items)
        _ <- store.removeItem("0")
        items <- store.getItems
      } yield items
    )

    assertEquals(expected, actual)
  }

}
