name := "akka-cluster-sharing"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  val akkaVersion = "2.5.8"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-remote" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
    "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
    "com.typesafe.akka" %% "akka-contrib" % akkaVersion,
    "org.iq80.leveldb" % "leveldb" % "0.7",
    "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8"
  )
}