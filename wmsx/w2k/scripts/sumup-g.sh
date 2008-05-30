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

x -d sumpara
sumpara sumpara.def $2
cat $casename.scf2p >> $casename.scf2

x -d lcore
lcore lcore.def

cat $casename.scf0 $casename.scf1_1 $casename.scf2 $casename.scfc >> $casename.scf
cp $casename.clmsum $casename.clmsum_old
x -d mixer
mixer mixer.def
cat $casename.scfm >>$casename.scf
result=`testconv -p :ENE -c 0.01 |cut -c1`
echo $result > result

date +"%s.%N"
