import sbt.Keys.libraryDependencies
// Resolvers
resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += Resolver.typesafeRepo("releases")

// Constants
val akkaVersion = "2.3.7" // NOTE: Akka 2.4.0 REQUIRES Java 8!

// Managed dependencies
val akkaActor  = "com.typesafe.akka" % "akka-actor_2.11"  % akkaVersion
val akkaRemote = "com.typesafe.akka" % "akka-remote_2.11" % akkaVersion
//val bcel       = "org.apache.bcel"   % "bcel"             % "5.2"
//val scalatest  = "org.scalatest"     % "scalatest_2.11"   % "2.2.4"     % "test"
//val scopt      = "com.github.scopt"  % "scopt_2.11"       % "3.3.0"

val scafi_core  = "it.unibo.apice.scafiteam" % "scafi-core_2.11"  % "0.1.0"
val scafi_platform = "it.unibo.apice.scafiteam" % "scafi-distributed_2.11"  % "0.1.0"
//val redisClient = "net.debasishg" % "redisclient_2.12" % "3.3"
val redisClient = "net.debasishg" % "redisclient_2.11" % "3.3"

// Cross-Building
crossScalaVersions := Seq("2.11.8")


// Common settings across projects
lazy val commonSettings = Seq(
  organization := "it.unibo.apice.scafiteam",
  scalaVersion := "2.11.8"
)

lazy val root = (project in file("."))
  .settings(
    name := "scafi-cobalt"
  )
  .aggregate(api, backend, frontend)

lazy val core = project.
  settings(commonSettings: _*).
  settings(
    name := "cobalt-core",
    version := "0.1.0",
    libraryDependencies ++= Seq(redisClient,scafi_core)
  )

// 'State Service' project definition
lazy val stateService = project.
  settings(commonSettings: _*).
  settings(
    name := "cobalt-StateService",
    version := "0.1.0",
    libraryDependencies ++= Seq(redisClient,scafi_core)
  )

lazy val api = project.
  settings(commonSettings: _*).
  settings(
    name := "cobalt-Api",
    version := "0.1.0",
    libraryDependencies ++= Seq(scafi_core)
  )

lazy val backend = project.
  settings(commonSettings: _*).
  settings(
    name := "cobalt-backend",
    version := "0.1.0",
    libraryDependencies ++= Dependencies.backend
  )
  .dependsOn(api)

lazy val frontend = project.
  settings(commonSettings: _*).
  settings(
    name := "cobalt-frontend",
    version := "0.1.0",
    libraryDependencies ++= Dependencies.frontend
  )

// 'State Service' project definition
lazy val computingService = project.
  settings(commonSettings: _*).
  settings(
    name := "cobalt-ComputingService",
    version := "0.1.0",
    libraryDependencies ++= Seq(scafi_core)
  )

// 'Centralized Platform' project definition
lazy val centralizedPlatform = project.
  settings(commonSettings: _*).
  settings(
    name := "cobalt-CentralizedPlatform",
    version := "0.1.0",
    libraryDependencies ++= Seq(redisClient,scafi_core,scafi_platform,akkaActor, akkaRemote)
  )