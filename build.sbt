
// Resolvers
resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += Resolver.typesafeRepo("releases")

// Constants
//val akkaVersion = "2.3.7" // NOTE: Akka 2.4.0 REQUIRES Java 8!

// Managed dependencies
//val akkaActor  = "com.typesafe.akka" % "akka-actor_2.11"  % akkaVersion
//val akkaRemote = "com.typesafe.akka" % "akka-remote_2.11" % akkaVersion
//val bcel       = "org.apache.bcel"   % "bcel"             % "5.2"
//val scalatest  = "org.scalatest"     % "scalatest_2.11"   % "2.2.4"     % "test"
//val scopt      = "com.github.scopt"  % "scopt_2.11"       % "3.3.0"

val redisClient = "net.debasishg" % "redisclient_2.12" % "3.3"
val pickling = "org.scala-lang.modules" % "scala-pickling_2.11" % "0.10.1"


// Common settings across projects
lazy val commonSettings = Seq(
  organization := "it.unibo.apice.scafiteam",
  scalaVersion := "2.12.1"
)

// 'State Service' project definition
lazy val stateService = project.
  settings(commonSettings: _*).
  settings(
    name := "cobalt-StateService",
    version := "0.1.0",
    libraryDependencies ++= Seq(redisClient,pickling)
  )

//// 'simulator' project definition
//lazy val simulator = project.
//  dependsOn(core).
//  settings(commonSettings: _*).
//  settings(
//    version := "0.1.0",
//    name := "scafi-simulator"
//  )
//
//// 'distributed' project definition
//lazy val distributed = project.
//  dependsOn(core).
//  settings(commonSettings: _*).
//  settings(
//    version := "0.1.0",
//    name := "scafi-distributed",
//    libraryDependencies ++= Seq(akkaActor, akkaRemote, bcel, scopt)
//  )
//
//// 'tests' project definition
//lazy val tests = project.
//  dependsOn(core, simulator).
//  settings(commonSettings: _*).
//  settings(
//    version := "0.1.0",
//    name := "scafi-tests",
//    libraryDependencies += scalatest,
//    packagedArtifacts := Map.empty
//  )
//
//// 'demos' project definition
//lazy val demos = project.
//  dependsOn(core, distributed, simulator).
//  settings(commonSettings: _*).
//  settings(
//    version := "0.1.0",
//    name := "scafi-demos"
//  )
