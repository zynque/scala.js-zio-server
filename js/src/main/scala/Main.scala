import org.scalajs.dom
import org.scalajs.dom.document
import com.raquo.laminar.api.L._

object TodoApp:

  def main(args: Array[String]): Unit =
    render(document.body, contents)

  val contents = div(
    span("Item: "),
    input(
      placeholder := "Label"
    ),
    input(
      placeholder := "Description"
    ),
    input(
      typ := "checkbox",
      checked := true
    )
  )
