name := "org-svc"
organization := "cool.walrus"
version := "0.0.1"

scalaVersion := "2.11.8"

resolvers ++= Seq(
  "Typesafe" at "https://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  filters,
  "com.iheart" %% "ficus" % "1.4.0",
  "com.softwaremill.quicklens" %% "quicklens" % "1.4.8",
  "com.pauldijou" %% "jwt-play-json" % "0.9.2",
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

fork in Test := true
javaOptions in Test += "-Dconfig.file=test/resources/application.conf"
