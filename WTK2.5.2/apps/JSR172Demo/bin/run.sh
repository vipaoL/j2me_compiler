#!/bin/sh
#
# This file runs the corresponded demo.
DEMO=JSR172Demo

PROXY_HOST=
PROXY_PORT=

if [ ! -n "${PROXY_HOST}" ] ; then
  echo "Error: you need to specify a proxy for this demo"
  exit 1;
fi

if [ ! -n "${PROXY_PORT}" ] ; then
  echo "Error: you need to specify a proxy for this demo"
  exit 1;
fi

`dirname $0`/../../../bin/emulator \
    -Dcom.sun.midp.io.http.proxy=${PROXY_HOST}:${PROXY_PORT} \
    -Xdescriptor:`dirname $0`/${DEMO}.jad
