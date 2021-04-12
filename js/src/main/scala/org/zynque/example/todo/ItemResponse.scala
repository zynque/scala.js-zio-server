package org.zynque.example.todo

enum ItemResponse {
  case GotItems(items: List[IdentifiedTodoItem])
  case CreatedItem(item: IdentifiedTodoItem)
  case PatchedItem(id: String)
  case DeletedItem(id: String)
  case Error(message: String)
}
