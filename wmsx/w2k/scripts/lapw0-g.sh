#!/bin/bash -x
date +"%s.%N"
export MAIN=$PWD
casename=$1
mkdir $PWD/$casename
export SCRATCH=$PWD/$casename
mv $casename* $SCRATCH/
cd $MAIN
. ./basesetup.sh
cd $SCRATCH

date +"%s.%N"
x -d lapw0
lapw0 lapw0.def
date +"%s.%N"
 


