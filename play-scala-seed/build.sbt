name := """platzi-video"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, DockerPlugin)

scalaVersion := "2.13.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "com.typesafe.play" %% "play-slick" % "5.0.0"
// https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc
libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.32.3.2"


// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"


// Docker
import com.typesafe.sbt.packager.docker._

dockerBaseImage := "openjdk:8-jre-alpine"
dockerExposedPorts ++= Seq(9000)

daemonUserUid in Docker := None
daemonUser in Docker := "daemon"

dockerCommands += Cmd("USER", "root")
dockerCommands += ExecCmd("RUN", "/bin/sh", "-c", "apk add --no-cache bash")
