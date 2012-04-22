package de.exoticorn.androidgame

abstract class InputEvent
case class TouchStart(x: Float, y: Float) extends InputEvent
case class TouchMove(x: Float, y: Float) extends InputEvent
case object TouchEnd extends InputEvent

abstract class Game {
  def drawFrame(timeStep: Float)
  def setSize(width: Int, height: Int)
  def create(implicit as: AssetStore)
  def inputEvent(e: InputEvent)
}