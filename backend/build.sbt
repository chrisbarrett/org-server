name := "org-svc"
organization := "cool.walrus"
version := "0.0.1"

scalaVersion := "2.11.8"

resolvers ++= Seq(
  "Pellucid Bintray" at "http://dl.bintray.com/pellucid/maven",
  "Typesafe" at "https://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "com.softwaremill.quicklens" %% "quicklens" % "1.4.8",
  "org.typelevel" %% "cats" % "0.9.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

scalacOptions ++= Seq(
  "-Xlint",
  "-deprecation",
  "-feature"
)

routesImport += "models._"

routesGenerator := play.routes.compiler.InjectedRoutesGenerator

lazy val root = (
  project.in(file("."))
  enablePlugins(PlayScala)
)

sources in (Compile, doc) := Seq.empty
publishArtifact in (Compile, packageDoc) := false

fork in Test := true
javaOptions in Test += "-Dconfig.file=test/resources/application.conf"
