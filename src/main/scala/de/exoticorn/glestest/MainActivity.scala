package de.exoticorn.glestest

import _root_.android.app.Activity
import _root_.android.os.Bundle
import android.opengl.GLSurfaceView

class MainActivity extends Activity {
  override def onCreate(bundle: Bundle) {
    super.onCreate(bundle)

    gameView = new GameView(this, new TestGame)

    setContentView(gameView)
  }

  override def onPause() {
    super.onPause()
    gameView.onPause()
  }

  override def onResume() {
    super.onResume()
    gameView.onResume()
  }

  private var gameView: GameView = _
}
