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
 
[ -f $casename.clmsum ] || exit 1
[ -f $casename.in0 ] || exit 1
[ -f $casename.inm ] || exit 1
[ -f $casename.output0 ] || exit 1
[ -f $casename.scf0 ] || exit 1
[ -f $casename.struct ] || exit 1
[ -f $casename.vns ] || exit 1
[ -f $casename.vsp ] || exit 1
[ -f lapw0.error ] || exit 1
[ -s lapw0.error ] && exit 1

exit 0

