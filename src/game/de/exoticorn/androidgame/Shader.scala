package de.exoticorn.androidgame

import android.opengl.GLES20._
import android.util.Log

abstract class Shader {
  protected val program: Int

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

  class Uniform1f(index: Int) {
    def set(value: Float) {
      glUniform1f(index, value)
    }
  }

  class Uniform2f(index: Int) {
    def set(value1: Float, value2: Float) {
      glUniform2f(index, value1, value2)
    }
  }

  class TextureUniform(index: Int) {
    def set(slot: Int, texture: Texture) {
      glActiveTexture(GL_TEXTURE0 + slot)
      glBindTexture(GL_TEXTURE_2D, texture.name)
      glUniform1i(index, slot)
    }
  }

  protected def uniform1f(name: String) = new Uniform1f(glGetUniformLocation(program, name))
  protected def uniform2f(name: String) = new Uniform2f(glGetUniformLocation(program, name))
  protected def textureUniform(name: String) = new TextureUniform(glGetUniformLocation(program, name))
}

