
// Resolvers
resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += Resolver.typesafeRepo("releases")

// Constants
val akkaV = "2.4.16"
val scalaV = "2.11.8"
val rediscalaV = "1.8.0"

// Managed dependencies
val config      = "com.typesafe"  % "config"  % "1.3.0"

val akkaHTTP    = "com.typesafe.akka" %% "akka-http" % "10.0.1"
val testKit     ="com.typesafe.akka" %% "akka-http-testkit" % "10.0.1"
val sprayJson   = "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.1"
val akkaActor   = "com.typesafe.akka" %% "akka-actor" % akkaV
val akkaRemote  = "com.typesafe.akka" %% "akka-remote" % akkaV
val akkaStream  = "com.typesafe.akka" %% "akka-stream" % akkaV

val scalaTest   = "org.scalatest" %% "scalatest" % "3.0.1" % "test"

val rediscala   = "com.github.etaty" %% "rediscala" % rediscalaV

val scafi_core  = "it.unibo.apice.scafiteam" % "scafi-core_2.11"  % "0.1.0"
val redisClient = "net.debasishg" % "redisclient_2.11" % "3.3"

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
  .aggregate(networkService)

lazy val core = project.
  settings(commonSettings: _*).
  settings(
    name := "cobalt-core",
    version := "0.1.0"
    //libraryDependencies ++= Seq()
  )

// 'State Service' project definition
lazy val networkService = project.
  settings(commonSettings: _*).
  settings(
    name := "cobalt-NetworkService",
    version := "0.1.0",
    libraryDependencies ++= Seq(akkaHTTP,akkaStream,akkaActor,akkaRemote,rediscala,sprayJson,testKit,scalaTest)
  )

// 'State Service' project definition
lazy val stateService = project.
  settings(commonSettings: _*).
  settings(
    name := "cobalt-StateService",
    version := "0.1.0",
    libraryDependencies ++= Seq(redisClient,scafi_core)
  )

lazy val computingService = project.
  settings(commonSettings: _*).
  settings(
    name := "cobalt-ComputingService",
    version := "0.1.0",
    libraryDependencies ++= Seq(scafi_core)
  )
