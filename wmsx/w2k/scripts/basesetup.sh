#!/bin/bash
#wget http://dps.uibk.ac.at/~berger/app/app.tar.gz 
#lcg-cp  lfn:/grid/app.tar.gz file:$PWD/app.tar.gz
cp /home/berger/Documents/glite/app.tar.gz .
tar -xzf app.tar.gz
export WIENROOT=$PWD/WIEN2K
export PATH=$WIENROOT:$PATH
rm -r $MAIN/app.tar.gz
cd $SCRATCH






