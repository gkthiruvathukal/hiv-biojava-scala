package edu.luc.bioi.hiv

import java.io.File

object Utils {

  def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }

  // TODO: Eventually, convert to Java 7/8 directory stream
  def getFileList(path : String, ext : String) : Array[String] = {
    require { ext.startsWith(".") }
    val fullPath = new File(path).getAbsolutePath()
    recursiveListFiles( new File(fullPath) ).filter( f => f.getName().endsWith(ext)).map(_.getAbsolutePath())
  }

}
