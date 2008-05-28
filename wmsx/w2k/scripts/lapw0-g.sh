#!/bin/bash -x
date +"%s.%N"
export MAIN=$PWD
casename=$1
export SCRATCH=$MAIN/$casename
mkdir -p $SCRATCH
mv $casename* $SCRATCH/
export WIENROOT=$PWD/WIEN2K
export PATH=$WIENROOT:$PATH
cd $SCRATCH

date +"%s.%N"
x -d lapw0
lapw0 lapw0.def
date +"%s.%N"
 


