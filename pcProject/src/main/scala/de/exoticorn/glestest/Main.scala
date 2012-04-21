package de.exoticorn.glestest

import org.lwjgl.opengl.{ Display, DisplayMode }
import org.lwjgl.input.Mouse

import javax.imageio.ImageIO
import java.awt.image.{
  BufferedImage,
  ColorConvertOp,
  DataBufferByte
}
import java.nio.ByteBuffer

object MyAssetStore extends AssetStore {
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
      buffer.put(data)
      buffer.rewind()
      new Image(img.getWidth, img.getHeight, buffer)
    }
  }
}

object Main extends App {
  val width = 1280
  val height = 768

  Display.setDisplayMode(new DisplayMode(width, height))
  Display.create()

  val game = new Game(MyAssetStore)
  game.create()
  game.setSize(width, height)

  var lastTime = System.nanoTime()

  while (!Display.isCloseRequested()) {
    while (Mouse.next) {
      if (Mouse.getEventButton() == 0) {
        if (Mouse.getEventButtonState()) {
          game.inputEvent(TouchStart(Mouse.getEventX().toFloat, height - Mouse.getEventY().toFloat))
        } else {
          game.inputEvent(TouchEnd)
        }
      } else if (Mouse.isButtonDown(0)) {
        game.inputEvent(TouchMove(Mouse.getEventX().toFloat, height - Mouse.getEventY().toFloat))
      }
    }
    val nowTime = System.nanoTime()
    val timeStep = (nowTime - lastTime).toFloat / 1000000000.0f
    lastTime = nowTime
    game.drawFrame(timeStep)
    Display.update()
  }

  Display.destroy()
}
