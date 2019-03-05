ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "lambdas"

lazy val root = (project in file("."))
  .enablePlugins(SbtTwirl)
  .settings(
    name := "bball-server",
    scalacOptions += "-Ypartial-unification",
    TwirlKeys.templateImports ++= Seq(
      "lambdas.league._",
      "lambdas.league.models._",
    ),
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "1.6.0",
      "org.scalatest" %% "scalatest" % "3.0.5" % Test,
    )
  )
