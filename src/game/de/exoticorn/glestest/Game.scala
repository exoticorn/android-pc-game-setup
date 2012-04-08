package de.exoticorn.glestest

import android.opengl.GLES20._
import android.util.Log

import java.nio.{ FloatBuffer, ByteBuffer, ByteOrder }

abstract class InputEvent
case class TouchStart(x: Float, y: Float) extends InputEvent
case class TouchMove(x: Float, y: Float) extends InputEvent
case object TouchEnd extends InputEvent

class Game {
  private var quadBuffer: FloatBuffer = _
  private var shaderProgram: Int = _
  private var positionAttribute: Int = _

  def drawFrame() {
    glClear(GL_COLOR_BUFFER_BIT)

    glUseProgram(shaderProgram)
    glVertexAttribPointer(positionAttribute, 2, GL_FLOAT, false, 8, quadBuffer)
    glEnableVertexAttribArray(positionAttribute)
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
  }

  def setSize(width: Int, height: Int) {
    glViewport(0, 0, width, height)
  }

  def create() {
    glClearColor(0, 1, 0, 1)

    val quadCoords = Array(
      -0.75f, -0.75f,
      0.75f, -0.75f,
      -0.75f, 0.75f,
      0.75f, 0.75f)

    val vbb = ByteBuffer.allocateDirect(quadCoords.size * 4)
    vbb.order(ByteOrder.nativeOrder())
    quadBuffer = vbb.asFloatBuffer()
    quadBuffer.put(quadCoords)
    quadBuffer.rewind()

    def makeShader(typ: Int, shaderCode: String) = {
      val shader = glCreateShader(typ)
      glShaderSource(shader, shaderCode)
      glCompileShader(shader)
      val log = glGetShaderInfoLog(shader)
      if (!log.isEmpty()) {
        Log.w("GLESTest", log)
      }
      shader
    }

    val vertexShaderCode = """
      attribute vec2 position;
      void main() {
        gl_Position = vec4(position, 0, 1);
      }
    """
    val fragmentShaderCode = """
      void main() {
        gl_FragColor= vec4(1, 0, 1, 1);
      }
    """

    val vertexShader = makeShader(GL_VERTEX_SHADER, vertexShaderCode)
    val fragmentShader = makeShader(GL_FRAGMENT_SHADER, fragmentShaderCode)

    shaderProgram = glCreateProgram()
    glAttachShader(shaderProgram, vertexShader)
    glAttachShader(shaderProgram, fragmentShader)
    glLinkProgram(shaderProgram)
    val log = glGetProgramInfoLog(shaderProgram)
    if (!log.isEmpty()) {
      Log.w("GLESTest", log)
    }

    positionAttribute = glGetAttribLocation(shaderProgram, "position")
  }

  def inputEvent(e: InputEvent) {
    e match {
      case TouchStart(x, y) =>
        glClearColor(x / 1280.0f, y / 768.0f, 0, 1)
      case TouchMove(x, y) =>
        glClearColor(x / 1280, y / 768, 0, 1)
      case TouchEnd =>
    }
  }
}