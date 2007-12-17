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

x -d sumpara
sumpara sumpara.def $K
cat $casename.scf2p >> $casename.scf2

x -d lcore
lcore lcore.def

cat $casename.scf0 $casename.scf1_1 $casename.scf2 $casename.scfc > $casename.scf
mv $casename.clmsum $casename.clmsum_old
x -d mixer
mixer mixer.def
cat $casename.scfm >>$casename.scf
result=`testconv -p :ENE -c 0.01 |cut -c1`
echo $result > result

echo `date +"%s.%N"` >> time.txt
#tar -czf sumup.tar  $casename.broyd1 $casename.broyd2 $casename.clmcor $casename.clmsum $casename.clmsum_old $casename.clmval $casename.inc $casename.inm $casename.outputm $casename.scf $casename.scf0 $casename.scf1_1 $casename.scf2 $casename.scfc $casename.scfm $casename.struct sumpara.def sumpara.error lcore.def lcore.error mixer.def mixer.error time.txt result
#mv sumup.tar ..


