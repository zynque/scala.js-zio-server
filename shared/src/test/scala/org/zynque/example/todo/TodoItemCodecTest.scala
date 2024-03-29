package org.zynque.example.todo

import org.junit.Test
import org.junit.Assert._
import io.circe._
import io.circe.syntax._
import cats.implicits._
import io.circe.parser.decode 

class TodoItemCodecTest extends munit.FunSuite {
  test("encode and decode TodoItem") {
    import TodoItemCodec._

    val item = TodoItem("thing", "it must be done", false)
    val jsonString = item.asJson.spaces2
    val decodedItem = decode[TodoItem](jsonString)

    assertEquals(decodedItem, item.asRight)
  }

  test("encode and decode TodoItemPatch") {
    import TodoItemCodec._

    val item = TodoItemPatch(Some("thing"), Some("it must be done"), None)
    val jsonString = item.asJson.spaces2
    val decodedItem = decode[TodoItemPatch](jsonString)

    assertEquals(decodedItem, item.asRight)
  }

  test("decode TodoItemPatch from blanks") {
    import TodoItemCodec._

    val jsonString = Json.obj(("title", Json.fromString("thing"))).spaces2
    val decodedItem = decode[TodoItemPatch](jsonString)

    assertEquals(decodedItem, TodoItemPatch(Some("thing"), None, None).asRight)
  }

  test("encode and decode IdentifiedTodoItem") {
    import TodoItemCodec._

    val item = IdentifiedTodoItem("a1", "thing", "it must be done", false)
    val jsonString = item.asJson.spaces2
    val decodedItem = decode[IdentifiedTodoItem](jsonString)

    assertEquals(decodedItem, item.asRight)
  }

}
