#!/bin/sh
mvn clean &&\
mvn package install assembly:assembly &&\
scp wmsx/target/*distribution* ochsner.dps.uibk.ac.at:/home/trac/www/download/ &&\
scp target/*src*zip ochsner.dps.uibk.ac.at:/home/trac/www/download/
