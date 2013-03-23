# Overview

Beginnings of the HIV evolution parser that splits Genbank data into mutliple
FASTA files.

# Prerequisites

## Required

- Java Development Kit (JDK) through your package management system or from [Oracle](http://www.oracle.com/technetwork/java/javase/downloads)
- [sbt](http://www.scala-sbt.org/)

These really are the only required prerequisites.

## Optional

- [Eclipse 4.2.x IDE for Java Developers](http://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/junosr1)
- [Scala IDE Eclipse plugin](http://scala-ide.org/download/milestone.html#scala_ide_21_milestone_3) corresponding to your Eclipse version

# Command-line

## Running the sample application

This creates the launch script (so it is easy to run like a regular command line program):

    $ sbt stage

Then run the application outside of sbt like this:

    $ target/start filename+ (list of files)

As an example...

    $ target/start data/JX447638.gb data/JX447639.gb

# Generating FASTA by gene

You need to have a folder (that doesn't exist a priori) to write the results. If the folder exists
from a previous run, you must rename or delete it.

    $ target/start data/*.gb | python scripts/postprocess.py <folder-name> 

I usually do:

    $ rm -rf results
    $ target/start data/*.gb | python scripts/postprocess.py results 
    


# Eclipse

## Generating the configuration files

    $ sbt eclipse

# Experimental Mongo+Python Postprocessor

MongoDB is a promising NoSQL database that can really put this work on steroids.

This is sort of a mockup, which I think should be added to the Scala version!

- postprocess-mongo.py: Writes the delimited data as individual documents to the Mongo database
- postprocess-fasta.py: Extracts the records for one of the genes and generates a FASTA file (to standard output)

Example usage to get the "gag" gene:

	$ target/start data/*.gb | python scripts/postprocess-mongo.py collection-name

where *collection-name* should be replaced with a new (and empty) collection name.

	$ python scripts/postprocess-fasta.py collection-name gene-name

where *collection-name* should be replaced with a new (and empty) collection name and gene-name should be one of the extracted genes (e.g. gag, env, etc.)

If you want to write the FASTA output to a file, do:

	$ python scripts/postprocess-fasta.py collection-name gene-name > gene-name.fasta

This is much more flexible than our previous effort, which basically writes the files to hard-coded filenames. By using this process, we'll eventually be able to add or change the original GenBank data but have a completely decoupled process for generating FASTA for further analysis and visualization.







