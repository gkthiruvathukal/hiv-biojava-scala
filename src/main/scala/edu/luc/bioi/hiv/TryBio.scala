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
  case class SequenceInformation(accession: String)
  case class GeneInformation(gene: String, translation : String)

  /**
   * Converts an annotation to a properly typed Scala map.
   */
  implicit def annotationAsScalaMap(annotation: Annotation) =
    annotation.asMap.asInstanceOf[JMap[String, String]].asScala

  def getSequenceInformation(sequence: Sequence): Option[SequenceInformation] =
    sequence.getAnnotation get "ACCESSION" map SequenceInformation

/*  def writeSomeFasta(): Unit = {
    import org.biojavax.bio.seq._
    val sdb = new HashSequenceDB()
    val dna1 = DNATools.createDNASequence("agct", "Dna-12");
    sdb.addSequence(dna1)
    val dna2 = DNATools.createDNASequence("agagct", "Dna-13");
    sdb.addSequence(dna2)
    RichSequence.IOTools.writeFasta(System.out, sdb.sequenceIterator(), null);
  }*/

  def getSourceInformation(sequence: Sequence): Option[SourceInformation] =
    sequence.features.asScala.find {
      _.getType == "source"
    } map { f =>
      val a = f.getAnnotation
      // TODO discuss what should happen if a sequence is missing any of these
      // annotation properties
      // right now: unchecked exception with program termination
      // alternative: skip the sequence (using Option)
      SourceInformation(a("country"), a("collection_date"), a("note"))
    }

  private val allowedGenes = Set("gag", "pol", "env", "tat", "vif", "rev", "vpr", "vpu", "nef")

  def getGenes(sequence: Sequence): Iterator[GeneInformation]  =
    sequence.features.asScala.filter { f =>
      f.getType == "CDS" && (allowedGenes contains f.getAnnotation()("gene"))
    } map { f =>
      val a = f.getAnnotation
      GeneInformation(a("gene"), a("translation"))
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
      // TODO discuss whether each input file should result in a separate
      // output file
      seq <- sequences.asScala
      seqInfo <- getSequenceInformation(seq)
      sourceInfo <- getSourceInformation(seq)
      gene <- getGenes(seq)
    } println(seqInfo.accession, gene.gene, sourceInfo.collectionDate,
        sourceInfo.note, gene.translation)
  }
}