package org.zynque.example.todo

enum ItemCommand {
  case Refresh
  case Create
  case UpdateTitle(id: String, title: String)
  case UpdateDescription(id: String, title: String)
  case UpdateCompleted(id: String, completed: Boolean)
  case Get(id: String)
  case Delete(id: String)
}
