--------------------------------------------------------------------------------
        Security and Trust Services API for J2ME (JSR 177) Demonstration
--------------------------------------------------------------------------------

1. Introduction

    This application contains a single MIDlet, JCRMIMIDlet, which shows how to 
    communicate with a card application using Java Card RMI, a card-friendly remote 
    object protocol

    
2. Usage

    You need to start up cref with an appropriate memory image:

    Windows:
        start bin\cref -p 9025 -i apps\SATSADemos\demo2.eeprom

    Linux:
        toolkit/bin/cref -i apps/SATSADemos/demo2.eeprom

    Now run JCRMIMIDlet to see how your application can communicate with a distributed 
    object on the card. 


3. Required APIs
    
    JSR 139 - Connected Limited Device Configuration (CLDC) 1.1
    JSR 118 - Mobile Information Device Profile (MIDP) 2.0
    JSR 177 - Security and Trust Services API for J2ME
