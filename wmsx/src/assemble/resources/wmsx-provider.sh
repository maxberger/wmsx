#!/bin/bash

DIR=$(dirname $0)

$DIR/bin/wmsx-provider $* & disown

