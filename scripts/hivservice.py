#
# Q&D web service that gets precomputed HIV data from our MongoDB 
#

from flask import *
from pymongo import *
import json
import StringIO
import socket
import re

FASTATEMPLATE=""">%(accession)s|%(gene)s|%(country)s|%(date)s|%(note)s
%(sequence)s"""

app = Flask(__name__)
app.debug = True

global database

def request_wants_json():
    best = request.accept_mimetypes \
        .best_match(['application/json', 'text/html'])
    return best == 'application/json' and \
        request.accept_mimetypes[best] > \
        request.accept_mimetypes['text/html']

#Function to setup mongoclient for db access
def setup_mongoclient(collection):
  client = MongoClient()
  db = client[collection]
  database = db
  return db

#Show available databases
@app.route("/genbank/")
def get_databases():
  client = MongoClient()
  db_names = client.database_names()
  text = '\n'.join(db_names)
  print("DB Names",text)
  resp = Response(text, status=200, mimetype='text/plain')
  resp.headers['Link'] = 'http://localhost'
  dbList = text.split('\n')
  return render_template("index.html", dbs=dbList)

def get_collection_gene_names_mongo(collection):
   db = setup_mongoclient(collection)
   return db.posts.distinct('gene')

def get_fasta(collection, gene):
   db = setup_mongoclient(collection)
   cursor = db.posts.find({ 'gene' : gene })
   fasta = StringIO.StringIO()
   for item in cursor:
      fasta.write(FASTATEMPLATE % item)
   text = fasta.getvalue()
   fasta.close()
   return text

def get_accession(collection, number):
  db = setup_mongoclient(collection)
  cursor = db.posts.find({ 'accession' : number})
  fasta = StringIO.StringIO()
  for item in cursor:
    fasta.write(FASTATEMPLATE % item)
  text = fasta.getvalue()
  fasta.close()
  return text

def date_after(collection, gene, year):
  db = setup_mongoclient(collection)
  cursor = db.posts.find({'gene' : gene , 'date': year })
  fasta = StringIO.StringIO()
  for item in cursor:
    fasta.write(FASTATEMPLATE % item)
  text = fasta.getvalue()
  fasta.close()
  return text

def date_range(collection, gene, firstYear, secondYear):
  db = setup_mongoclient(collection)
  cursor = db.posts.find({'gene' : gene , 'date' : {'$gte': firstYear, '$lte': secondYear}})
  fasta = StringIO.StringIO()
  for item in cursor:
    fasta.write(FASTATEMPLATE % item)
  text = fasta.getvalue()
  fasta.close()
  return text 

def get_by_location(collection, gene, country):
  db = setup_mongoclient(collection)
  cursor = db.posts.find({"gene" : gene, "country" : country})
  fasta = StringIO.StringIO()
  for item in cursor:
    fasta.write(FASTATEMPLATE % item)
  text = fasta.getvalue()
  fasta.close()
  return text

@app.route("/")
def defaultRoute():
  return redirect("/genbank/")

@app.route("/genbank/<collection>/")
def get_collection_gene_names(collection):
   text = '\n'.join( get_collection_gene_names_mongo(collection))
   #resp = Response(text, status=200, mimetype='text/plain')
   #resp.headers['Link'] = 'http://localhost'
   geneList = text.split('\n')
   return render_template("geneView.html", genes=geneList)
  
#Query for specific gene
@app.route("/genbank/<collection>/<gene>/")
def get_collection_gene(collection, gene):
  #resp = Response(get_fasta(collection, gene), status=200, mimetype='text/plain')
  #resp.headers['Link'] = 'http://localhost'
  text = get_fasta(collection, gene).split('\n')
  return render_template("selectGene.html", fastas=text)

#Query for gene in specific year
@app.route("/genbank/<collection>/<gene>/date/<year>/")
def query_date(collection, gene, year):
  resp = Response(date_after(collection, gene, year), status=200, mimetype='text/plain')
  resp.headers['Link'] = 'http://localhost'
  return resp

#Query date range of years:  Assume format xxxx-yyyy where xxxx is a year
#that is less than yyyy
@app.route("/genbank/<collection>/<gene>/<range>/")
def query_range(collection, gene, range):
  regex = re.compile("([0-9]+)\-([0-9]+)")
  r = regex.search(range)
  firstYear = r.group(1)
  secondYear = r.group(2)
  resp = Response(date_range(collection, gene, firstYear, secondYear), status=200, mimetype='text/plain')
  resp.headers['Link'] = 'http://localhost'
  return resp

#Query gene by country of isolation
@app.route("/genbank/<collection>/<gene>/location/<country>/")
def query_location(collection, gene, country):
  resp = Response(get_by_location(collection, gene, country), status=200, mimetype='text/plain')
  resp.headers['Link'] = 'http://localhost'
  return resp

#Query sequence by accession number
@app.route("/genbank/<collection>/accession/<number>")
def query_accession(collection, number):
  resp = Response(get_accession(collection, number), status=200, mimetype='text/plain')
  resp.headers['Link'] = 'http://localhost'
  return resp

@app.route("/genbank/<collection>/unknown/<thing>/")
def get_unknown_thing(collection, thing):
   db = setup_mongoclient(collection)
   textfile = StringIO.StringIO()

   if thing in ['country', 'date', 'note']:
     results = db.posts.find({ thing : 'unknown '+thing })
     unknowns = set( r['accession'] for r in results )
     for unknown in unknowns:
        textfile.write(unknown + '\n')

   resp = Response(textfile.getvalue(), status=200, mimetype='text/plain')
   textfile.close()
   resp.headers['Link'] = 'http://localhost'
   return resp








# new route will accept both a GET and POST request from the client (web browser)
@app.route("/genbank/<collection>/<gene>/form", methods=["POST"])
def getQuery(collection, gene):
  print "test"
  queryType = request.form["type"]
  queryValue = request.form["value"]
  #queryValue = request.args.get('value')
  #print queryType
  print queryValue
  print database
  print queryType

  #if (queryType == "location"):
  
  c=collection
  g= gene

  return redirect(url_for('query_location', collection = c, gene = g, country = queryValue))










#Running app on localhost
if __name__ == "__main__":
    app.run(port=5050)
