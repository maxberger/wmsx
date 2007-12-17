#!/bin/bash -x
export MAIN=$PWD
casename=$1
mkdir $PWD/$casename
export SCRATCH=$PWD/$casename
mv $casename* $SCRATCH/
cd $MAIN
. ./basesetup.sh
cd $SCRATCH

echo `date +"%s.%N"` >> time.txt
x -d lapw0
lapw0 lapw0.def
echo `date +"%s.%N"` >> time.txt
 


