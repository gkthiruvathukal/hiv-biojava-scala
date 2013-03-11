package edu.luc.bioi.hiv

import org.biojava.bio._
import org.biojava.bio.seq._
import org.biojava.bio.seq.io._
import java.io._



object TryBio {
  case class SourceInformation( country : String, collection_date : String,  note : String )
  case class SequenceInformation( accession : String )

  def getSequenceInformation( sequence : Sequence) : SequenceInformation = {
      val seqAnnotation = sequence.getAnnotation()
      val accessionNumber = seqAnnotation.getProperty("ACCESSION").toString
      SequenceInformation( accessionNumber)
  }

  def getSourceInformation( sequence : Sequence ) : SourceInformation = {
      var featureIterator = sequence.features()
      var country = ""
      var collection_date = ""
      var note = ""

      while (featureIterator.hasNext()) {
         val feature = featureIterator.next()
         val featureType = feature.getType()
         val featureAnnotation = feature.getAnnotation()
         if (featureType == "source") {
            country = featureAnnotation.getProperty("country").toString()
            collection_date =  featureAnnotation.getProperty("collection_date").toString()
            note = featureAnnotation.getProperty("note").toString()
            println(s"FASTA_INFO  country = $country  collection_date=$collection_date  note=$note")
         }
      }
      SourceInformation(country, collection_date, note)
  }

  def processCDS( sequence : Sequence, seqInfo : SequenceInformation, sourceInfo : SourceInformation ) {
      var featureIterator = sequence.features()
      while (featureIterator.hasNext()) {
         val feature = featureIterator.next()
         val featureType = feature.getType()
         val accessionNumber = seqInfo.accession
         val featureAnnotation = feature.getAnnotation()
         val country = sourceInfo.country
         val collection_date = sourceInfo.collection_date
         val note = sourceInfo.note
         var iterator = featureAnnotation.keys().iterator()
         while (iterator.hasNext()) {
           val key = iterator.next()
           val value = featureAnnotation.getProperty(key)
         }

         val allowedGenes = Set("gag", "pol", "env", "tat", "vif", "rev", "vpr", "vpu", "nef")
         if (featureType == "CDS") {
            val translation = featureAnnotation.getProperty("translation")
            val gene = featureAnnotation.getProperty("gene").toString
            if (allowedGenes contains gene)
               println(s"$accessionNumber|$gene|$country|$collection_date|$note\n$translation")
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
      val seqInfo = getSequenceInformation( seq)

      val sourceInfo = getSourceInformation(seq)

      processCDS( seq, seqInfo, sourceInfo)
    }
  }
}