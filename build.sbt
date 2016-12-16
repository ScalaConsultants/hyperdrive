name := "hyperdrive"

version := "0.1"

scalaVersion := "2.12.1"

resolvers ++= Seq(
  "snapshots"           at "http://oss.sonatype.org/content/repositories/snapshots",
  "releases"            at "http://oss.sonatype.org/content/repositories/releases",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.0", 
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.0",
  "com.chuusai" %% "shapeless" % "2.3.2"
)
