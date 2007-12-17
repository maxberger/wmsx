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

K=`expr $(cat $casename.klist | wc -l) - 2`
export SCRATCH=.
x -d lapw2
sed -i 's/TOT/FERMI/' $casename.in2
lapw2 lapw2.def $K
sed -i 's/FERMI/TOT/' $casename.in2
echo `date +"%s.%N"` >> time.txt

#tar -czf lapwfermiout.tar $casename.energy_* $casename.energydn_* $casename.in1 $casename.in2 $casename.kgen $casename.klist* $casename.output2 $casename.scf2 $casename.struct $casename.vector_* $casename.vsp $casename.weigh_* $casename.weight lapw*.error time.txt *.def *.clmval*

#mv *.tar ..


