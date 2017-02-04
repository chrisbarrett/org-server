name := "org-svc"
organization := "cool.walrus"
version := "0.0.1"

scalaVersion := "2.11.8"

resolvers ++= Seq(
  "Pellucid Bintray" at "http://dl.bintray.com/pellucid/maven",
  "Typesafe" at "https://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
)

scalacOptions ++= Seq(
  "-Xlint",
  "-deprecation",
  "-feature"
)

lazy val root = (
  project.in(file("."))
  enablePlugins(PlayScala)
)

sources in (Compile, doc) := Seq.empty
publishArtifact in (Compile, packageDoc) := false

fork in Test := true
javaOptions in Test += "-Dconfig.file=test/resources/application.conf"
