import zio._
import fs2._
import zio.interop.catz._
import java.nio.file.Paths
import cats.effect.Blocker

object FileStreaming {
  def loadFileBytes(filePath: String): Task[Vector[Byte]] =
    Blocker[Task].use { blocker =>
      io.file.readAll[Task](Paths.get(filePath), blocker, 4096)
        .compile
        .toVector
    }
  def streamFileContent(content: Vector[Byte]): Stream[Task, Byte] = Stream.fromIterator(content.iterator)
}
