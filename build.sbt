name := "ScalaPlay"

version := "1.0"

lazy val `scalaplay` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq( jdbc , anorm , cache , ws )

libraryDependencies ++= Seq(
  "jp.t2v" %% "play2-auth"      % "0.13.0",
  "jp.t2v" %% "play2-auth-test" % "0.13.0" % "test"
)

libraryDependencies += "postgresql" % "postgresql" % "9.1-901-1.jdbc4"

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  