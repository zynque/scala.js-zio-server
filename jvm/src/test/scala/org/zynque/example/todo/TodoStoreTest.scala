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

    assertEquals(actual.toSet, expected.toSet)
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

    assertEquals(actual, expected.some)
  }

  test("Get no item") {   
    val actual = Runtime.default.unsafeRun(
      for {
        store <- TodoStore.inMemory(Map())
        item <- store.getItem("0")
      } yield item
    )

    assertEquals(actual, Option.empty[IdentifiedTodoItem])
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

    assertEquals(createdItem, expectedItem)
    assertEquals(items.toSet, Set(expectedItem))
  }

  test("Update item") {
    val original = TodoItem("Milk", "Organic Whole Milk", false)
    val update = TodoItem("Milk2", "Lactose-Free Organic Whole Milk", true)
    val originalItem = IdentifiedTodoItem("0", original.title, original.description, original.completed)
    val expectedItem = IdentifiedTodoItem("0", update.title, update.description, update.completed)
    
    val items = Runtime.default.unsafeRun(
      for {
        store <- TodoStore.inMemory(Map("0" -> originalItem))
        _ <- store.updateItem("0", update)
        items <- store.getItems
      } yield items
    )

    assertEquals(items.toSet, Set(expectedItem))
  }

  test("Update no item") {
    val update = TodoItem("Milk2", "Lactose-Free Organic Whole Milk", true)
    
    val (error, items) = Runtime.default.unsafeRun(
      for {
        store <- TodoStore.inMemory(Map())
        error <- store.updateItem("0", update).flip
        items <- store.getItems
      } yield (error, items)
    )

    assertEquals(error, TodoStoreError.ItemDoesNotExist("0"))
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

    assertEquals(actual, expected)
  }

  test("Remove no item") {
    val items = TodoStore.sampleItems
    val expected = items.values
    
    val actual = Runtime.default.unsafeRun(
      for {
        store <- TodoStore.inMemory(items)
        _ <- store.removeItem("5")
        items <- store.getItems
      } yield items
    )

    assertEquals(actual.toSet, expected.toSet)
  }
}
