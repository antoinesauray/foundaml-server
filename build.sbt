val Http4sVersion = "0.20.0-M5"
val Specs2Version = "4.1.0"
val LogbackVersion = "1.2.3"
val ScalazZIOVersion = "1.0-RC1"

fork in run := true

lazy val root = (project in file("."))
  .settings(
    organization := "foundaml",
    name := "foundaml-server",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.8",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-blaze-server"     % Http4sVersion,
      "org.http4s"      %% "http4s-circe"            % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"              % Http4sVersion,
      "org.specs2"      %% "specs2-core"             % Specs2Version % "test",
      "ch.qos.logback"  %  "logback-classic"         % LogbackVersion,
      "org.scalaz"      %% "scalaz-zio"              % ScalazZIOVersion,
      "org.scalaz"      %% "scalaz-zio-interop-cats" % ScalazZIOVersion,
      "org.scalaz"      %% "scalaz-zio-interop-shared" % ScalazZIOVersion,
      "org.scalaz"      %% "scalaz-zio-interop-future" % ScalazZIOVersion
    ),
    addCompilerPlugin("org.spire-math" %% "kind-projector"     % "0.9.6"),
    addCompilerPlugin("com.olegpy"     %% "better-monadic-for" % "0.2.4")
  )

