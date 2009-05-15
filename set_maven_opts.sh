#!/bin/sh
# Increase Java Heap for Findbugs plugin
# Command: source set_maven_opts.sh
export MAVEN_OPTS="-Xmx1024m -Xms128m -XX:MaxPermSize=512m"
