package edu.luc.bioi.hiv

import java.io.{BufferedReader, FileReader}
import java.util.{Iterator => JIterator, Map => JMap}

import scala.collection.JavaConverters._
import scala.language.implicitConversions

import org.biojava.bio.seq.{Feature, Sequence, SequenceIterator}
import org.biojava.bio.seq.io.SeqIOTools
import org.biojava.bio.Annotation

object TryBio {
  case class SourceInformation(country: String, collectionDate: String, note: String)
  case class SequenceInformation(accession: String, origin: String)
  case class GeneInformation(gene: String, start : Int, end : Int)

  /**
   * Converts an annotation to a properly typed Scala map.
   */
  implicit def annotationAsScalaMap(annotation: Annotation) =
    annotation.asMap.asInstanceOf[JMap[String, String]].asScala

  def getSequenceInformation(sequence: Sequence): Option[SequenceInformation] =
    sequence.getAnnotation get "ACCESSION" map {
      acc =>
        val origin = sequence.seqString
        SequenceInformation(acc, origin)
    }

  private val UNKNOWN_COUNTRY = "unknown country"
  private val UNKNOWN_DATE    = "unknown date"
  private val UNKNOWN_NOTE    = "unknown note"

  def getSourceInformation(sequence: Sequence): Option[SourceInformation] =
    sequence.features.asScala.find {
      _.getType == "source"
    } map { f =>
      val a = f.getAnnotation
      SourceInformation(
        a.getOrElse("country", UNKNOWN_COUNTRY),
        a.getOrElse("collection_date", UNKNOWN_DATE),
        a.getOrElse("note", UNKNOWN_NOTE)
      )
    }

  private val allowedGenes = Set("gag", "pol", "env", "tat", "vif", "rev", "vpr", "vpu", "nef")

  def getGenes(sequence: Sequence): Iterator[GeneInformation]  =
    sequence.features.asScala.filter { f =>
      f.getType == "CDS" && (allowedGenes contains f.getAnnotation()("gene"))
    } map { f =>
      val a = f.getAnnotation
      val l = f.getLocation
      // these values start at 1, so we need to normalize for substring
      // to extract the subsequence
      GeneInformation(a("gene"), l.getMin - 1, l.getMax - 1)
    }

  /**
   * Conversion of SequenceIterator to generic Java iterator.
   */
  implicit class JavaSequenceIterator(it: SequenceIterator) extends JIterator[Sequence] {
    override def hasNext() = it.hasNext
    override def next() = it.nextSequence()
    override def remove() = throw new UnsupportedOperationException
  }

  def main(args: Array[String]) {
    for {
      arg <- args
      sequences: JIterator[Sequence] =
        SeqIOTools.readGenbank(new BufferedReader(new FileReader(arg)))
      seq <- sequences.asScala
      seqInfo <- getSequenceInformation(seq)
      sourceInfo <- getSourceInformation(seq)
      gene <- getGenes(seq)
    } println(seqInfo.accession, gene.gene, sourceInfo.collectionDate,
        sourceInfo.note, seqInfo.origin.substring(gene.start, gene.end))
  }
}