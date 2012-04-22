package de.exoticorn.glestest

import de.exoticorn.androidgame._

import android.opengl.GLES20._
import android.util.Log

import java.nio.{ FloatBuffer, ByteBuffer, ByteOrder }

class BackgroundShader extends Shader {
  protected val program = compile(
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
      /*
        highp float z = rayDirection.z / abs(rayDirection.y);
        lowp float brightness = clamp(1.0 - z * 0.1, 0.0, 1.0);
        lowp float onOff = mod(z + offset, 1.0) > 0.5 ? 1.0 : 0.0;
        lowp vec3 color = vec3(onOff, onOff, onOff) * brightness;
        gl_FragColor= vec4(color, 1);
      */
      gl_FragColor = vec4(0.2, 0.3, 0.4, 1.0);
      }
      """)

  val positionAttribute = glGetAttribLocation(program, "position")
  val offsetUniform = uniform1f("offset")

  def use(offset: Float) {
    glUseProgram(program)
    offsetUniform.set(offset)
    glEnableVertexAttribArray(positionAttribute)
  }
}

class DiscShader extends Shader {
  protected val program = compile(
    """
      attribute vec2 position;
      uniform vec2 pos;
      uniform vec2 size;
      varying lowp vec2 uv;
      void main() {
        gl_Position = vec4(position * size + pos, 0, 1);
        uv = position * 0.5 + 0.5;
		uv.y = 1.0 - uv.y;
      }
      """,
    """
      varying lowp vec2 uv;
      uniform sampler2D texture;
      void main() {
      	gl_FragColor = texture2D(texture, uv);
      }
      """)

  val positionAttribute = glGetAttribLocation(program, "position")
  val posUniform = uniform2f("pos")
  val sizeUniform = uniform2f("size")
  val texUniform = textureUniform("texture")
  private var widthScale = 1.0f
  private var heightScale = 1.0f

  def use(width: Int, height: Int) {
    glUseProgram(program)
    widthScale = 1.0f / width
    heightScale = 1.0f / height
  }

  def drawQuad(x: Float, y: Float, w: Float, h: Float, texture: Texture) {
    texUniform.set(0, texture)
    posUniform.set(x * widthScale, -y * heightScale)
    sizeUniform.set(w * widthScale, h * heightScale)
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
  }
}

class TestGame extends Game {
  private var quadBuffer: FloatBuffer = _
  private var bgShader: BackgroundShader = _
  private var discShader: DiscShader = _
  private var positionAttribute: Int = _
  private var offsetUniform: Int = _
  private var offset = 0.0f
  private var displayWidth = 640
  private var displayHeight = 480

  private var texture: Texture = _

  case class Target(var x: Float, var y: Float, var sx: Float, var sy: Float)
  private var targets = List.empty[Target]
  private var targetTimer = 1.0f

  private val rng = new java.util.Random

  def drawFrame(timeStep: Float) {
    offset = (offset + timeStep) % 1.0f

    targetTimer -= timeStep
    if (targetTimer < 0) {
      targetTimer = 1.0f
      spawnTarget()
    }

    targets = targets.filter { target =>
      target.sy += 300 * timeStep
      target.x += target.sx * timeStep
      target.y += target.sy * timeStep
      math.abs(target.x) < displayWidth + 400 && math.abs(target.y) < displayHeight + 400
    }

    glClear(GL_COLOR_BUFFER_BIT)

    bgShader.use(offset)
    glVertexAttribPointer(bgShader.positionAttribute, 2, GL_FLOAT, false, 8, quadBuffer)
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)

    discShader.use(displayWidth, displayHeight)
    glVertexAttribPointer(discShader.positionAttribute, 2, GL_FLOAT, false, 8, quadBuffer)
    for (target <- targets) {
      discShader.drawQuad(target.x, target.y, 100, 100, texture)
    }

    GLHelper.checkError("end of frame")
  }

  def setSize(width: Int, height: Int) {
    glViewport(0, 0, width, height)
    displayWidth = width
    displayHeight = height
  }

  def create(implicit as: AssetStore) {
    glClearColor(0, 0, 0, 1)
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

    texture = Texture.load("test-texture.png")

    bgShader = new BackgroundShader
    discShader = new DiscShader
  }

  def spawnTarget() {
    val sx = rng.nextFloat() * 200 + 100
    val sy = rng.nextFloat() * 200 - 900
    val x = rng.nextFloat() * 300 - 1000
    if (rng.nextBoolean()) {
      targets ::= Target(x, 500, sx, sy)
    } else {
      targets ::= Target(-x, 500, -sx, sy)
    }
  }

  def inputEvent(e: InputEvent) {
    e match {
      case TouchStart(x, y) =>
        val tx = x.toFloat * 2 - displayWidth
        val ty = y.toFloat * 2 - displayHeight
        targets = targets.filterNot { target =>
          val dx = target.x - tx
          val dy = target.y - ty
          math.sqrt(dx * dx + dy * dy) < 100
        }
      case TouchMove(x, y) =>
      case TouchEnd =>
    }
  }
}
