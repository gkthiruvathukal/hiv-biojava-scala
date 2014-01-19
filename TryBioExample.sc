/**
 * Created by gkt on 1/19/14.
 */

import java.io.File

/* Get current directory (just to see how IntelliJ sets it. */

val current = new java.io.File( "." ).getCanonicalPath()
println("Working directory is " + current)
/* List contents of the data directory. */
val directory = new File("./data").listFiles()

/* Select only the Genbank files */
val selectedFiles = directory.filter(_.getName().endsWith(".gb"))

/* Get as Array[String], needed by our current main() */
val selectedFileNames = selectedFiles.map( a => a.getCanonicalPath())

/* Use the first 5 files as input. (Drop take(5) if you want to process entire data set. */
edu.luc.bioi.hiv.TryBio.main(selectedFileNames.take(5))

