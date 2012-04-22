package de.exoticorn.androidgame

import org.lwjgl.opengl.{ Display, DisplayMode }
import org.lwjgl.input.Mouse

class PCWindow(game: Game, width: Int = 1280, height: Int = 768) {
  def create() {
    Display.setDisplayMode(new DisplayMode(width, height))
    Display.create()

    game.create(PCAssetStore)
    game.setSize(width, height)
  }

  def run() {
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
  }

  def destroy() {
    Display.destroy()
  }
}

object PCWindow {
  def runGame(game: Game, width: Int = 1280, height: Int = 768) {
    val window = new PCWindow(game, width, height)
    window.create()
    window.run()
    window.destroy()
  }
}