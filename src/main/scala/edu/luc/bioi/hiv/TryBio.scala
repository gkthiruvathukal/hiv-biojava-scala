package edu.luc.bioi.hiv

import java.io.BufferedReader
import java.io.FileReader
import org.biojava.bio.seq.Sequence
import org.biojava.bio.seq.io.SeqIOTools

object TryBio {
  case class SourceInformation(country: String, collection_date: String, note: String)
  case class SequenceInformation(accession: String)

  def getSequenceInformation(sequence: Sequence): SequenceInformation = {
    val seqAnnotation = sequence.getAnnotation()
    if (seqAnnotation.containsProperty("ACCESSION")) {
      SequenceInformation(seqAnnotation.getProperty("ACCESSION").toString)
    } else
      null
  }

  /*
  def writeSomeFasta(): Unit = {
    import org.biojavax.bio.seq._
    val sdb = new HashSequenceDB()
    val dna1 = DNATools.createDNASequence("agct", "Dna-12");
    sdb.addSequence(dna1)
    val dna2 = DNATools.createDNASequence("agagct", "Dna-13");
    sdb.addSequence(dna2)
    RichSequence.IOTools.writeFasta(System.out, sdb.sequenceIterator(), null);
  }
  *
  */

  def getSourceInformation(sequence: Sequence): SourceInformation = {
    var featureIterator = sequence.features()

    while (featureIterator.hasNext()) {
      val feature = featureIterator.next()
      val featureType = feature.getType()
      val featureAnnotation = feature.getAnnotation()
      if (featureType == "source") {
        val country = featureAnnotation.getProperty("country").toString
        val collection_date = featureAnnotation.getProperty("collection_date").toString
        val note = featureAnnotation.getProperty("note").toString
        return SourceInformation(country, collection_date, note)
      }
    }
    null
  }

  def processCDS(sequence: Sequence, seqInfo: SequenceInformation, sourceInfo: SourceInformation) {
    var featureIterator = sequence.features()
    while (featureIterator.hasNext()) {
      val feature = featureIterator.next()
      val featureType = feature.getType()
      val featureAnnotation = feature.getAnnotation()
      var iterator = featureAnnotation.keys().iterator()

      val allowedGenes = Set("gag", "pol", "env", "tat", "vif", "rev", "vpr", "vpu", "nef")
      if (featureType == "CDS") {
        val translation = featureAnnotation.getProperty("translation")
        val gene = featureAnnotation.getProperty("gene").toString
        if (allowedGenes contains gene)
          println(seqInfo.accession + "|" + gene + "|" + sourceInfo.country + "|" + sourceInfo.collection_date + "|" + sourceInfo.note + "|" + translation)
        // else safe to ignore
      } // else safe to ignore non CDS records
    }
  }

  def main(args: Array[String]): Unit = {

    val s = args(0)
    val br = new BufferedReader(new FileReader(s))
    val sequences = SeqIOTools.readGenbank(br)

    while (sequences.hasNext()) {
      val seq = sequences.nextSequence()
      val seqInfo = getSequenceInformation(seq)
      if (seqInfo != null) {
         val sourceInfo = getSourceInformation(seq)
            if (sourceInfo != null)
              processCDS(seq, seqInfo, sourceInfo)
      }
    }
  }
}