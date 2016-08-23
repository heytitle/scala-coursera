name := "scala-on-coursera"

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-deprecation")

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

// grading libraries
libraryDependencies ++= Seq(
  "junit" % "junit" % "4.10" % "test",
  "com.storm-enroute" %% "scalameter-core" % "0.6",
  "com.github.scala-blitz" %% "scala-blitz" % "1.1",
  "org.scala-lang.modules" %% "scala-swing" % "1.0.1",
  "com.storm-enroute" %% "scalameter" % "0.6" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.1",
  "org.scalactic" %% "scalactic" % "3.0.0",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test"
)

// libraryDependencies ++= Seq("org.specs2" % "specs2_2.9.1" % "1.12.3" 
// % "test")
