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

K=`expr $(cat $casename.klist | wc -l) - 2`
export SCRATCH=.
x -d lapw2
sed -i 's/TOT/FERMI/' $casename.in2
lapw2 lapw2.def $K
sed -i 's/FERMI/TOT/' $casename.in2
date +"%s.%N"

[ -f $casename.recprlist ] || exit 1
[ -f $casename.scf2 ] || exit 1
[ -f lapw2.error ] || exit 1
[ -s lapw2.error ] && exit 1

exit 0

