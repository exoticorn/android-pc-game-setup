package de.exoticorn.androidgame

import java.nio.ByteBuffer

abstract class AssetStore {
  def open[A](filename: String)(cb: java.io.InputStream => A): A
  def readImage(filename: String): Image
}

class Image(val width: Int, val height: Int, val data: ByteBuffer)
