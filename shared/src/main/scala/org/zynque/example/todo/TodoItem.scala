package org.zynque.example.todo

final case class CreateTodoItem(title: String, description: String)

final case class TodoItem(id: String, title: String, description: String)
