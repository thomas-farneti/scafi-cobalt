import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.dockerUpdateLatest
import sbt.Keys.version

scalacOptions := Seq(
  "-unchecked",
  "-feature",
  "-deprecation",
  "-encoding", "utf8",
  "-Xlint:missing-interpolator",
  "-Ywarn-unused-import",
  "-Ywarn-dead-code")

// Resolvers
resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += Resolver.typesafeRepo("releases")
resolvers += Resolver.bintrayRepo("lonelyplanet", "maven")

// Constants
val akkaV = "2.4.16"
val akkaHttpV = "10.0.3"
val scalaV = "2.11.8"
val rediscalaV = "1.8.0"

val config      = "com.typesafe"  % "config"  % "1.3.1"

val akkaActor   = "com.typesafe.akka" %% "akka-actor" % akkaV
val akkaRemote  = "com.typesafe.akka" %% "akka-remote" % akkaV
val akkaStream  = "com.typesafe.akka" %% "akka-stream" % akkaV

val akkaHTTP    = "com.typesafe.akka" %% "akka-http" % akkaHttpV
val testKit     = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV % "test"
val sprayJson   = "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV

val reactiveRabbit = "io.scalac" %% "reactive-rabbit" % "1.1.4"
val scalaTest   = "org.scalatest" %% "scalatest" % "3.0.1" % "test"
val rediscala   = "com.github.etaty" %% "rediscala" % rediscalaV
val scafi_core  = "it.unibo.apice.scafiteam" % "scafi-core_2.11"  % "0.1.0"

val prometheusClient = "io.prometheus" % "simpleclient" % "0.0.20"
val prometheusCommon = "io.prometheus" % "simpleclient_common" % "0.0.20"
val prometheusHotSpot = "io.prometheus" % "simpleclient_hotspot" % "0.0.20"

// Cross-Building
crossScalaVersions := Seq("2.11.8")

// Common settings across projects
lazy val commonSettings = Seq(
  organization := "it.unibo.apice.scafiteam",
  scalaVersion := scalaV,
  dockerRepository := Some("scaficobalt")
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "scafi-cobalt",
    version := "0.1.0"
  ).
  aggregate(common,domainService,executionService,sensorManagerMicroService,ingestionService,fieldVisualizerService)

lazy val common = project.
  settings(commonSettings: _*).
  settings(
    name := "core",
    version := "0.1.0",
    libraryDependencies ++= Seq(scafi_core,sprayJson,prometheusClient,prometheusCommon,prometheusHotSpot,akkaStream,akkaActor,reactiveRabbit)
  )

lazy val domainService = project.
  dependsOn(common).
  settings(commonSettings: _*).
  settings(
    name := "domainservice",
    version := "0.1.0",
    libraryDependencies ++= Seq(akkaHTTP,akkaStream,akkaActor,akkaRemote,rediscala,sprayJson,testKit,scalaTest,reactiveRabbit,prometheusClient,prometheusCommon),
    dockerUpdateLatest := true,
    dockerExposedPorts := Seq(8080)
  )
  .enablePlugins(DockerPlugin,JavaAppPackaging)

lazy val executionService = project.
  dependsOn(common).
  settings(commonSettings: _*).
  settings(
    name := "executionservice",
    version := "0.1.0",
    libraryDependencies ++= Seq(scafi_core,
      akkaHTTP,
      akkaStream,
      akkaActor,
      akkaRemote,
      rediscala,
      sprayJson,
      testKit,
      scalaTest,
      reactiveRabbit,
      prometheusClient,prometheusHotSpot),
    dockerUpdateLatest := true,
    dockerExposedPorts := Seq(8080)
  )
  .enablePlugins(DockerPlugin,JavaAppPackaging)

lazy val fieldVisualizerService = project.
  dependsOn(common).
  settings(commonSettings: _*).
  settings(
    name := "visualizerservice",
    version := "0.1.0",
    libraryDependencies ++= Seq(scafi_core,akkaHTTP,akkaStream,akkaActor,akkaRemote,rediscala,sprayJson,testKit,scalaTest,reactiveRabbit),
    dockerUpdateLatest := true,
    dockerExposedPorts := Seq(8080)
  )
  .enablePlugins(DockerPlugin,JavaAppPackaging)

lazy val ingestionService = project.
  dependsOn(common).
  settings(commonSettings: _*).
  settings(
    name := "ingestionservice",
    version := "0.1.0",
    libraryDependencies ++= Seq(scafi_core,
      akkaHTTP,
      akkaStream,
      akkaActor,
      akkaRemote,
      rediscala,
      sprayJson,
      testKit,
      scalaTest,
      reactiveRabbit,
      prometheusClient,prometheusHotSpot),
    dockerUpdateLatest := true,
    dockerExposedPorts := Seq(8080)
  )
  .enablePlugins(DockerPlugin,JavaAppPackaging)

lazy val testDevice = project.
  dependsOn(common).
  settings(commonSettings: _*).
  settings(
    name := "testdevice",
    version := "0.1.0",
    libraryDependencies ++= Seq(scafi_core,akkaHTTP,akkaStream,akkaActor,akkaRemote,sprayJson)
  )
  .enablePlugins(JavaAppPackaging)

lazy val sensorManagerMicroService = project.
  dependsOn(common).
  settings(commonSettings: _*).
  settings(
    name := "sensorservice",
    version := "0.1.0",
    libraryDependencies ++= Seq(scafi_core,akkaHTTP,akkaStream,akkaActor,akkaRemote,rediscala,sprayJson,testKit,scalaTest),
    dockerUpdateLatest := true,
    dockerExposedPorts := Seq(8080)
  )
  .enablePlugins(DockerPlugin,JavaAppPackaging)