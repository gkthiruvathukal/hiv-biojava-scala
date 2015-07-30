package edu.luc.bioi.hiv

import java.io.File

/**
 * Created by gkt on 7/30/15.
 * Scala a bit lacking in Scala-esque utilities for File/Directory handling.
 * In the meantime, this is inspired by:
 * http://stackoverflow.com/questions/2637643/how-do-i-list-all-files-in-a-subdirectory-in-scala
 *
 *
 */
object FileUtils {

  def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }

  def getFileList(path : String, ext : String) : Array[String] = {
    require { ext.startsWith(".") }
    val fullPath = new File(path).getAbsolutePath()
    recursiveListFiles( new File(fullPath) ).filter( f => f.getName().endsWith(ext)).map(_.getAbsolutePath())
  }
}
