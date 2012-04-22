package de.exoticorn.androidgame

import javax.imageio.ImageIO
import java.awt.image.{
  BufferedImage,
  ColorConvertOp,
  DataBufferByte
}
import java.nio.ByteBuffer

object PCAssetStore extends AssetStore {
  def open[A](filename: String)(cb: java.io.InputStream => A): A = {
    val is = getClass().getResourceAsStream("/" + filename)
    try {
      cb(is)
    } finally {
      if (is != null) {
        is.close()
      }
    }
  }

  def readImage(filename: String): Image = {
    open(filename) { is =>
      val img = ImageIO.read(is)
      val inputImage = new BufferedImage(img.getWidth, img.getHeight, BufferedImage.TYPE_4BYTE_ABGR)
      val convertOp = new ColorConvertOp(null)
      convertOp.filter(img, inputImage)
      val data = inputImage.getRaster.getDataBuffer.asInstanceOf[DataBufferByte].getData()
      val buffer = ByteBuffer.allocateDirect(data.size)
      for (i <- 0 until img.getWidth * img.getHeight) {
        val a = data(i * 4)
        val b = data(i * 4 + 1)
        val g = data(i * 4 + 2)
        val r = data(i * 4 + 3)
        buffer.put(i * 4, r)
        buffer.put(i * 4 + 1, g)
        buffer.put(i * 4 + 2, b)
        buffer.put(i * 4 + 3, a)
      }
      new Image(img.getWidth, img.getHeight, buffer)
    }
  }
}
