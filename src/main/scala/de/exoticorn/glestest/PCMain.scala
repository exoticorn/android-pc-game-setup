package de.exoticorn.glestest

import org.lwjgl.opengl.{ Display, DisplayMode }

object Main extends App {
  Display.setDisplayMode(new DisplayMode(640, 480))
  Display.create()

  while (!Display.isCloseRequested()) {
    Display.update()
  }

  Display.destroy()
}