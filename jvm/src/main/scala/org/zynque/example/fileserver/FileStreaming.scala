package org.zynque.example.fileserver

import zio._
import fs2._
import zio.interop.catz._
import java.nio.file.Paths
import cats.effect.Blocker

object FileStreaming {
  def loadFileBytes(filePath: String): Task[List[Byte]] =
    Blocker[Task].use { blocker =>
      io.file.readAll[Task](Paths.get(filePath), blocker, 4096)
        .compile
        .toList
    }
  def streamFileContent(content: List[Byte]): Stream[Task, Byte] = Stream.fromIterator(content.iterator)
}
