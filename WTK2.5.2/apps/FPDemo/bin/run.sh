#!/bin/sh
#
# This file runs the corresponded demo.
DEMO=FPDemo

`dirname $0`/../../../bin/emulator -Xdescriptor:`dirname $0`/${DEMO}.jad
