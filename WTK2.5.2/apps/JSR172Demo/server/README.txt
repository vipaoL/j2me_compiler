This is simple service implementation which can be used with JSR172 aware
J2EE client.

BUILD AND RUN INSTRUCTIONS:

You need JWSDP-1.2 (Java Web Services Development Pack) which can be found here:
 
 http://developer.java.sun.com/developer/technicalArticles/WebServices/wsj2ee/
 
Set environment variables:

  JAVA_HOME=<Java installation path>
  TOMCAT_HOME=<JWSDP-1.2 installation path>

You can build and start the service with following commands:

  build.sh or build.bat
  deploy.sh or deploy.bat

You can check the service is up and running with browser, open url:
  
  http://localhost:8080/serverscript/serverscript

You should see here some information about the service and 
link to wsdl file (this wsdl file corresponds to this localhost url). 
If you want to test a client from a local url, you need to regenerate
the connector classes in JSR172Demo client using this wsdl file.
