package de.exoticorn.glestest

import android.opengl.GLES20._
import android.util.Log

import java.nio.{ FloatBuffer, ByteBuffer, ByteOrder }

abstract class InputEvent
case class TouchStart(x: Float, y: Float) extends InputEvent
case class TouchMove(x: Float, y: Float) extends InputEvent
case object TouchEnd extends InputEvent

abstract class Shader {
  protected def compile(vertexSrc: String, fragmentSrc: String): Int = {
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

    val vertexShader = makeShader(GL_VERTEX_SHADER, vertexSrc)
    val fragmentShader = makeShader(GL_FRAGMENT_SHADER, fragmentSrc)

    val shaderProgram = glCreateProgram()
    glAttachShader(shaderProgram, vertexShader)
    glAttachShader(shaderProgram, fragmentShader)
    glLinkProgram(shaderProgram)
    val log = glGetProgramInfoLog(shaderProgram)
    if (!log.isEmpty()) {
      Log.w("GLESTest", log)
    }

    shaderProgram
  }
}

class TunnelShader extends Shader {
  private val shaderProgram = compile(
    """
      attribute vec2 position;
      varying highp vec3 rayDirection;
      void main() {
        gl_Position = vec4(position, 0, 1);
        rayDirection = vec3(position.x * 1.6, position.y, 1);
      }
      """,
    """
      varying highp vec3 rayDirection;
      uniform lowp float offset;
      void main() {
      	mediump float angle = atan(rayDirection.x, rayDirection.y);
        highp float z = rayDirection.z * inversesqrt(rayDirection.x * rayDirection.x + rayDirection.y * rayDirection.y);
        lowp float brightness = clamp(1.0 - z * 0.1, 0.0, 1.0);
        gl_FragColor= vec4(mod(angle / (3.141 / 4.0) + offset, 1.0), mod(z + offset * 2.0, 1.0), 0, 1) * brightness;
      }
      """)

  val positionAttribute = glGetAttribLocation(shaderProgram, "position")
  private val offsetUniform = glGetUniformLocation(shaderProgram, "offset")

  def use(offset: Float) {
    glUseProgram(shaderProgram)
    glUniform1f(offsetUniform, offset)
    glEnableVertexAttribArray(positionAttribute)
  }
}

class DiscShader extends Shader {
  private val shaderProgram = compile(
    """
      attribute vec2 position;
      uniform vec2 pos;
      uniform vec2 size;
      varying vec2 uv;
      void main() {
        gl_Position = vec4(position * size + pos, 0, 1);
        uv = position;
      }
      """,
    """
      varying vec2 uv;
      void main() {
        mediump float dist = sqrt(dot(uv, uv));
        lowp float brightness = clamp(1.0 - dist + 0.2, 0.0, 1.0);
        gl_FragColor = vec4(brightness, brightness, brightness, dist < 1.0 ? 1.0 : 0.0);
      }
      """)

  val positionAttribute = glGetAttribLocation(shaderProgram, "position")
  private val posUniform = glGetUniformLocation(shaderProgram, "pos")
  private val sizeUniform = glGetUniformLocation(shaderProgram, "size")
  private var widthScale = 1.0f
  private var heightScale = 1.0f

  def use(width: Int, height: Int) {
    glUseProgram(shaderProgram)
    widthScale = 1.0f / width
    heightScale = 1.0f / height
  }

  def drawQuad(x: Float, y: Float, w: Float, h: Float) {
    glUniform2f(posUniform, x * widthScale, -y * heightScale)
    glUniform2f(sizeUniform, w * widthScale, h * heightScale)
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
  }
}

class Game {
  private var quadBuffer: FloatBuffer = _
  private var tunnelShader: TunnelShader = _
  private var discShader: DiscShader = _
  private var positionAttribute: Int = _
  private var offsetUniform: Int = _
  private var offset = 0.0f
  private var displayWidth = 640
  private var displayHeight = 480

  private var posX = 0.0f
  private var posY = 0.0f

  def drawFrame() {
    offset = (offset + 1 / 60.0f) % 1.0f

    glClear(GL_COLOR_BUFFER_BIT)

    tunnelShader.use(offset)
    glVertexAttribPointer(tunnelShader.positionAttribute, 2, GL_FLOAT, false, 8, quadBuffer)
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)

    discShader.use(displayWidth, displayHeight)
    glVertexAttribPointer(discShader.positionAttribute, 2, GL_FLOAT, false, 8, quadBuffer)
    discShader.drawQuad(posX, posY, 100, 100)
  }

  def setSize(width: Int, height: Int) {
    glViewport(0, 0, width, height)
    displayWidth = width
    displayHeight = height
  }

  def create() {
    glClearColor(0, 1, 0, 1)
    glEnable(GL_BLEND)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

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

    tunnelShader = new TunnelShader
    discShader = new DiscShader
  }

  def inputEvent(e: InputEvent) {
    e match {
      case TouchStart(x, y) =>
        posX = x.toFloat * 2 - displayWidth
        posY = y.toFloat * 2 - displayHeight
      case TouchMove(x, y) =>
      case TouchEnd =>
    }
  }
}
