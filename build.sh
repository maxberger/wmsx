#!/bin/sh
. ~/bin/jdk14
mvn install package && unzip -o wmsx/target/wmsx-*-SNAPSHOT*zip -d /home/berger/afs/public/  
