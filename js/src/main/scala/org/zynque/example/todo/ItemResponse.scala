package org.zynque.example.todo

enum ItemResponse {
  case Ok
  case GotItems(items: List[IdentifiedTodoItem])
  case UpdatedItem(item: IdentifiedTodoItem)
  case Error(message: String)
}
