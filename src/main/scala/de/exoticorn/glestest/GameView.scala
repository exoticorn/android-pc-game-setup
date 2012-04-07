package de.exoticorn.glestest

import android.opengl.GLSurfaceView
import android.content.Context
import android.view.MotionEvent
import java.lang.Runnable

class GameView(context: Context) extends GLSurfaceView(context) {
  setEGLContextClientVersion(2)
  val renderer = new Renderer(context)
  setRenderer(renderer)

  override def onTouchEvent(e: MotionEvent): Boolean = e.getActionMasked() match {
    case MotionEvent.ACTION_DOWN => sendInputEvent(TouchStart(e.getX(), e.getY()))
    case MotionEvent.ACTION_MOVE => sendInputEvent(TouchMove(e.getX(), e.getY()))
    case MotionEvent.ACTION_UP => sendInputEvent(TouchEnd)
    case _ => false
  }

  def sendInputEvent(e: InputEvent) = {
    queueEvent(new Runnable {
      def run {
        renderer.onInputEvent(e)
      }
    })
    true
  }
}