#!/bin/sh

mvn org.jini.maven-jini-plugin:maven-jini-plugin:httpd& 
sleep 3
mvn org.jini.maven-jini-plugin:maven-jini-plugin:httpd -DcodebasePort=8123 -DcodebaseDir=/home/berger/workspace/wmsx/wmsx-provider/target/ &
sleep 3
mvn org.jini.maven-jini-plugin:maven-jini-plugin:reggie& 
