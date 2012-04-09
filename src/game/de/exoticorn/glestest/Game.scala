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
  private var offsetUniform: Int = _
  private var offset = 0.0f

  def drawFrame() {
    offset = (offset + 1 / 60.0f) % 1.0f

    glClear(GL_COLOR_BUFFER_BIT)

    glUseProgram(shaderProgram)
    glUniform1f(offsetUniform, offset)
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
      -1.0f, -1.0f,
      1.0f, -1.0f,
      -1.0f, 1.0f,
      1.0f, 1.0f)

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
      varying highp vec3 rayDirection;
      void main() {
        gl_Position = vec4(position, 0, 1);
        rayDirection = vec3(position.x * 1.6, position.y, 1);
      }
    """
    val fragmentShaderCode = """
      varying highp vec3 rayDirection;
      uniform lowp float offset;
      void main() {
      	mediump float angle = atan(rayDirection.x, rayDirection.y);
        highp float z = rayDirection.z * inversesqrt(rayDirection.x * rayDirection.x + rayDirection.y * rayDirection.y);
        lowp float brightness = clamp(1.0 - z * 0.1, 0.0, 1.0);
        gl_FragColor= vec4(mod(angle / (3.141 / 4.0) + offset, 1.0), mod(z + offset * 2.0, 1.0), 0, 1) * brightness;
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
    offsetUniform = glGetUniformLocation(shaderProgram, "offset")
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
