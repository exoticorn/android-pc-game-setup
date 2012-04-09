import sbt._

import Keys._
import AndroidKeys._

object General {
  val settings = Defaults.defaultSettings ++ Seq (
    name := "GLES Test Project",
    version := "0.1",
    versionCode := 0,
    scalaVersion := "2.9.1",
    platformName in Android := "android-10"
  )

  val proguardSettings = Seq (
    useProguard in Android := true
  )

  lazy val fullAndroidSettings =
    General.settings ++
    AndroidProject.androidSettings ++
    TypedResources.settings ++
    proguardSettings ++
    AndroidManifestGenerator.settings ++
    AndroidMarketPublish.settings ++ Seq (
      keyalias in Android := "change-me",
      libraryDependencies += "org.scalatest" %% "scalatest" % "1.7.RC1" % "test"
    )
}

object AndroidBuild extends Build {
  lazy val androidBuild = Project (
    "androidBuild",
    file("."),
    settings = General.fullAndroidSettings ++ Seq(
      unmanagedSourceDirectories in Compile <++= baseDirectory { base =>
	Seq(
	  base / "src/game"
	)
      }
    )
  )

  lazy val pcBuild = Project (
    "pcBuild",
    file("pcProject"),
    settings = Defaults.defaultSettings ++ LWJGLPlugin.lwjglSettings ++ Seq(
      unmanagedSourceDirectories in Compile <++= baseDirectory { base =>
	Seq(
	  base / "src/game"
	)
      }
    )
  )
}
