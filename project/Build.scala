import com.typesafe.sbt.SbtStartScript
import sbt._
import Keys._

object BuildSettings {
  val buildOrganization = "ucalgary_students"
  val buildVersion = "0.1"
  val buildScalaVersion = "2.10.1"

  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := buildOrganization,
    version := buildVersion,
    scalaVersion := buildScalaVersion)
}

object Build extends Build {

  import BuildSettings._;

  lazy val project = Project(
      "cpsc457_Assignment_2"
	  , file(".")
    , settings = buildSettings ++ Seq(
      libraryDependencies ++= Seq(
          //"junit" % "junit" % "4.12" % "test"
         "com.novocode" % "junit-interface" % "0.11" % Test // exclude("junit", "junit-dep")
      )
      , testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v")
      , parallelExecution in Test := false
      //, conflictManager := ConflictManager.strict
    ) ++ SbtStartScript.startScriptForClassesSettings
  )
}
