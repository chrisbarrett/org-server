scalaVersion := "2.12.1"

version := "0.0.1"


lazy val root = (
  project.in(file("."))
  aggregate(service, lib)
)


lazy val lib = (
  project.in(file("lib"))
  settings(commonSettings: _*)
  settings(
    name := "org-lib",
    libraryDependencies ++= Seq(

    )
  )
)


lazy val service = (
  project.in(file("service"))
  settings(commonSettings: _*)
  settings(
    name := "org-svc",
    libraryDependencies ++= Seq(

    )
  )
  enablePlugins(PlayScala)
)


lazy val commonSettings = Seq(
  organization := "cool.walrus",

  resolvers += "Pellucid Bintray" at "http://dl.bintray.com/pellucid/maven",

  scalacOptions ++= Seq(
    "-Xlint",
    "-deprecation",
    "-feature"
  ),

  javaOptions in Test += "-Dconfig.file=test/resources/application.conf",

  sources in (Compile, doc) := Seq.empty,
  publishArtifact in (Compile, packageDoc) := false,

  unmanagedSourceDirectories in Compile += baseDirectory.value / "src" / "main" / "generated",
  unmanagedSourceDirectories in Test += baseDirectory.value / "src" / "test" / "generated",

  fork in Test := true
)
