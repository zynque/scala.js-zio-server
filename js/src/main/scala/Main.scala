import org.scalajs.dom
import org.scalajs.dom.document

object AnApp:

  def main(args: Array[String]): Unit =
    appendParagraph(document.body, "Hello from scala.js!")
  
  def appendParagraph(targetNode: dom.Node, text: String): Unit =
    val p = document.createElement("p")
    p.textContent = text
    targetNode.appendChild(p)
