#!/bin/sh
tar -xzf worker.tar.gz
rm worker.tar.gz
sh worker/worker/bin/worker
rm -rf worker
