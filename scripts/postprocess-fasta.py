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
from pymongo import *

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
   mongo_db_name = sys.argv[1]
   gene = sys.argv[2]
   client = MongoClient()
   db = client[mongo_db_name]
   cursor = db.posts.find({ 'gene' : gene })
   for item in cursor:
      print(FASTATEMPLATE % item)

main()