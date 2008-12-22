#!/bin/sh
mvn clean &&\
mvn -o package install assembly:assembly &&\
scp wmsx/target/*distribution* ochsner.dps.uibk.ac.at:/home/wmsx/www/download/ &&\
scp target/*src*zip ochsner.dps.uibk.ac.at:/home/wmsx/www/download/
