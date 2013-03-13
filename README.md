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
