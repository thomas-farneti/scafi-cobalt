
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
val scalaConsul = "com.codacy" %% "scala-consul" % "2.0.2"

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
    name := "scafi-cobalt"
  )
  .aggregate(core,domainService,executionService,sensorManagerMicroService,ingestionService)

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
    libraryDependencies ++= Seq(akkaHTTP,akkaStream,akkaActor,akkaRemote,rediscala,sprayJson,testKit,scalaTest,rctRabbitMq)
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
    libraryDependencies ++= Seq(scalaConsul,scafi_core,akkaHTTP,akkaStream,akkaActor,akkaRemote,rediscala,sprayJson,testKit,scalaTest,rctRabbitMq)
  )
  .enablePlugins(DockerPlugin,JavaAppPackaging)
  .settings(
    packageName in Docker := "executionservice"
  )

lazy val ingestionService = project.
  dependsOn(core).
  settings(commonSettings: _*).
  settings(
    name := "cobalt-IngestionService",
    version := "0.1.0",
    libraryDependencies ++= Seq(scafi_core,akkaHTTP,akkaStream,akkaActor,akkaRemote,rediscala,sprayJson,testKit,scalaTest,rctRabbitMq)
  )
  .enablePlugins(DockerPlugin,JavaAppPackaging)
  .settings(
    packageName in Docker := "ingestionservice"
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
