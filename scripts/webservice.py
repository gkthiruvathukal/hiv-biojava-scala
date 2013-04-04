#
# Q&D web service that gets precomputed HIV data from our MongoDB 
#

from flask import *
from pymongo import *
import json
import StringIO

FASTATEMPLATE=""">%(accession)s|%(gene)s|%(date)s|%(note)s
%(sequence)s"""

app = Flask(__name__)
app.debug = False

def request_wants_json():
    best = request.accept_mimetypes \
        .best_match(['application/json', 'text/html'])
    return best == 'application/json' and \
        request.accept_mimetypes[best] > \
        request.accept_mimetypes['text/html']

@app.route("/data")
def get_databases():
  client = MongoClient()
  db_names = client.database_names()
  text = '\n'.join(db_names)
  print("DB Names",text)
  resp = Response(text, status=200, mimetype='text/plain')
  resp.headers['Link'] = 'http://localhost'
  return resp

def get_collection_gene_names_mongo(collection):
   client = MongoClient()
   db = client[collection]
   return db.posts.distinct('gene')

def get_fasta(collection, gene):
   client = MongoClient()
   db = client[collection]
   cursor = db.posts.find({ 'gene' : gene })
   fasta = StringIO.StringIO()
   for item in cursor:
      fasta.write(FASTATEMPLATE % item)
   text = fasta.getvalue()
   fasta.close()
   return text

@app.route("/data/<collection>")
def get_collection_gene_names(collection):
   text = '\n'.join( get_collection_gene_names_mongo(collection))
   resp = Response(text, status=200, mimetype='text/plain')
   resp.headers['Link'] = 'http://localhost'
   return resp
  

@app.route("/data/<collection>/<gene>")
def get_collection_gene(collection, gene):
   resp = Response(get_fasta(collection, gene), status=200, mimetype='text/plain')
   resp.headers['Link'] = 'http://localhost'
   return resp

if __name__ == "__main__":
    app.run(host='0.0.0.0')

