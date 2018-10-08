name := "akka-cluster-calc"

version := "1.o"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  val akkaVersion = "2.5.8"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster" % akkaVersion
  )
}