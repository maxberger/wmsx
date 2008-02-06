#!/bin/bash
#wget http://dps.uibk.ac.at/~berger/app/app.tar.gz 
#lcg-cp  lfn:/grid/app.tar.gz file:$PWD/app.tar.gz
cp /home/max_ber/tmp/app.tar.gz .
lcg-cp --vo compchem guid:ebd07921-439e-45fa-b8b1-f61f117a6ada file:$PWD/app.tar.gz
tar -xzf app.tar.gz
export WIENROOT=$PWD/WIEN2K
export PATH=$WIENROOT:$PATH
rm -r $MAIN/app.tar.gz
cd $SCRATCH






