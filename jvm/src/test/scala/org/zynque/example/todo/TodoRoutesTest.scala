package org.zynque.example.todo

import zio._
import cats.syntax.option._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import zio.interop.catz._
import io.circe._
import org.http4s.circe._
import CirceEntityCodec._
import TodoItemCodec._

class TodoRoutesTest extends munit.FunSuite {
  val dsl = Http4sDsl[Task]
  import dsl._

  test("Get items") {
    val initialItems = TodoStore.sampleItems
    val expected = initialItems.values

    val (status, actual) =
      TodoRouteTester(initialItems)
        .testGet[List[IdentifiedTodoItem]]("/todo")

    assertEquals(status, Status.Ok)
    assertEquals(actual.toSet, expected.toSet)
  }

  test("Get item") {
    val initialItems = TodoStore.sampleItems
    val expected = initialItems("0")
    
    val (status, actual) =
      TodoRouteTester(initialItems)
        .testGet[IdentifiedTodoItem]("/todo/0")

    assertEquals(status, Status.Ok)
    assertEquals(actual, expected)
  }

  test("Get no item") {
    val (status, _) =
      TodoRouteTester(Map())
        .testGet[Unit]("/todo/0")

    assertEquals(status, Status.NotFound)
  }

  test("Create item") {
    val request = TodoItem("Milk", "Organic Whole Milk", false)
    val expectedItem = IdentifiedTodoItem("0", request.title, request.description, false)
    val tester = TodoRouteTester(Map())

    val (statusOfPost, responseOfPost) = tester.testPost[TodoItem, IdentifiedTodoItem](request, "/todo")
    val (statusOfGet, responseOfGet) = tester.testGet[IdentifiedTodoItem]("/todo/0")

    assertEquals(statusOfPost, Status.Ok)
    assertEquals(responseOfPost, expectedItem)
    assertEquals(statusOfGet, Status.Ok)
    assertEquals(responseOfGet, expectedItem)
  }

  test("Update item") {
    val original = TodoItem("Milk", "Organic Whole Milk", false)
    val update = TodoItem("Milk2", "Lactose-Free Organic Whole Milk", true)
    val originalItem = IdentifiedTodoItem("0", original.title, original.description, original.completed)
    val expectedItem = IdentifiedTodoItem("0", update.title, update.description, update.completed)
    val tester = TodoRouteTester(Map("0" -> originalItem))

    val (statusOfPatch, _) = tester.testPatch[TodoItem, Unit](update, "/todo/0")
    val (statusOfGet, responseOfGet) = tester.testGet[IdentifiedTodoItem]("/todo/0")

    assertEquals(statusOfPatch, Status.Ok)
    assertEquals(statusOfGet, Status.Ok)
    assertEquals(responseOfGet, expectedItem)
  }

  test("Update no item") {
    val update = TodoItem("Milk2", "Lactose-Free Organic Whole Milk", true)
    val tester = TodoRouteTester(Map())

    val (status, _) = tester.testPatch[TodoItem, Unit](update, "/todo/0")

    assertEquals(status, Status.NotFound)
  }

  test("Remove item") {
    val tester = TodoRouteTester(TodoStore.sampleItems)

    val (statusOfDelete, _) = tester.testDelete[Unit]("/todo/0")
    val (statusOfGet, remainingItems) = tester.testGet[List[IdentifiedTodoItem]]("/todo")

    assertEquals(statusOfDelete, Status.Ok)
    assertEquals(remainingItems, List(TodoStore.sampleItems("1")))
  }

  test("Remove no item") {
    val tester = TodoRouteTester(Map())

    val (statusOfDelete, _) = tester.testDelete[Unit]("/todo/0")
    
    assertEquals(statusOfDelete, Status.Ok)
  }

  class TodoRouteTester(initialItems: Map[String, IdentifiedTodoItem]) {
    val store = Runtime.default.unsafeRun(TodoStore.inMemory(initialItems))
    val routes = TodoRoutes(store).routes

    def testGet[A](uri: String)(implicit decoder: EntityDecoder[Task, A]): (Status, A) =
      Runtime.default.unsafeRun(
        for {
          uri <- Task.fromEither(Uri.fromString(uri))
          request = Request[Task](method = Method.GET, uri = uri)
          maybeResponse <- routes.run(request).value
          response = maybeResponse.getOrElse(throw Exception("got no response"))
          decodedResponse <- response.as[A]
        } yield (response.status, decodedResponse)
      )

    def testPost[A, B](requestBody: A,
                       uri: String)(implicit encoder: EntityEncoder[Task, A],
                                             decoder: EntityDecoder[Task, B]): (Status, B) =
      Runtime.default.unsafeRun(
        for {
          uri <- Task.fromEither(Uri.fromString(uri))
          request = Request[Task](method = Method.POST,
                                  uri = uri,
                                  body = encoder.toEntity(requestBody).body)
          maybeResponse <- routes.run(request).value
          response = maybeResponse.getOrElse(throw Exception("got no response"))
          decodedResponse <- response.as[B]
        } yield (response.status, decodedResponse)
      )

    def testPatch[A, B](requestBody: A,
                        uri: String)(implicit encoder: EntityEncoder[Task, A],
                                              decoder: EntityDecoder[Task, B]): (Status, B) =
      Runtime.default.unsafeRun(
        for {
          uri <- Task.fromEither(Uri.fromString(uri))
          request = Request[Task](method = Method.PATCH,
                                  uri = uri,
                                  body = encoder.toEntity(requestBody).body)
          maybeResponse <- routes.run(request).value
          response = maybeResponse.getOrElse(throw Exception("got no response"))
          decodedResponse <- response.as[B]
        } yield (response.status, decodedResponse)
      )
    
    def testDelete[A](uri: String)(implicit decoder: EntityDecoder[Task, A]): (Status, A) =
      Runtime.default.unsafeRun(
        for {
          uri <- Task.fromEither(Uri.fromString(uri))
          request = Request[Task](method = Method.DELETE,
                                  uri = uri)
          maybeResponse <- routes.run(request).value
          response = maybeResponse.getOrElse(throw Exception("got no response"))
          decodedResponse <- response.as[A]
        } yield (response.status, decodedResponse)
      )
  }

}
