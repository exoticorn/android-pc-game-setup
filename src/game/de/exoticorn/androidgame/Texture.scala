package de.exoticorn.androidgame

import android.opengl.GLES20._
import android.util.Log

object GLHelper {
  def checkError(where: String) {
    val error = glGetError()
    if (error != GL_NO_ERROR) {
      val tpe = error match {
        case GL_INVALID_ENUM => "INVALID ENUM"
        case GL_INVALID_VALUE => "INVALID VALUE"
        case GL_INVALID_OPERATION => "INVALID OPERATION"
        case GL_INVALID_FRAMEBUFFER_OPERATION => "INVALID FRAMEBUFFER OPERATION"
        case GL_OUT_OF_MEMORY => "OUT OF MEMORY"
        case _ => "UNKNOWN"
      }
      Log.w("Gl error", "%s (%s)".format(tpe, where))
    }
  }
}

class Texture(val name: Int, val width: Int, val height: Int)

object Texture {
  def load(filename: String)(implicit as: AssetStore): Texture = fromImage(as.readImage(filename))

  def fromImage(image: Image): Texture = {
    val names = Array(0)
    glGenTextures(1, names, 0)
    val name = names(0)
    glBindTexture(GL_TEXTURE_2D, name)
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.width, image.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image.data)
    GLHelper.checkError("glTexImage2d")
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
    GLHelper.checkError("texture parameter")
    new Texture(name, image.width, image.height)
  }
}

