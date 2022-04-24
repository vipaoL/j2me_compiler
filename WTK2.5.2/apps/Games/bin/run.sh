#!/bin/sh
#
# This file runs the corresponded demo.
DEMO=games

`dirname $0`/../../../bin/emulator -Xdescriptor:`dirname $0`/${DEMO}.jad
