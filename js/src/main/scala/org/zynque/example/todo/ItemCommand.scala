package org.zynque.example.todo

enum ItemCommand {
  case Refresh
  case Create
  case UpdateItem(id: String, item: TodoItem)
  case Get(id: String)
  case Delete(id: String)
}
