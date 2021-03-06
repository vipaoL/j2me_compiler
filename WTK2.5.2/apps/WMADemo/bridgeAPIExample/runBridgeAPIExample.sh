#!/bin/sh

javapathtowtk=/home/runner/work/j2me_compiler/jdk1.6.0_45/bin/

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

KVEM_BRIDGE=`dirname $PRG`
KVEM_HOME=`cd ${KVEM_BRIDGE}/../../.. ; pwd`
KVEM_LIB="${KVEM_HOME}/wtklib"

"${javapathtowtk}java" -Dkvem.home="${KVEM_HOME}" -classpath ".:${KVEM_LIB}/kvem.jar:${KVEM_LIB}/kenv.zip" WMABridgeAPIExample
