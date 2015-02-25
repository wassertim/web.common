import sbt._
import Keys._

name := "Web Common"

organization := "web.common"

version := "0.1.3"

scalaVersion := "2.11.5"

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "org.specs2" % "specs2_2.11" % "2.4.16",
  "commons-io" % "commons-io" % "2.4",
  "org.imgscalr" % "imgscalr-lib" % "4.2",
  "commons-codec" % "commons-codec" % "1.7"
)

scalacOptions in Test ++= Seq("-Yrangepos")

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)