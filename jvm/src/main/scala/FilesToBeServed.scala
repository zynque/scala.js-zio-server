import org.http4s.headers.`Content-Type`
import org.http4s.MediaType

final case class FileToBeServed(filePathOnDisk: String, fileNameOnServer: String, contentType: `Content-Type`)

// TODO: support path to fully optimized js files
object FilesToBeServed {
  val indexHtml = FileToBeServed("./index.html", "index.html", `Content-Type`(MediaType.text.html))
  val mainJs = FileToBeServed("./js/target/scala-3.0.0-M3/scala-js-zio-server-fastopt/main.js", "main.js", `Content-Type`(MediaType.text.javascript))
  val mainJsSourceMap = FileToBeServed("./js/target/scala-3.0.0-M3/scala-js-zio-server-fastopt/main.js.map", "main.js.map", `Content-Type`(MediaType.application.`octet-stream`))  
}
