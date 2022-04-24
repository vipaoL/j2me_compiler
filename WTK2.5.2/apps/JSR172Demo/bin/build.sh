#!/bin/sh -e
#
# This batch file builds and preverifies the code for the demos.
# it then packages them in a JAR file appropriately.
#
DEMO=JSR172Demo
LIB_DIR=../../../lib
CLDCAPI=${LIB_DIR}/cldcapi10.jar
MIDPAPI=${LIB_DIR}/midpapi20.jar
J2MEWSAPI=${LIB_DIR}/j2me-ws.jar
J2ME_XMLRPC_API=${LIB_DIR}/j2me-xmlrpc.jar
PREVERIFY=../../../bin/preverify

PATHSEP=":"

JAVAC=javac
JAR=jar

if [ -n "${JAVA_HOME}" ] ; then
  JAVAC=${JAVA_HOME}/bin/javac
  JAR=${JAVA_HOME}/bin/jar
fi

#
# Make possible to run this script from any directory'`
#
cd `dirname $0`

echo "Creating directories..."
mkdir -p ../tmpclasses
mkdir -p ../classes

echo "Compiling source files..."

${JAVAC} \
    -bootclasspath ${CLDCAPI}${PATHSEP}${MIDPAPI}${PATHSEP}${J2MEWSAPI}${PATHSEP}${J2ME_XMLRPC_API} \
    -source 1.3 \
    -target 1.3 \
    -d ../tmpclasses \
    -classpath ../tmpclasses \
    `find ../src -name '*'.java`

echo "Preverifying class files..."

${PREVERIFY} \
    -classpath ${CLDCAPI}${PATHSEP}${MIDPAPI}${PATHSEP}${J2MEWSAPI}${PATHSEP}${J2ME_XMLRPC_API}${PATHSEP}../tmpclasses \
    -d ../classes \
    ../tmpclasses

echo "Jaring preverified class files..."
${JAR} cmf MANIFEST.MF ${DEMO}.jar -C ../classes .

if [ -d ../res ] ; then
  ${JAR} uf ${DEMO}.jar -C ../res .
fi

echo
echo "Don't forget to update the JAR file size in the JAD file!!!"
echo
