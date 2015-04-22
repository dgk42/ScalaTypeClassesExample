name := "TypeClassesExample"

version := "1.0"


scalaVersion := "2.11.6"


resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases"
resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "org.scalaz"     %% "scalaz-core" % "7.1.1",
  "org.scalacheck" %% "scalacheck"  % "1.12.2" % "test",
  "junit"           % "junit"       % "4.12"   % "test",
  "org.scalatest"  %% "scalatest"   % "2.2.4"  % "test")

scalacOptions ++= Seq(
  "-target:jvm-1.7",
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xfuture",
  "-Xlint",
  "-Ywarn-dead-code")


// Include only src/main/scala in the compile configuration
unmanagedSourceDirectories in Compile <<= (scalaSource in Compile)(Seq(_))

// Include only src/main/scala in the test configuration
unmanagedSourceDirectories in Test <<= (scalaSource in Test)(Seq(_))


// begin eclipse plugin
EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource
// end
