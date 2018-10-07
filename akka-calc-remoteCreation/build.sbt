lazy val commonSettings = Seq(
  name := "remoteCalcDemo",
  version := "1.0",
  scalaVersion := "2.11.8",
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.5.8",
    "com.typesafe.akka" %% "akka-remote" % "2.5.8",
    "com.typesafe.akka" %% "akka-testkit" % "2.5.8" % "test"
  )
)

lazy val messages = project.in(new File("messages"))
  .settings(commonSettings)
  .settings(
    name := "messages"
  )

lazy val remote = project.in(new File("remote"))
  .settings(commonSettings)
  .settings(
    name := "remote"
  ).aggregate(messages).dependsOn(messages)

lazy val local = project.in(new File("."))
  .settings(commonSettings)
  .settings(
    name := "local"
  ).aggregate(messages, remote).dependsOn(messages, remote)

