package org.zynque.example.fileserver

import zio._
import fs2._
import zio.interop.catz._
import java.nio.file.Paths
import cats.effect.Blocker

object FileStreaming {
  def loadFileString(filePath: String): Task[String] =
    Blocker[Task].use { blocker =>
      io.file.readAll[Task](Paths.get(filePath), blocker, 4096)
        .compile
        .toList
        .map(list => String(list.toArray, java.nio.charset.StandardCharsets.UTF_8)) 
    }
}
