package org.zynque.example.todo

final case class TodoItem(title: String, description: String, completed: Boolean)

final case class TodoItemPatch(title: Option[String], description: Option[String], completed: Option[Boolean])

final case class IdentifiedTodoItem(id: String, title: String, description: String, completed: Boolean)
