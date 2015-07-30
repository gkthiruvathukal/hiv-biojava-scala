package edu.luc.bioi.hiv

/*
 * This is from the Apache Spark docs. Using this as a quick sanity check for build environent.
 */
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import java.io.File

object BioJavaSpark {



  def main(args: Array[String]) {

    val logFile = "YOUR_SPARK_HOME/README.md" // Should be some file on your system
    val conf = new SparkConf().setAppName("Simple Application")
    val sc = new SparkContext(conf)
    val logData = sc.textFile(logFile, 2).cache()
    val numAs = logData.filter(line => line.contains("a")).count()
    val numBs = logData.filter(line => line.contains("b")).count()
    println("Lines with a: %s, Lines with b: %s".format(numAs, numBs))
  }
}