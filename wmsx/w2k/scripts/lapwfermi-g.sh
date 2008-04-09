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

K=`expr $(cat $casename.klist | wc -l) - 2`
export SCRATCH=.
x -d lapw2
sed -i 's/TOT/FERMI/' $casename.in2
lapw2 lapw2.def $K
sed -i 's/FERMI/TOT/' $casename.in2
date +"%s.%N"
