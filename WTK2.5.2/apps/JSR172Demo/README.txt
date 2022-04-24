--------------------------------------------------------------------------------
              J2ME Web Services (JSR 172 API) Demonstration
--------------------------------------------------------------------------------

1. Introduction

    JSR172Demo shows how to access a web service from a MIDlet. The web service 
    is already running on an Internet server. If you are behind a firewall, 
    you must configure the emulator's proxy server settings. Choose Edit > Preferences 
    from the KToolbar menu, then select Network Configuration. Fill in the proxy 
    server address file and the port number.

2. Usage

    - Open this project.
    - Run stubgenerator (Project -> Stub Generator).
    - Enter path to serverscript.wsdl (in src directory)
    - Enter package name: example.serverscript.connector
    - Build and run the example.
    - You can browse through simulated news headlines, all of which are retrieved 
      from the web service.
    - To see what is going on behind the scenes, use the network monitor.

3. Required APIs
    
    JSR 139 - Connected Limited Device Configuration (CLDC) 1.1
    JSR 118 - Mobile Information Device Profile (MIDP) 2.0
    JSR 172 - J2ME Web Services Specification


Note:
------
    You may use a stub generator before the build to regenerate the connection
    package sources.
