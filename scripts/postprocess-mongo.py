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
import sys
from pymongo import *


FASTATEMPLATE=""">%(accession)s|%(gene)s|%(date)s|%(note)s
%(sequence)s"""

# We need to make sure these characters don't end up in the FASTA. Best
# to make sure they don't end up in the db to begin with.
def clean(text):
   return text.replace(';','').replace(':','').replace('(','').replace(')','')
   
def main():

   mongo_db_name = sys.argv[1]

   # Assume Mongo is running on localhost at its defaults

   client = MongoClient()
   db = client[mongo_db_name]

   folder = sys.argv[1]
   if db.posts.count() > 0:
      print("Mongo database %s is not empty. Please create new" % folder)
      sys.exit(1)

   for line in sys.stdin:
      text = line.strip()
      (accession, gene, country, date, note, sequence) = data = line.split("|")[:6]
      document = {
         'accession' : clean(accession),
         'gene' : clean(gene), 
         'country' : clean(country),
         'date' : clean(date),
         'note' : clean(note),
         'sequence' : sequence
      }
      db.posts.insert(document)

      document['sequence'] = sequence[0:min(len(sequence), 20)] + "..."
      print("Wrote " + str(document))
   print("Wrote %d documents" % db.posts.count())
main()
