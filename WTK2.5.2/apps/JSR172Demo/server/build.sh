#!/bin/sh -x
#
# * Copyright (c) 2007, Sun Microsystems, Inc.
# *
# * All rights reserved.
# *
# * Redistribution and use in source and binary forms, with or without
# * modification, are permitted provided that the following conditions
# * are met:
# *
# *  * Redistributions of source code must retain the above copyright
# *    notice, this list of conditions and the following disclaimer.
# *  * Redistributions in binary form must reproduce the above copyright
# *    notice, this list of conditions and the following disclaimer in the
# *    documentation and/or other materials provided with the distribution.
# *  * Neither the name of Sun Microsystems nor the names of its contributors
# *    may be used to endorse or promote products derived from this software
# *    without specific prior written permission.
# *
# * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
# * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
# * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
# * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
# * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
# * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
# * PROCU#ENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
# * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
# * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
# * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
# * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

# You should have the following variables set:
#
#JAVA_HOME=<path to J2SDK installation root>
#TOMCAT_HOME=<path to jwsdp-1.2 installation root>

PATH=$TOMCAT_HOME/bin:$TOMCAT_HOME/jaxrpc/bin:$JAVA_HOME/bin:$PATH
export PATH TOMCAT_HOME JAVA_HOME

if [ "$TOMCAT_HOME" = "" ]
then
  echo TOMCAT_HOME variable is not set
  echo TOMCAT_HOME should point to JWSDP-1.2 installation
  echo You can download it from:
  echo http://java.sun.com/webservices/downloads/webservicespack.html
  exit
fi

if [ ! -x $TOMCAT_HOME/jaxrpc/bin/wscompile.sh ]
then
  echo TOMCAT_HOME should point to JWSDP-1.2 installation
  echo You can download it from:
  echo http://java.sun.com/webservices/downloads/webservicespack.html
  exit
fi

if [ "$JAVA_HOME" = "" ]
then
  echo JAVA_HOME variable is not set
  echo JAVA_HOME should point to J2SDK installation
  exit
fi

if [ ! -x $JAVA_HOME/bin/javac ]
then
  echo JAVA_HOME should point to J2SDK installation
  exit
fi
mkdir -p WEB-INF/classes
echo running javac
javac -d WEB-INF/classes src/serverscript/* -classpath \
$TOMCAT_HOME/jaxrpc/lib/jaxrpc-api.jar:$TOMCAT_HOME/common/lib/servlet-api.jar || exit

mkdir tmp_src
echo running wscompile.sh
wscompile.sh -gen:server -d WEB-INF/classes -keep -model WEB-INF/model.gz \
        -s tmp_src/ -f:wsi,documentliteral -classpath WEB-INF/classes \
	src/config.xml || exit

cp src/web.xml src/jaxrpc-ri.xml WEB-INF/
echo running jar
jar -cf serverscript.jar WEB-INF/
echo running wsdeploy.sh
wsdeploy.sh serverscript.jar -o serverscript.war




