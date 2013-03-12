package edu.luc.bioi.hiv

import java.io.BufferedReader
import java.io.FileReader

import org.biojava.bio.seq.Feature
import org.biojava.bio.seq.Sequence
import org.biojava.bio.seq.io.SeqIOTools
import scala.collection.JavaConverters._

object TryBio {
  case class SourceInformation(country: String, collection_date: String, note: String)
  case class SequenceInformation(accession: String)
  case class GeneInformation(gene: String, translation : String)

  def getSequenceInformation(sequence: Sequence): SequenceInformation = {
    val seqAnnotation = sequence.getAnnotation()
    require { seqAnnotation.containsProperty("ACCESSION") }
    SequenceInformation(seqAnnotation.getProperty("ACCESSION").toString)
  }

/*  def writeSomeFasta(): Unit = {
    import org.biojavax.bio.seq._
    val sdb = new HashSequenceDB()
    val dna1 = DNATools.createDNASequence("agct", "Dna-12");
    sdb.addSequence(dna1)
    val dna2 = DNATools.createDNASequence("agagct", "Dna-13");
    sdb.addSequence(dna2)
    RichSequence.IOTools.writeFasta(System.out, sdb.sequenceIterator(), null);
  }*/

  def getSourceInformation(sequence: Sequence): SourceInformation = {
    val featureIterator = sequence.features
    val features = featureIterator.asScala
    val items = features.find( _.getType == "source").map {
      f => val a = f.getAnnotation ;
      SourceInformation(a.getProperty("country").toString, a.getProperty("collection_date").toString,
           a.getProperty("note").toString)
    }
    items.get
  }

  def getGenes(sequence: Sequence)  =  {
    val featureIterator = sequence.features()
    val features = featureIterator.asScala

    val allowedGenes = Set("gag", "pol", "env", "tat", "vif", "rev", "vpr", "vpu", "nef")

    features.filter(n => n.getType == "CDS" && (allowedGenes contains n.getAnnotation().getProperty("gene").toString)).
       map {
         f => val a = f.getAnnotation;
         GeneInformation(a.getProperty("gene").toString, a.getProperty("translation").toString)
       }
  }

  def main(args: Array[String]): Unit = {
    val s = args(0)
    val br = new BufferedReader(new FileReader(s))
    val sequences = SeqIOTools.readGenbank(br)

    // val scalaSequences = sequences.asScala
    // For some reason: sequence iterators cannot be turned into Scala. Not that important.
    while (sequences.hasNext()) {
      val seq = sequences.nextSequence()
      val seqInfo = getSequenceInformation(seq);
      val sourceInfo = getSourceInformation(seq)
      val report = for (gene <- getGenes(seq))
        yield (seqInfo.accession, gene.gene.toString,
            sourceInfo.collection_date,
            sourceInfo.note,
            gene.translation)
      report foreach println
    }
  }
}