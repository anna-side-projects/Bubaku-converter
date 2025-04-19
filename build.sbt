ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.5"

lazy val root = (project in file("."))
  .settings(
    name := "bubakuConvert"
  )

libraryDependencies += "org.playframework" %% "play-json" % "3.0.4"
libraryDependencies += "com.lihaoyi" %% "os-lib" % "0.11.3"