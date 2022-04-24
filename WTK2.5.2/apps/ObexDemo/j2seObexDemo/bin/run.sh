#!/bin/sh
#
# @(#)run.sh	1.3 02/03/04
#

#
# This script is derived from 'emulator' one.
# One may track the changes in 'emulator' script
# and apply them to this one.
#
javapathtowtk=

PRG=$0

# Resolve soft links
while [ -h "$PRG" ]; do
    ls=`/bin/ls -ld "$PRG"`
    link=`/usr/bin/expr "$ls" : '.*-> \(.*\)$'`
    if /usr/bin/expr "$link" : '^/' > /dev/null 2>&1; then
        PRG="$link"
    else
        PRG="`/usr/bin/dirname $PRG`/$link"
    fi
done

DEMO_BIN=`dirname "$PRG"`
DEMO_LIB="${DEMO_BIN}/demo.jar"

KVEM_HOME=`cd "${DEMO_BIN}/../../../.." ; pwd`
KVEM_LIB="${KVEM_HOME}/wtklib"

CLASSPATH=${KVEM_LIB}/kvem.jar
CLASSPATH=${CLASSPATH}:${KVEM_LIB}/kenv.zip
CLASSPATH=${CLASSPATH}:${KVEM_LIB}/gcf-op.jar
CLASSPATH=${CLASSPATH}:${DEMO_LIB}

"${javapathtowtk}java" -Dkvem.home="${KVEM_HOME}" \
    -Djava.library.path="${KVEM_HOME}/bin" \
    -cp ${CLASSPATH} ObexDemoMain
