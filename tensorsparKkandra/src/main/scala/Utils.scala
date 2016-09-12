/**
  * Created by Xisco Sastre & Alberto Pou
  */

import java.io.File
import java.net.URL
import java.security.MessageDigest

import scala.sys.process._


object Utils {

  /**
    * Function that sets the Twitter properties to interact with the API
    */
  def setTwitterProperties() {
    System.setProperty("twitter4j.oauth.consumerKey", "XXX")
    System.setProperty("twitter4j.oauth.consumerSecret", "XXX")
    System.setProperty("twitter4j.oauth.accessToken", "XXX")
    System.setProperty("twitter4j.oauth.accessTokenSecret", "XXX")
  }

  /**
    * Function that dowloads an image in a temp directory
    * @param url: Image url
    * @return Image path
    */
  def downloadFile(url: String): String = {
    val filename = md5(url) + ".jpg"
    new URL(url) #> new File("/tmp/cc/" + filename) !!;
    "/tmp/cc/" + filename
  }

  /**
    * Funtion that produces a hash of a string
    * @param s String
    * @return Hash key
    */
  def md5(s: String) = {
    MessageDigest.getInstance("MD5").digest(s.getBytes).toString()
  }

  /**
    * Function that classify an image using a tensorflow algorithm
    * @param img_path Image path to classify
    * @return Classification results
    */
  def classifyImage(img_path: String): String = {
    // val virtualenv_path = "/Users/xiscosastre/.virtualenvs/bsc/"
    val virtualenv_path = "/root/.virtualenvs/tensorsparkkandra/"
    val classify_image_path = virtualenv_path + "lib/python2.7/site-packages/tensorflow/models/image/imagenet/classify_image.py"
    val command = virtualenv_path + "bin/python " + classify_image_path + " --image_file=" + img_path
    command !!
  }

  /**
    * Function that get the category of the classification results
    * @param classification_results Tensorflow algorithm results
    * @return Image category
    */
  def getCategory(classification_results: String): String = {
    classification_results.split("\n")(0).split("score = ")(0).dropRight(2)
  }

  /**
    * Function that gets the confidence of the classification results
    * @param classification_results Tensorflow algorithm results
    * @return Image confidence
    */
  def getConfidence(classification_results: String): Double = {
    classification_results.split("\n")(0).split("score = ")(1).dropRight(1).toDouble
  }

  /**
    * Class Image that represented in our Cassandra DB
    * @param imgid
    * @param category
    * @param confidence
    * @param url
    */
  case class Image(imgid: Long, category: String, confidence: Double, url: String)

  /**
    * Function that generates our object Image based on Twitter image information
    * @param imgid Image id
    * @param url Image url
    * @return Image object
    */
  def computeMediaObject(imgid: Long, url: String): Image = {
    val image_path = Utils.downloadFile(url)
    val result = Utils.classifyImage(image_path)
    Image(imgid, Utils.getCategory(result), Utils.getConfidence(result), url)
  }

}
