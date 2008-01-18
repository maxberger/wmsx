#!/bin/sh
mvn clean
mvn package install assembly:assembly
cp wmsx/target/*distribution* $HOME/public_html/wmsx/
cp target/*src*zip $HOME/public_html/wmsx/
