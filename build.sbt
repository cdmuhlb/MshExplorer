name := "MshExplorer"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.3"

scalacOptions ++= Seq("-target:jvm-1.7", "-deprecation", "-feature", "-unchecked")

unmanagedJars in Compile += Attributed.blank(file(
    System.getenv("JAVA_HOME") + "/jre/lib/jfxrt.jar"))

fork := true

outputStrategy := Some(StdoutOutput)

packageArchetype.java_application
