#!/bin/sh

aclocal && \
libtoolize --force --copy && \
autoheader && \
automake --gnu --add-missing --copy && \
autoconf
