name := "hyperdrive"

version := "0.1"

scalaVersion := "2.12.4"

resolvers ++= Seq(
  "snapshots"           at "http://oss.sonatype.org/content/repositories/snapshots",
  "releases"            at "http://oss.sonatype.org/content/repositories/releases",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation", "-encoding", "utf8")

//needed for Cats
scalacOptions += "-Ypartial-unification"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.10", 
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.10",
  "org.typelevel" %% "cats-core" % "1.0.0-RC1",
  "com.chuusai" %% "shapeless" % "2.3.2",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)
