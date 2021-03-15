package org.zynque.example.todo

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

  test("Create item") {
    val request = TodoItem("Milk", "Organic Whole Milk", false)
    val expectedItem = IdentifiedTodoItem("0", request.title, request.description, false)
    
    val (createdItem, items) = Runtime.default.unsafeRun(
      for {
        store <- TodoStore.inMemory()
        createdItem <- store.createItem(request)
        items <- store.getItems
      } yield (createdItem, items)
    )

    assertEquals(expectedItem, createdItem)
    assertEquals(Set(expectedItem), items.toSet)
  }

  test("Update item") {
    val original = TodoItem("Milk", "Organic Whole Milk", false)
    val update = TodoItem("Milk2", "Lactose-Free Organic Whole Milk", true)
    val originalItem = IdentifiedTodoItem("0", original.title, original.description, false)
    val expectedItem = IdentifiedTodoItem("0", update.title, update.description, true)
    
    val items = Runtime.default.unsafeRun(
      for {
        store <- TodoStore.inMemory(Map("0" -> originalItem))
        _ <- store.updateItem("0", update)
        items <- store.getItems
      } yield items
    )

    assertEquals(Set(expectedItem), items.toSet)
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
