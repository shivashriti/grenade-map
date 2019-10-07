lazy val root = (project in file("."))
  .settings(
    name := "Grenade Map",
    version := "0.0.1",
    scalaVersion := "2.12.0",
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-swing" % "2.1.1",
      "com.typesafe.akka" %% "akka-actor" % "2.5.0",
      "org.specs2" %% "specs2-core" % "4.6.0" % "test"
    ),
    fork in run := true,
    fork := true,
    scalacOptions in Test ++= Seq("-Yrangepos"),
    assemblyJarName in assembly := "grenade-map.jar",
    test in assembly := {},
    mainClass in assembly := Some("com.shriti.grenademap.AppView")
  )
