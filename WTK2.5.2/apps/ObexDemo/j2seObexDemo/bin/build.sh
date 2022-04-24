#!/bin/sh
#
# @(#)build.sh	1.3 02/03/04
#
PRG=$0


DEMO_BIN=`dirname "$PRG"`
KVEM_HOME=`cd "${DEMO_BIN}/../../../.." ; pwd`
KVEM_LIB="${KVEM_HOME}/wtklib"

DEMO_SRC=`cd "${DEMO_BIN}/../src" ; pwd`
DEMO_RES1=`cd "${DEMO_BIN}/../res" ; pwd`
DEMO_RES2=`cd "${DEMO_BIN}/../../res" ; pwd`
DEMO_JAR="${DEMO_BIN}/demo.jar"
DEMO_CLASSES="${DEMO_BIN}/../classes"

JAVAC=javac
JAR=jar

if [ -n "${JAVA_HOME}" ] ; then
  JAVAC=${JAVA_HOME}/bin/javac
  JAR=${JAVA_HOME}/bin/jar
fi

echo "Creating directories..."
mkdir -p ${DEMO_CLASSES}

CLASSPATH=${KVEM_LIB}/kvem.jar
CLASSPATH=${CLASSPATH}:${KVEM_LIB}/kenv.zip
CLASSPATH=${CLASSPATH}:${KVEM_LIB}/gcf-op.jar
CLASSPATH=${CLASSPATH}:${DEMO_CLASSES}

echo "Compiling source files..."
${JAVAC} \
    -d ${DEMO_CLASSES}\
    -classpath ${CLASSPATH}\
    `find ${DEMO_SRC} -name '*'.java`
          
echo "Jaring class files..."
${JAR} cvf ${DEMO_JAR} -C ${DEMO_CLASSES} .

echo "Jaring resurcess files..."
${JAR} uvf ${DEMO_JAR} -C ${DEMO_RES1} .
${JAR} uvf ${DEMO_JAR} -C ${DEMO_RES2} ./images
