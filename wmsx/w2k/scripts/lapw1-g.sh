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

Idx=$2
sed -n "${Idx}p" $casename.klist > $casename.klist_$Idx
echo END >> $casename.klist_$Idx
x -d lapw1
sed s/klist\'/klist_$Idx\'/ lapw1.def | sed s/output1\'/output1_$Idx\'/ | sed s/vector\'/vector_$Idx\'/ | sed s/energy\'/energy_$Idx\'/ | sed s/scf1\'/scf1_$Idx\'/ > lapw1_$Idx.def

if [ $Idx -gt 1 ]; then
	head -n `expr $(cat  lapw1_$Idx.def | wc -l) - 1` lapw1_$Idx.def >tmp$Idx
	cat tmp$Idx >lapw1_$Idx.def
fi

lapw1 lapw1_$Idx.def
echo `date +"%s.%N"` >> time.txt
#tar -czf lapw1-out$Idx.tar $casename.energy_* $casename.in1  $casename.klist_* $casename.output1_* $casename.scf1_* $casename.struct $casename.vector_* $casename.vsp lapw1_*.error time.txt $casename.nsh *.def
