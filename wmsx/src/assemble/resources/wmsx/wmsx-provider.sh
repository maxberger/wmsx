#!/bin/bash

DIR=$(dirname $0)/../libexec/wmsx

$DIR/bin/wmsx-provider $* & disown

