package org.zynque.example.todo

import org.junit.Test
import org.junit.Assert._
import io.circe._
import io.circe.syntax._
import cats.implicits._
import io.circe.parser.decode 

class TodoItemCodecTest extends munit.FunSuite {
  test("encode and decode IdentifiedTodoItem") {
    import TodoItemCodec._

    val item = IdentifiedTodoItem("a1", "thing", "it must be done", false)
    val jsonString = item.asJson.spaces2
    val decodedItem = decode[IdentifiedTodoItem](jsonString)

    assertEquals(item.asRight, decodedItem)
  }

  test("encode and decode TodoItem") {
    import TodoItemCodec._

    val item = TodoItem("thing", "it must be done", false)
    val jsonString = item.asJson.spaces2
    val decodedItem = decode[TodoItem](jsonString)

    assertEquals(item.asRight, decodedItem)
  }
}
