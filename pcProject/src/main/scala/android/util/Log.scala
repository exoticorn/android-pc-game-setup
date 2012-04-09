package android.util

object Log {
  def w(tag: String, message: String) {
    println(tag + ": " + message)
  }
}
