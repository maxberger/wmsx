#!/bin/sh
mvn install package
unzip -o wmsx/target/wmsx-*-SNAPSHOT*zip -d /home/berger/afs/public/  
