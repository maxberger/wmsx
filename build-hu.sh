#!/bin/sh
. ~/bin/jdk14
mvn -o install package && unzip -o wmsx/target/wmsx-*-SNAPSHOT*zip -d /home/berger/afs/public/  
