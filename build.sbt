addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.9")

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "lambdas"

val catsVersion = "1.6.0"
val http4sVersion = "0.18.21"
val circeVersion = "0.10.0"

lazy val root = (project in file("."))
  .enablePlugins(SbtTwirl)
  .enablePlugins(FlywayPlugin)
  .settings(
    name := "bball-server",
    scalacOptions ++= Seq(
      "-Ypartial-unification",
      "-language:higherKinds",
      "-Xfatal-warnings",
      "-deprecation",
      "-unchecked",
      "-explaintypes",
    ),
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % catsVersion,
      "org.typelevel" %% "cats-effect" % "0.10.1",
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % http4sVersion,
      "org.http4s" %% "http4s-twirl" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "io.circe" %% "circe-literal" % circeVersion,
      "io.circe" %% "circe-optics" % circeVersion,
      "org.postgresql" % "postgresql" % "42.2.5",
      "org.playframework.anorm" %% "anorm" % "2.6.2",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.scalatest" %% "scalatest" % "3.0.5" % Test,
    ),
    TwirlKeys.templateImports ++= Seq(
      "lambdas.league._",
      "lambdas.league.models._",
    ),
    flywayUrl := "jdbc:postgresql://localhost:5432/league",
    flywayUser := "postgres",
    flywayPassword := "whatevs",
  )
