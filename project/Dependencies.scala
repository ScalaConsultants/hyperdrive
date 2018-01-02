import sbt._

object BuildSettings {

  val akkaHttpVersion = "10.0.11"

}

object Dependencies {

  val common = Seq(
    "com.typesafe.akka" %% "akka-http" % BuildSettings.akkaHttpVersion, 
    "com.typesafe.akka" %% "akka-http-spray-json" % BuildSettings.akkaHttpVersion,
    "org.typelevel" %% "cats-core" % "1.0.1",
    "com.chuusai" %% "shapeless" % "2.3.3",
    "org.scalatest" %% "scalatest" % "3.0.4" % "test"
  )

}
