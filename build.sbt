name := "Wassertim Common"

organization := "wt.common"

version := "0.1-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "2.3.7" % "test",
  "commons-io" % "commons-io" % "2.4",
  "org.imgscalr" % "imgscalr-lib" % "4.2",
  "commons-codec" % "commons-codec" % "1.7"
)

scalacOptions in Test ++= Seq("-Yrangepos")

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)