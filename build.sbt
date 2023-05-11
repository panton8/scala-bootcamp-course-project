ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "FPLGame"
  )

val DoobieVersion = "1.0.0-RC1"
val NewTypeVersion = "0.4.4"
val EnumeratumDoobieVersion = "1.7.0"
val Http4sVersion = "0.23.18"
val CirceVersion = "0.14.0-M5"
val circeVersion = "0.14.1"

libraryDependencies ++= Seq(
  "org.tpolecat" %% "doobie-core"     % DoobieVersion,
  "org.tpolecat" %% "doobie-postgres" % DoobieVersion,
  "org.tpolecat" %% "doobie-hikari"   % DoobieVersion,
  "com.beachape" %% "enumeratum-doobie" % EnumeratumDoobieVersion,
  "org.http4s" %% "http4s-ember-server" % Http4sVersion,
  "org.http4s"  %% "http4s-jdk-http-client" % "0.9.0",
  "org.http4s" %% "http4s-circe" % Http4sVersion,
  "org.http4s" %% "http4s-server" % Http4sVersion,
  "org.http4s" %% "http4s-ember-client" % Http4sVersion,
  "org.http4s" %% "http4s-client" % Http4sVersion,
  "org.http4s" %% "http4s-core" % Http4sVersion,
  "org.http4s" %% "http4s-dsl" % Http4sVersion,
  "io.circe" %% "circe-generic" % CirceVersion,
  "org.reactormonk" %% "cryptobits" % "1.3",
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic"  % circeVersion,
  "io.circe" %% "circe-parser"  % circeVersion,
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "org.slf4j" % "slf4j-simple" % "1.7.5",
  "org.reactormonk" %% "cryptobits" % "1.3"
)