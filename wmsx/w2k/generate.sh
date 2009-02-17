#!/bin/bash
casename=$1
if [ -z "$casename" ] ; then
  echo Not enough parameters
  exit 1
fi

count=$(($(cat $casename/$casename.klist|wc -l)-2))

lapw1=
lapw2=
vecen=
weigh=
scfclm=

function replace {
  source=$1
  target=$2
  idx=$3
  echo replacing $source $target $casename $idx
  cat $source | sed s/IDX/$idx/g | sed s/LAPW1/$lapw1/g | sed s/LAPW2/$lapw2/g | sed s/WEIGH/$weigh/g | sed s/VECEN/$vecen/g | sed s/SCFCLM/$scfclm/g | sed s/CASENAME/$casename/g > $target
}

i=0
while [ $i -lt $count ] ; do
  i=$(($i+1))
  file1=lapw1_$i.jdl
  replace lapw1.orig $file1 $i 
  file2=lapw2_$i.jdl
  replace lapw2.orig $file2 $i
  if [ ! -z "$lapw1" ] ; then
    lapw1=${lapw1},
    lapw2=${lapw2},
    vecen=${vecen},
    weigh=${weigh},
    scfclm=${scfclm},
  fi
  lapw1=${lapw1}\"${file1}\"
  lapw2=${lapw2}\"${file2}\"
  vecen=${vecen}\"$casename\\/$casename.vector_$i\",\"$casename\\/$casename.energy_$i\"
  scfclm=${scfclm}\"$casename\\/$casename.scf2_$i\",\"$casename\\/$casename.clmval_$i\"
  weigh=${weigh}\"$casename\\/$casename.weigh_$i\"
done

replace w2k.orig w2k.jdl 0 
replace lapwfermi.orig lapwfermi.jdl 0 
replace sumup.orig sumup.jdl $count 
