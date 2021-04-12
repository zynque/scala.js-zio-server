package org.zynque.example.todo

import io.circe._

object TodoItemCodec {

  implicit val todoItemEncoder: Encoder[TodoItem] =
    Encoder.forProduct3("title", "description", "completed")(
      t => (t.title, t.description, t.completed))

  implicit val todoItemDecoder: Decoder[TodoItem] =
    Decoder.forProduct3("title", "description", "completed")(
      (title, description, completed) => TodoItem(title, description, completed))

  implicit val todoItemPatchEncoder: Encoder[TodoItemPatch] =
    Encoder.forProduct3("title", "description", "completed")(
      t => (t.title, t.description, t.completed))

  implicit val todoItemPatchDecoder: Decoder[TodoItemPatch] =
    Decoder.forProduct3("title", "description", "completed")(
      (title, description, completed) => TodoItemPatch(title, description, completed))

  implicit val identifiedTodoItemEncoder: Encoder[IdentifiedTodoItem] =
    Encoder.forProduct4("id", "title", "description", "completed")(
      t => (t.id, t.title, t.description, t.completed))

  implicit val identifiedTodoItemDecoder: Decoder[IdentifiedTodoItem] =
    Decoder.forProduct4("id", "title", "description", "completed")(
      (id, title, description, completed) => IdentifiedTodoItem(id, title, description, completed))
}
