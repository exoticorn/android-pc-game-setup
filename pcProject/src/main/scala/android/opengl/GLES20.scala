package android.opengl

import java.nio.{ FloatBuffer, ByteBuffer }
import org.lwjgl.opengl.{ GL20, GL11, GL13 }

object GLES20 {
  val GL_COLOR_BUFFER_BIT = GL11.GL_COLOR_BUFFER_BIT
  val GL_FLOAT = GL11.GL_FLOAT
  val GL_TRIANGLE_STRIP = GL11.GL_TRIANGLE_STRIP
  val GL_VERTEX_SHADER = GL20.GL_VERTEX_SHADER
  val GL_FRAGMENT_SHADER = GL20.GL_FRAGMENT_SHADER
  val GL_BLEND = GL11.GL_BLEND
  val GL_ONE = GL11.GL_ONE
  val GL_SRC_ALPHA = GL11.GL_SRC_ALPHA
  val GL_ONE_MINUS_SRC_ALPHA = GL11.GL_ONE_MINUS_SRC_ALPHA
  val GL_TEXTURE_2D = GL11.GL_TEXTURE_2D
  val GL_RGBA = GL11.GL_RGBA
  val GL_UNSIGNED_BYTE = GL11.GL_UNSIGNED_BYTE
  val GL_TEXTURE_MIN_FILTER = GL11.GL_TEXTURE_MIN_FILTER
  val GL_TEXTURE_MAG_FILTER = GL11.GL_TEXTURE_MAG_FILTER
  val GL_TEXTURE_WRAP_S = GL11.GL_TEXTURE_WRAP_S
  val GL_TEXTURE_WRAP_T = GL11.GL_TEXTURE_WRAP_T
  val GL_NEAREST = GL11.GL_NEAREST
  val GL_LINEAR = GL11.GL_LINEAR
  val GL_CLAMP_TO_EDGE = GL11.GL_CLAMP
  val GL_REPEAT = GL11.GL_REPEAT
  val GL_TEXTURE0 = GL13.GL_TEXTURE0
  val GL_NO_ERROR = GL11.GL_NO_ERROR
  val GL_INVALID_ENUM = GL11.GL_INVALID_ENUM
  val GL_INVALID_VALUE = GL11.GL_INVALID_VALUE
  val GL_INVALID_OPERATION = GL11.GL_INVALID_OPERATION
  val GL_INVALID_FRAMEBUFFER_OPERATION = 4711
  val GL_OUT_OF_MEMORY = GL11.GL_OUT_OF_MEMORY

  def glGetError() = GL11.glGetError

  def glClearColor(r: Float, g: Float, b: Float, a: Float) { GL11.glClearColor(r, g, b, a) }
  def glClear(f: Int) { GL11.glClear(f) }

  def glEnable(e: Int) { GL11.glEnable(e) }
  def glBlendFunc(sFactor: Int, dFactor: Int) { GL11.glBlendFunc(sFactor, dFactor) }

  def glCreateShader(typ: Int): Int = GL20.glCreateShader(typ)
  def glShaderSource(shader: Int, source: String) {
    val cleanedSource = """highp|mediump|lowp""".r.replaceAllIn(source, "")
    GL20.glShaderSource(shader, cleanedSource)
  }
  def glCompileShader(shader: Int) { GL20.glCompileShader(shader) }
  def glGetShaderInfoLog(shader: Int): String = GL20.glGetShaderInfoLog(shader, 1024)
  def glCreateProgram(): Int = GL20.glCreateProgram()
  def glAttachShader(program: Int, shader: Int) { GL20.glAttachShader(program, shader) }
  def glLinkProgram(program: Int) { GL20.glLinkProgram(program) }
  def glGetProgramInfoLog(program: Int): String = GL20.glGetProgramInfoLog(program, 1024)
  def glGetAttribLocation(program: Int, name: String): Int = GL20.glGetAttribLocation(program, name)
  def glGetUniformLocation(program: Int, name: String): Int = GL20.glGetUniformLocation(program, name)
  def glUseProgram(name: Int) { GL20.glUseProgram(name) }
  def glUniform1f(location: Int, value: Float) { GL20.glUniform1f(location, value) }
  def glUniform2f(location: Int, value1: Float, value2: Float) { GL20.glUniform2f(location, value1, value2) }
  def glUniform1i(location: Int, value: Int) { GL20.glUniform1i(location, value) }
  def glVertexAttribPointer(attr: Int, numComp: Int, typ: Int, normalize: Boolean, stride: Int, buffer: FloatBuffer) { GL20.glVertexAttribPointer(attr, numComp, normalize, stride, buffer) }
  def glEnableVertexAttribArray(attr: Int) { GL20.glEnableVertexAttribArray(attr) }
  def glDrawArrays(prim: Int, start: Int, count: Int) { GL11.glDrawArrays(prim, start, count) }
  def glViewport(x: Int, y: Int, w: Int, h: Int) { GL11.glViewport(x, y, w, h) }

  def glGenTextures(n: Int, textures: Array[Int], offset: Int) {
    for (i <- offset until (offset + n)) {
      textures(i) = GL11.glGenTextures()
    }
  }
  def glBindTexture(target: Int, texture: Int) { GL11.glBindTexture(target, texture) }
  def glActiveTexture(texture: Int) { GL13.glActiveTexture(texture) }
  def glTexImage2D(target: Int, level: Int, internalFormat: Int, width: Int, height: Int, border: Int, format: Int, tpe: Int, data: ByteBuffer) {
    GL11.glTexImage2D(target, level, internalFormat, width, height, border, format, tpe, data)
  }
  def glTexParameteri(target: Int, pname: Int, param: Int) { GL11.glTexParameteri(target, pname, param) }
}
