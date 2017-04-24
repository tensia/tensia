name := "tensia"

version := "1.0"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.0",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.0",
  "org.scalatest" % "scalatest_2.12" % "3.0.1" % "test"
)
