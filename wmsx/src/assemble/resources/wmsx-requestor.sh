#!/bin/sh

DIR=$(dirname $0)

stty -echo
$DIR/bin/wmsx-requestor $*
stty echo
