package de.exoticorn.glestest

import android.opengl.GLSurfaceView
import android.content.Context
import android.os.SystemClock

import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.egl.EGLConfig
import android.view.MotionEvent
import java.lang.Runnable

class GameView(context: Context) extends GLSurfaceView(context) {
  setEGLContextClientVersion(2)
  val renderer = new GameRenderer(context)
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
        renderer.game.inputEvent(e)
      }
    })
    true
  }

  class GameRenderer(context: Context) extends GLSurfaceView.Renderer {
    object MyAssetStore extends AssetStore {
      def open(filename: String)(cb: java.io.InputStream => Unit) {
	val is = context.getAssets().open(filename)
	try {
	  cb(is)
	} finally {
	  if(is != null) {
	    is.close()
	  }
	}
      }
    }
  
    val game = new Game(MyAssetStore)
    var lastTime = SystemClock.uptimeMillis()

    def onSurfaceCreated(unused: GL10, config: EGLConfig) {
      game.create()
    }

    def onSurfaceChanged(unused: GL10, width: Int, height: Int) {
      game.setSize(width, height)
    }

    def onDrawFrame(unused: GL10) {
      val thisTime = SystemClock.uptimeMillis()
      val timeStep = (thisTime - lastTime).toFloat / 1000.0f
      lastTime = thisTime
      game.drawFrame(timeStep)
    }
  }
}
