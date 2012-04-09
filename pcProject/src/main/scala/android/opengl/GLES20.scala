package android.opengl

import java.nio.FloatBuffer
import org.lwjgl.opengl.{ GL20, GL11 }

object GLES20 {
  val GL_COLOR_BUFFER_BIT = GL11.GL_COLOR_BUFFER_BIT
  val GL_FLOAT = GL11.GL_FLOAT
  val GL_TRIANGLE_STRIP = GL11.GL_TRIANGLE_STRIP
  val GL_VERTEX_SHADER = GL20.GL_VERTEX_SHADER
  val GL_FRAGMENT_SHADER = GL20.GL_FRAGMENT_SHADER

  def glClearColor(r: Float, g: Float, b: Float, a: Float) { GL11.glClearColor(r, g, b, a) }
  def glClear(f: Int) { GL11.glClear(f) }

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
  def glVertexAttribPointer(attr: Int, numComp: Int, typ: Int, normalize: Boolean, stride: Int, buffer: FloatBuffer) { GL20.glVertexAttribPointer(attr, numComp, normalize, stride, buffer) }
  def glEnableVertexAttribArray(attr: Int) { GL20.glEnableVertexAttribArray(attr) }
  def glDrawArrays(prim: Int, start: Int, count: Int) { GL11.glDrawArrays(prim, start, count) }
  def glViewport(x: Int, y: Int, w: Int, h: Int) { GL11.glViewport(x, y, w, h) }
}
