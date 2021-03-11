package org.zynque.example.todo

import io.circe._

object TodoItemCodec {

  implicit val createTodoItemEncoder: Encoder[CreateTodoItem] =
    Encoder.forProduct2("title", "description")(
      t => (t.title, t.description))

  implicit val createTodoItemDecoder: Decoder[CreateTodoItem] =
    Decoder.forProduct2("title", "description")(
      (title, description) => CreateTodoItem(title, description))

  implicit val todoItemEncoder: Encoder[TodoItem] =
    Encoder.forProduct4("id", "title", "description", "completed")(
      t => (t.id, t.title, t.description, t.completed))

  implicit val todoItemDecoder: Decoder[TodoItem] =
    Decoder.forProduct4("id", "title", "description", "completed")(
      (id, title, description, completed) => TodoItem(id, title, description, completed))
}
