#!/bin/sh
mvn install:install-file -Dfile=../src/main/resources/tinylaf.jar -DartifactId=tinylaf -DgroupId=wmsx-gui -Dversion=1.3.8 -Dpackaging=jar -DgeneratePom=true

