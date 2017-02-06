import sbt.Keys.version

scalacOptions := Seq(
  "-unchecked",
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
  scalaVersion := scalaV
)

lazy val root = (project in file("."))
  .settings(
    name := "scafi-cobalt",
    version := "0.1.0"
  )
  .aggregate(core,domainService,executionService,sensorManagerMicroService,ingestionService,fieldVisualizerService)

lazy val core = project.
  settings(commonSettings: _*).
  settings(
    name := "cobalt-core",
    version := "0.1.0",
    libraryDependencies ++= Seq(scafi_core,sprayJson)
  )

lazy val domainService = project.
  dependsOn(core).
  settings(commonSettings: _*).
  settings(
    name := "cobalt-domainService",
    version := "0.1.0",
    libraryDependencies ++= Seq(akkaHTTP,akkaStream,akkaActor,akkaRemote,rediscala,sprayJson,testKit,scalaTest,reactiveRabbit)
  )
  .enablePlugins(DockerPlugin,JavaAppPackaging)
    .settings(
      packageName in Docker := "domainservice"
    )


lazy val executionService = project.
  dependsOn(core).
  settings(commonSettings: _*).
  settings(
    name := "cobalt-ExecutionService",
    version := "0.1.0",
    libraryDependencies ++= Seq(scafi_core,akkaHTTP,akkaStream,akkaActor,akkaRemote,rediscala,sprayJson,testKit,scalaTest,reactiveRabbit)
  )
  .enablePlugins(DockerPlugin,JavaAppPackaging)
  .settings(
    packageName in Docker := "executionservice"
  )

lazy val fieldVisualizerService = project.
  dependsOn(core).
  settings(commonSettings: _*).
  settings(
    name := "cobalt-FieldVisualizer",
    version := "0.1.0",
    libraryDependencies ++= Seq(scafi_core,akkaHTTP,akkaStream,akkaActor,akkaRemote,rediscala,sprayJson,testKit,scalaTest,reactiveRabbit)
  )
  .enablePlugins(DockerPlugin,JavaAppPackaging)
  .settings(
    packageName in Docker := "visulizerservice"
  )

lazy val ingestionService = project.
  dependsOn(core).
  settings(commonSettings: _*).
  settings(
    name := "cobalt-IngestionService",
    version := "0.1.0",
    libraryDependencies ++= Seq(scafi_core,akkaHTTP,akkaStream,akkaActor,akkaRemote,rediscala,sprayJson,testKit,scalaTest,reactiveRabbit)
  )
  .enablePlugins(DockerPlugin,JavaAppPackaging)
  .settings(
    packageName in Docker := "ingestionservice"
  )

lazy val testDevice = project.
  dependsOn(core).
  settings(commonSettings: _*).
  settings(
    name := "cobalt-testDevice",
    version := "0.1.0",
    libraryDependencies ++= Seq(scafi_core,akkaHTTP,akkaStream,akkaActor,akkaRemote,sprayJson)
  )
  .enablePlugins(DockerPlugin,JavaAppPackaging)
  .settings(
    packageName in Docker := "testdevice"
  )

lazy val sensorManagerMicroService = project.
  dependsOn(core).
  settings(commonSettings: _*).
  settings(
    name := "cobalt-SensorManager",
    version := "0.1.0",
    libraryDependencies ++= Seq(scafi_core,akkaHTTP,akkaStream,akkaActor,akkaRemote,rediscala,sprayJson,testKit,scalaTest)
  )
  .enablePlugins(DockerPlugin,JavaAppPackaging)
  .settings(
    packageName in Docker := "sensorservice"
  )
