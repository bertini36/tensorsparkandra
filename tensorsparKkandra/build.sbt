name := "tensorsparKkandra"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies += "org.apache.spark" %% "spark-core" % "1.6.1"

libraryDependencies += "org.apache.spark" %% "spark-streaming" % "1.6.1"

libraryDependencies += "org.apache.spark" %% "spark-streaming-twitter" % "1.6.1"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "1.6.1"
libraryDependencies += "com.datastax.spark" %% "spark-cassandra-connector" % "1.6.0-M2"
libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-core" % "3.0.2"

resolvers ++= Seq(
  "Akka Repository" at "http://repo.akka.io/releases/",
  "Spray Repository" at "http://repo.spray.cc/")