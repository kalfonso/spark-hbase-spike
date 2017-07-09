name := "spark-hbase-spike"

version := "1.0"

scalaVersion := "2.10.6"

resolvers +=
  "HortonWorks repository" at "http://repo.hortonworks.com/content/repositories/releases"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.6.2" % "provided",
  "org.apache.spark" %% "spark-sql" % "1.6.2" % "provided",
  "org.apache.hbase" % "hbase-server" % "1.1.2" % "test",
  "org.scalatest" %% s"scalatest" % "3.0.3" % "test",
  "com.holdenkarau" % "spark-testing-base_2.10" % "1.6.3_0.7.0" % "test",
  "com.hortonworks" % "shc-core" % "1.1.1-1.6-s_2.10"
)

assemblyMergeStrategy in assembly := {
  case m if m.toLowerCase.endsWith("manifest.mf") => MergeStrategy.discard
  case m if m.startsWith("META-INF") => MergeStrategy.discard
  case PathList("javax", "servlet", xs @ _*) => MergeStrategy.first
  case PathList("org", "apache", xs @ _*) => MergeStrategy.first
  case PathList("org", "jboss", xs @ _*) => MergeStrategy.first
  case "about.html"  => MergeStrategy.rename
  case "reference.conf" => MergeStrategy.concat
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}


        