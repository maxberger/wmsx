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
Idx=$2

date +"%s.%N"
x -d lapw2

sed "s/\/.*${casename}\./${casename}./" lapw2.def > lapw2.defx
sed s/output2\'/output2_$Idx\'/ lapw2.defx | sed s/clmval\'/clmval_$Idx\'/ | sed s/vector\'/vector_$Idx\'/ | sed s/scf2\'/scf2_$Idx\'/ | sed s/weigh\'/weigh_$Idx\'/ | sed s/weighdn\'/weighdn_$Idx\'/ | sed s/vrespval\'/vrespval_$Idx\'/ | sed s/energydn\'/energydn_$Idx\'/ | sed s/energy\'/energy_$Idx\'/ | sed s/help\'/help_$Idx\'/ > lapw2_$Idx.def

lapw2 lapw2_$Idx.def $Idx

date +"%s.%N"
