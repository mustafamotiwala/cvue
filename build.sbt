import ScalateKeys._

name := "CingleVue Coding Challenge"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.10.2"

seq(scalateSettings:_*)

seq(scalatraWithJRebel:_*)

//scanDirectories in Compile := Nil

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

resolvers ++= Seq(
  "Scala Tools Releases"           at    "http://scala-tools.org/repo-releases/",
  "Sonatype"                       at    "http://oss.sonatype.org/content/repositories/releases",
  "Java.net Maven2 Repository"     at    "http://download.java.net/maven/2/",
  "Typesafe Repository"            at    "http://repo.typesafe.com/typesafe/releases/"
)


libraryDependencies ++= {
  val scalatraVersion = "2.2.1"
  Seq(
    "org.scalatra" %% "scalatra"          % scalatraVersion,
    "org.scalatra" %% "scalatra-scalate"  % scalatraVersion,
    "org.scalatra" %% "scalatra-json"     % "2.2.1",
    "org.scalatra" %% "scalatra-specs2"   % scalatraVersion   % "test"
  )
}

libraryDependencies ++= Seq(
  "org.eclipse.jetty"         %  "jetty-webapp"         % "8.1.10.v20130312"      % "container",
  "org.json4s"                %% "json4s-jackson"       % "3.2.4",
  "org.mongodb"               %% "casbah"               % "2.6.3", //Useful for connecting to Mongo from REPL
  "org.reactivemongo"         %% "reactivemongo"        % "0.9",   //For connecting to Mongo from within App
  "org.eclipse.jetty.orbit"   %  "javax.servlet"        % "3.0.0.v201112011016"   % "container;provided;test" artifacts Artifact("javax.servlet", "jar", "jar"),
  "ch.qos.logback"            %  "logback-classic"      % "1.0.11"                % "compile"
)

port in container.Configuration := 8080

//Setup the Scalate template engine to compile
scalateTemplateConfig in Compile <<= (sourceDirectory in Compile){ base =>
  Seq(
    TemplateConfig(
      base / "webapp" / "WEB-INF" / "templates",
      Seq.empty,  /* default imports should be added here */
      Seq(
        Binding("context", "_root_.org.scalatra.scalate.ScalatraRenderContext", importMembers = true, isImplicit = true)
      ),  /* add extra bindings here */
      Some("templates")
    )
  )
}

EclipseKeys.withSource := true
