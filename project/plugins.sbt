resolvers += Resolver.url("scalasbt releases", new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

addSbtPlugin("org.scala-sbt" % "sbt-android-plugin" % "0.6.1")

addSbtPlugin("com.github.philcali" % "sbt-lwjgl-plugin" % "3.1.1")
