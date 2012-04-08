package de.exoticorn.glestest

import org.lwjgl.opengl.{ Display, DisplayMode }
import org.lwjgl.input.Mouse

object Main extends App {
  Display.setDisplayMode(new DisplayMode(640, 480))
  Display.create()

  val width = 640
  val height = 480

  val game = new Game
  game.create()
  game.setSize(width, height)

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
    game.drawFrame()
    Display.update()
  }

  Display.destroy()
}