scalaVersion := "2.13.7"

scalacOptions += "-deprecation"

Global / onChangedBuildSource := ReloadOnSourceChanges

libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core" % "2.3.0",
    "org.typelevel" %% "cats-effect" % "3.3.0",
    "co.fs2" %% "fs2-core" % "3.2.2",
    "co.fs2" %% "fs2-io" % "3.2.2",
    "org.scalacheck" %% "scalacheck" % "1.14.1" % "test",
)