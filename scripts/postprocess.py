#!/usr/bin/env python2

#
# This script assumes that the Scala program to parse the Genbank datasets has run and sent its
# output to standard output.
#
# At some point I (or Konstantin!) will rework the Scala output to write the results by gene type 
# so we can lazily processs the input stream.
#

import sys
import os
import os.path

FASTATEMPLATE=""">%(accession)s|%(gene)s|%(date)s|%(note)s
%(sequence)s"""

def getFiles(folder, names):
   files = {}
   for name in names:
      qname = os.path.join(folder, name)
      files[name] = open(qname, "w")
   return files

def closeFiles(files):
   for f in files.values():
      f.close()

def writeData(file, record):
   (accession, gene, country, date, note, sequence) = record
   file.write(FASTATEMPLATE % vars())

def main():
   folder = sys.argv[1]
   if os.path.exists(folder):
      print("Folder %s exists; please specify empty folder or new one" % folder)
      sys.exit(1)
   os.makedirs(folder)   
   genes = {}
   for line in sys.stdin:
      text = line.strip()
      (accession, gene, country, date, note, sequence) = data = line.split("|")[:6]
      records = genes.get(gene, [])
      records.append(data)
      genes[gene] = records

   files = getFiles(folder, genes.keys())
   for gene in genes.keys():
      for record in genes[gene]:
         writeData(files[gene], record)

   closeFiles(files)

main()