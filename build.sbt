
// Resolvers
resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += Resolver.typesafeRepo("releases")

// Constants
val akkaV = "2.4.16"
val scalaV = "2.11.8"
val rediscalaV = "1.8.0"
val kafkaVersion = "0.10.1.1"

// Managed dependencies
val config      = "com.typesafe"  % "config"  % "1.3.1"

val akkaHTTP    = "com.typesafe.akka" %% "akka-http" % "10.0.1"
val testKit     = "com.typesafe.akka" %% "akka-http-testkit" % "10.0.1"
val sprayJson   = "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.1"
val akkaActor   = "com.typesafe.akka" %% "akka-actor" % akkaV
val akkaRemote  = "com.typesafe.akka" %% "akka-remote" % akkaV
val akkaStream  = "com.typesafe.akka" %% "akka-stream" % akkaV
val rctRabbitMq = "io.scalac" %% "reactive-rabbit" % "1.1.4"

val scalaTest   = "org.scalatest" %% "scalatest" % "3.0.1" % "test"

val rediscala   = "com.github.etaty" %% "rediscala" % rediscalaV

val scafi_core  = "it.unibo.apice.scafiteam" % "scafi-core_2.11"  % "0.1.0"

lazy val kafkaDependencies = Seq(
  "org.apache.kafka"                %% "kafka"                      % kafkaVersion,
  "org.apache.kafka"                % "kafka-clients"                % kafkaVersion
)



// Cross-Building
crossScalaVersions := Seq("2.11.8")

// Common settings across projects
lazy val commonSettings = Seq(
  organization := "it.unibo.apice.scafiteam",
  scalaVersion := scalaV
)

lazy val root = (project in file("."))
  .settings(
    name := "scafi-cobalt"
  )
  .aggregate(core,networkMicroService,computingMicroService)

lazy val core = project.
  settings(commonSettings: _*).
  settings(
    name := "cobalt-core",
    version := "0.1.0",
    libraryDependencies ++= Seq(scafi_core,sprayJson)
  )

lazy val networkMicroService = project.
  dependsOn(core).
  settings(commonSettings: _*).
  settings(
    name := "cobalt-networkService",
    version := "0.1.0",
    libraryDependencies ++= Seq(akkaHTTP,akkaStream,akkaActor,akkaRemote,rediscala,sprayJson,testKit,scalaTest,rctRabbitMq)
  )
  .enablePlugins(DockerPlugin,JavaAppPackaging)

lazy val computingMicroService = project.
  dependsOn(core).
  settings(commonSettings: _*).
  settings(
    name := "cobalt-ComputingService",
    version := "0.1.0",
    libraryDependencies ++= Seq(scafi_core,akkaHTTP,akkaStream,akkaActor,akkaRemote,rediscala,sprayJson,testKit,scalaTest)
  )

lazy val ingestionMicroService = project.
  dependsOn(core).
  settings(commonSettings: _*).
  settings(
    name := "cobalt-IngestionService",
    version := "0.1.0",
    libraryDependencies ++= Seq(scafi_core,akkaHTTP,akkaStream,akkaActor,akkaRemote,rediscala,sprayJson,testKit,scalaTest,rctRabbitMq)
  )

lazy val sensorManagerMicroService = project.
  dependsOn(core).
  settings(commonSettings: _*).
  settings(
    name := "cobalt-SensorManager",
    version := "0.1.0",
    libraryDependencies ++= Seq(scafi_core,akkaHTTP,akkaStream,akkaActor,akkaRemote,rediscala,sprayJson,testKit,scalaTest)
  )
