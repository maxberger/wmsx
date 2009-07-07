#!/bin/sh

DIR=$(dirname $0)/../libexec/wmsx

stty -echo
$DIR/bin/wmsx-requestor $*
stty echo
