import sbt._

object Version {
  val mockito         = "1.9.5"
  val akka            = "2.3.9"
  val specs2          = "2.4.15"
  val slf4j           = "1.7.6"
  val logback         = "1.1.1"
  val typesafeLogger  = "3.1.0"
}

object Library {

  val akkaActor      = "com.typesafe.akka"          %% "akka-actor"      % Version.akka
  val akkaSlf4j      = "com.typesafe.akka"          %% "akka-slf4j"      % Version.akka
  val akkaTestKit    = "com.typesafe.akka"          %% "akka-testkit"    % Version.akka
  val slf4jApi       = "org.slf4j"                  %  "slf4j-api"       % Version.slf4j
  val logbackClassic = "ch.qos.logback"             %  "logback-classic" % Version.logback
  val typesafeLogger = "com.typesafe.scala-logging" %% "scala-logging"   % Version.typesafeLogger
  val specs2         = "org.specs2"                 %% "specs2"          % Version.specs2
}

object Dependencies {

  import Library._

  val taxiCoDeps = Seq(
    akkaActor,
    akkaSlf4j,
    slf4jApi,
    logbackClassic,
    typesafeLogger,
    akkaTestKit % "test",
    specs2 % "test"
  )
}
object Resolvers {
  //specs2 dependency on scalaz
  val scalaz = "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"
}

//TODO: Scalariform settings
/*
ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
  .setPreference(DoubleIndentClassDeclaration, true)
  .setPreference(PreserveDanglingCloseParenthesis, true)
*/
