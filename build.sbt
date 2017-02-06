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

// Cross-Building
crossScalaVersions := Seq("2.11.8")

// Common settings across projects
lazy val commonSettings = Seq(
  organization := "it.unibo.apice.scafiteam",
  scalaVersion := scalaV,
  dockerUpdateLatest := true,
  dockerRepository := Some("scaficobalt")
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "scafi-cobalt",
    version := "0.1.0"
  ).
  aggregate(core,domainService,executionService,sensorManagerMicroService,ingestionService,fieldVisualizerService)

lazy val core = project.
  settings(commonSettings: _*).
  settings(
    name := "core",
    version := "0.1.0",
    libraryDependencies ++= Seq(scafi_core,sprayJson)
  )

lazy val domainService = project.
  dependsOn(core).
  settings(commonSettings: _*).
  settings(
    name := "domainservice",
    version := "0.1.0",
    libraryDependencies ++= Seq(akkaHTTP,akkaStream,akkaActor,akkaRemote,rediscala,sprayJson,testKit,scalaTest,reactiveRabbit)
  )
  .enablePlugins(DockerPlugin,JavaAppPackaging)



lazy val executionService = project.
  dependsOn(core).
  settings(commonSettings: _*).
  settings(
    name := "executionservice",
    version := "0.1.0",
    libraryDependencies ++= Seq(scafi_core,akkaHTTP,akkaStream,akkaActor,akkaRemote,rediscala,sprayJson,testKit,scalaTest,reactiveRabbit)
  )
  .enablePlugins(DockerPlugin,JavaAppPackaging)

lazy val fieldVisualizerService = project.
  dependsOn(core).
  settings(commonSettings: _*).
  settings(
    name := "visualizerservice",
    version := "0.1.0",
    libraryDependencies ++= Seq(scafi_core,akkaHTTP,akkaStream,akkaActor,akkaRemote,rediscala,sprayJson,testKit,scalaTest,reactiveRabbit)
  )
  .enablePlugins(DockerPlugin,JavaAppPackaging)


lazy val ingestionService = project.
  dependsOn(core).
  settings(commonSettings: _*).
  settings(
    name := "ingestionservice",
    version := "0.1.0",
    libraryDependencies ++= Seq(scafi_core,akkaHTTP,akkaStream,akkaActor,akkaRemote,rediscala,sprayJson,testKit,scalaTest,reactiveRabbit)
  )
  .enablePlugins(DockerPlugin,JavaAppPackaging)

lazy val testDevice = project.
  dependsOn(core).
  settings(commonSettings: _*).
  settings(
    name := "testdevice",
    version := "0.1.0",
    libraryDependencies ++= Seq(scafi_core,akkaHTTP,akkaStream,akkaActor,akkaRemote,sprayJson)
  )

lazy val sensorManagerMicroService = project.
  dependsOn(core).
  settings(commonSettings: _*).
  settings(
    name := "sensorservice",
    version := "0.1.0",
    libraryDependencies ++= Seq(scafi_core,akkaHTTP,akkaStream,akkaActor,akkaRemote,rediscala,sprayJson,testKit,scalaTest)
  )
  .enablePlugins(DockerPlugin,JavaAppPackaging)