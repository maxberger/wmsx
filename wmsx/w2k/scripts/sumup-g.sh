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

[ -f $casename.scf2p ] || exit 1
[ -f $casename.clmval ] || exit 1
[ -f $casename.scfc ] || exit 1
[ -f $casename.clmcor ] || exit 1
[ -f $casename.scf ] || exit 1
[ -f $casename.broyd1 ] || exit 1
[ -f $casename.broyd2 ] || exit 1
[ -f $casename.scfm ] || exit 1
[ -f result ] || exit 1
[ -f sumpara.error ] || exit 1
[ -s sumpara.error ] && exit 1
[ -f lcore.error ] || exit 1
[ -s lcore.error ] && exit 1
[ -f mixer.error ] || exit 1
[ -s sumpara.error ] && exit 1

exit 0

