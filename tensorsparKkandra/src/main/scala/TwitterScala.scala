/**
  * Created by Xisco Sastre & Alberto Pou
  */

import com.spark.streaming.StreamingLogger
import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.twitter._

import com.datastax.spark.connector.cql.CassandraConnector
import com.datastax.spark.connector._


object TwitterScala {

  def main(args: Array[String]) {

    // Set initial properties
    StreamingLogger.setStreamingLogLevels()
    Utils.setTwitterProperties()

    // Streaming definition (In local: .setMaster("local[n]"))
    val conf = new SparkConf().setAppName("CloudProject")
      .set("spark.cassandra.connection.host", "cassandra-01.xstrcbt.com")
    val ssc = new StreamingContext(conf, Seconds(1))
    val filter = Array(args(0))
    val stream = TwitterUtils.createStream(ssc, None, filter)

    // Cassandra connector configuration
    CassandraConnector(conf).withSessionDo { session =>
      session.execute("CREATE KEYSPACE IF NOT EXISTS tensorsparkkandra WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 2 }")
      session.execute("USE tensorsparkkandra")
      session.execute("CREATE TABLE IF NOT EXISTS tensorsparkkandra.images (imgid bigint, url text, category text, confidence double, Primary KEY (imgid, category, confidence))")
    }

    // Get tweet's media
    val media_computed = stream.filter(tweet => tweet.getMediaEntities.nonEmpty)
      .map(tweet => Utils.computeMediaObject(tweet.getMediaEntities()(0).getId, tweet.getMediaEntities()(0).getMediaURL))

    // Save images in Cassandra DB
    media_computed.foreachRDD(rdd => {
      rdd.saveToCassandra("tensorsparkkandra", "images", SomeColumns("imgid", "category", "confidence", "url"))
    })

    // Start streaming
    ssc.start

    // Wait for the computation to terminate
    ssc.awaitTermination

  }

}