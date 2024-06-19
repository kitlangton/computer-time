val scala3Version = "3.4.2"

inThisBuild(
  List(
    scalaVersion      := scala3Version,
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision
  )
)

Global / onChangedBuildSource := ReloadOnSourceChanges

/////////////////////////
// Project Definitions //
/////////////////////////

val zioVersion = "2.1.3"

lazy val root = project
  .in(file("."))
  .settings(
    publish / skip := true
  )
  .aggregate(core)

lazy val core = project
  .in(file("./modules/core"))
  .settings(
    name         := "stubby",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "com.lihaoyi"          %% "pprint"          % "0.9.0",
      "io.github.kitlangton" %% "quotidian"       % "0.0.15",
      "dev.zio"              %% "zio"             % zioVersion,
      "dev.zio"              %% "zio-test"        % zioVersion    % Test,
      "dev.zio"              %% "zio-test-sbt"    % zioVersion    % Test,
      "org.scala-lang"       %% "scala3-compiler" % scala3Version % "provided",
      ("org.mockito"         %% "mockito-scala"   % "1.17.31"     % Test).cross(CrossVersion.for3Use2_13)
    )
  )

/////////////////////
// Command Aliases //
/////////////////////

addCommandAlias("prepare", "scalafmtAll; scalafixAll")
