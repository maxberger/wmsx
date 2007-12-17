#!/bin/bash -x
export MAIN=$PWD
casename=$1
mkdir $PWD/$casename
export SCRATCH=$PWD/$casename
mv $casename* $SCRATCH/
cd $MAIN
. ./basesetup.sh
cd $SCRATCH
Idx=$2

echo `date +"%s.%N"` >> time.txt
x -d lapw2
sed s/output2\'/output2_$Idx\'/ lapw2.def | sed s/clmval\'/clmval_$Idx\'/ | sed s/vector\'/vector_$Idx\'/ | sed s/scf2\'/scf2_$Idx\'/ | sed s/weigh\'/weigh_$Idx\'/ | sed s/weighdn\'/weighdn_$Idx\'/ | sed s/vrespval\'/vrespval_$Idx\'/ | sed s/energydn\'/energydn_$Idx\'/ | sed s/energy\'/energy_$Idx\'/ | sed s/help\'/help_$Idx\'/ > lapw2_$Idx.def

lapw2 lapw2_$Idx.def $Idx
echo `date +"%s.%N"` >> time.txt
#tar -czf lapw2-out$Idx.tar  $casename.clmval_*  $casename.energy_* $casename.help_* $casename.in2 $casename.kgen $casename.output2_* $casename.scf2_* $casename.struct $casename.vector_* $casename.vrespval_* $casename.vsp $casename.weigh_* lapw2_*.error time.txt *.def 

