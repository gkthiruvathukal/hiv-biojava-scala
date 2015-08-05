package edu.luc.bioi.hiv

/*
 * This is from the Apache Spark docs. Using this as a quick sanity check for build environent.
 */
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import java.io.File
import java.nio.file._


object BioJavaSpark {


  def nanoTime[R](block: => R): (Double, R) = {
    val t0 = System.nanoTime()
    val result = block    // call-by-name
    val t1 = System.nanoTime()
    (t1 - t0, result)
  }

  def sparkMain(args: Array[String]) {

    val logFile = "hiv-biojava-spark.log" // Should be some file on your system
    val conf = new SparkConf().setAppName("Parallel GenBank Parsing")
    val sc = new SparkContext(conf)
    val logData = sc.textFile(logFile, 2).cache()
    val numAs = logData.filter(line => line.contains("a")).count()
    val numBs = logData.filter(line => line.contains("b")).count()
    println("Lines with a: %s, Lines with b: %s".format(numAs, numBs))

    import FileUtils.getFileList

    val fileList = getFileList( "./data", ".gb")

    nanoTime {
      for (filePath <- getFileList("./data", ".gb")) {
        val path = Paths.get(filePath)
        val text = Files.readAllLines(path)
        val joined = String.join("\n", text)
      }
    }
  }

  def readFile(path : Path) : String = {
    val text = Files.readAllLines(path)
    val joined = String.join("\n", text)
    joined
  }

  def main(args: Array[String]): Unit = {
    import FileUtils.getFileList

    val fileList = getFileList( "./data", ".gb")

    val (deltaT, data) = nanoTime {
      val data = getFileList("./data", ".gb") map { f => Paths.get(f) } map { p => readFile(p) }
      data // result of block
    }
    println(s"load time for ${fileList.length} files = $deltaT ns; avg = ${deltaT.toDouble / fileList.length}")
  }
}