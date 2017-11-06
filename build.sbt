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

lazy val hyperdrive = (project in file("."))
  .settings(
    libraryDependencies ++= Dependencies.common
  )

lazy val example = (project in file("example"))
  .dependsOn(hyperdrive)
