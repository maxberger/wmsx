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

rm -f $casename.nsh

Idx=$2
sed -n "${Idx}p" $casename.klist > $casename.klist_$Idx
echo END >> $casename.klist_$Idx
x -d lapw1
sed "s/\/.*${casename}.vec/${casename}.vec/" lapw1.def > lapw1.defx
sed s/klist\'/klist_$Idx\'/ lapw1.defx | sed s/output1\'/output1_$Idx\'/ | sed s/vector\'/vector_$Idx\'/ | sed s/energy\'/energy_$Idx\'/ | sed s/scf1\'/scf1_$Idx\'/ > lapw1_$Idx.def

if [ $Idx -gt 1 ]; then
	head -n `expr $(cat  lapw1_$Idx.def | wc -l) - 1` lapw1_$Idx.def >tmp$Idx
	cat tmp$Idx >lapw1_$Idx.def
fi

lapw1 lapw1_$Idx.def
date +"%s.%N"

[ -f $casename.energy_$Idx ] || exit 1
[ -f $casename.scf1_$Idx ] || exit 1
[ -f $casename.vector_$Idx ] || exit 1
[ -f lapw1_$Idx.error ] || exit 1
[ -s lapw1_$Idx.error ] && exit 1

exit 0

