lazy val root = project
  .in(file((".")))
  .settings(commonSettings)
  .aggregate(core, api, batch)

lazy val core = project
  .in(file("core"))
  .settings(commonSettings)
  .settings(coreSettings)

lazy val api = project
  .in(file("api"))
  .dependsOn(core % "compile->compile; test->test")
  .settings(apiSettings)
  .settings(commonSettings)

lazy val batch = project
  .in(file("batch"))
  .dependsOn(core % "compile->compile; test->test")
  .settings(batchSettings)
  .settings(commonSettings)

lazy val commonSettings = Seq(
  scalaVersion := "2.11.12",
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "utf-8",
    "-explaintypes",
    "-feature",
    "-language:higherKinds",
  ),
  scalacOptions in (Compile, console) := (scalacOptions in (Compile, console)).value,
  scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value,

  resolvers += Resolver.sonatypeRepo("releases"),

  test in assembly := {},

  assemblyMergeStrategy in assembly := {
    case PathList(ps @ _*) if ps.last.endsWith(".properties") => MergeStrategy.first
    case PathList(ps @ _*) if ps.last.endsWith(".class")      => MergeStrategy.first
    case PathList(ps @ _*) if ps.last == "BUILD"              => MergeStrategy.discard
    case ".gitkeep"                                           => MergeStrategy.first
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  }
)

lazy val coreSettings = coreLibraryDependencies ++ coreTestingLibraryDependencies

lazy val coreLibraryDependencies = Seq(
  libraryDependencies ++= Seq(
    "com.github.pureconfig" %% "pureconfig"         % "0.12.1",
    "com.twitter"           %% "util-app"           % "19.12.0",
    "com.twitter"           %% "util-logging"       % "19.12.0",
    "eu.timepit"            %% "refined-pureconfig" % "0.9.10",
    "net.jafama"            %  "jafama"             % "2.3.1",
  )
)

lazy val coreTestingLibraryDependencies = Seq(
  libraryDependencies ++= Seq(
    "org.scalacheck" %% "scalacheck"   % "1.14.2" % "test",
    "org.scalatest"  %% "scalatest"    % "3.0.8"  % "test",
    "org.mockito"    %  "mockito-core" % "3.1.0"  % "test",
  )
)

lazy val apiSettings = apiLibraryDependencies

lazy val apiLibraryDependencies = Seq()

lazy val batchSettings = batchLibraryDependencies

lazy val sparkVersion = "2.4.4"

lazy val batchLibraryDependencies = Seq(
  libraryDependencies ++= Seq(
    "com.treasuredata.client" %  "td-client"   % "0.9.0",
    "org.apache.hadoop"       %  "hadoop-aws"  % "3.2.1"      % "provided",
    "org.apache.spark"        %% "spark-core"  % sparkVersion % "provided",
    "org.apache.spark"        %% "spark-mllib" % sparkVersion % "provided",
    "org.apache.spark"        %% "spark-sql"   % sparkVersion % "provided",
  )
)

addCommandAlias("fmt", ";scalafmt ;test:scalafmt")

scalafmtVersion in ThisBuild := "1.5.1"
scalafmtTestOnCompile in ThisBuild := true
scalafmtShowDiff in (ThisBuild, scalafmt) := true
