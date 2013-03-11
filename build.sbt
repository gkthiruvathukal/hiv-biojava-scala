import com.typesafe.sbt.SbtStartScript

name := "hiv-biojava-scala"

version := "0.0.2"

scalaVersion := "2.10.1-RC1"

resolvers += "BioJava repository" at "http://www.biojava.org/download/maven/"

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.11",
  "com.novocode" % "junit-interface" % "0.10-M2" % "test",
  "org.biojava" % "core" % "1.8.1",
  "org.biojava" % "alignment" % "1.8.1"
)

seq(SbtStartScript.startScriptForClassesSettings: _*)
