package de.exoticorn.glestest

import org.lwjgl.opengl.{ Display, DisplayMode }
import org.lwjgl.input.Mouse

object MyAssetStore extends AssetStore {
  def open(filename: String)(cb: java.io.InputStream => Unit) {
    val is = getClass().getResourceAsStream("/" + filename)
    try {
      cb(is)
    } finally {
      if (is != null) {
        is.close()
      }
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
