--------------------------------------------------------------------------------
        Security and Trust Services API for J2ME (JSR 177) Demonstration
--------------------------------------------------------------------------------

1. Itroduction 

    SATSADemos includes demonstrations of SATSA, the Security and Trust Services APIs. 
    Most of the demonstrations show how to communicate with a smart card. The emulator 
    can communicate with a simulated smart card using a socket protocol. The smart 
    card simulator, cref, is included with the toolkit
    
2. Usage

    The following sections contain instructions for each menu choice for this demo. 
    For each demo, be sure to do the following before launching the emulator:
    - Run the instance(s) of cref from the command line.
    - Be sure to set the set the security domain to maximum. 
    
    2.1 APDUMIDlet
        This MIDlet demonstrates communication with a smart card using Application 
        Protocol Data Units (APDUs), small packets of data. APDUMIDlet expects to 
        find two simulated smart cards. You can run the smart card simulator using cref, 
        which is part of the Java Card Development Kit.
        The Mohair application includes pre-built memory images that you can use with cref. 
        The memory images contain Java Card applications with which Mohair interacts. 
        The memory images are in the root directory of the Mohair project.

        On Windows, start up two instances of cref like this, one for each simulated card slot 
        (assuming the current directory is the toolkit root directory):

            start bin\cref -p 9025 -i apps\SATSADemos\demo2.eeprom
            start bin\cref -p 9026 -i apps\SATSADemos\demo2.eeprom

        On Linux you can use:

            toolkit/bin/cref -p 9025 -i apps/SATSADemos/demo2.eeprom
            toolkit/bin/cref -p 9026 -i apps/SATSADemos/demo2.eeprom

        Note that the port numbers (9025 and 9026 in this example) must match the port 
        numbers you specified in the SATSA preferences. Also, make sure you use 
        the correct path to demo2.eeprom.
        Once you have the two smart card simulators running, you can run APDUMIDlet. 
        
    2.2 SATMIDlet
        SATMIDlet demonstrates smart card communication with a slight variation 
        on APDU communication.
        To set up the simulated smart card, use cref, very much like you did for 
        APDUMIDlet. This time you don't have to specify a port number, and the memory 
        image is different:

        Windows:
            start bin\cref -i apps\SATSADemos\sat.eeprom

        Linux:
            toolkit/bin/cref -i apps/SATSADemos/sat.eeprom

        When the smart card simulator is running, you can run SATMIDlet 
        to communicate with card applications. 
        
    2.3 CryptoMIDlet
        CryptoMIDlet demonstrates the general cryptographic features of SATSA. 
        It does not interact with a smart card in any way. 
        
    2.4 MohairMIDlet
        MohairMIDlet has two functions. The first, Find slots, displays all the available 
        card slots. Each slot has a number followed by `C' or `H' indicating whether the slot 
        is cold-swappable or hot-swappable. After viewing the slots select Back 
        to return to the first screen.
        The second part of MohairMIDlet, SATSA-PKI Sign test, uses a smart card to generate 
        a digital signature. As with the earlier demonstrations, you need to run cref 
        with the right memory image to prepare for the connection from MohairMIDlet. 
        Type the following in the installation directory:

        Windows:
            start bin\cref -i apps\SATSADemos\sat.eeprom

        Linux:
            toolkit/bin/cref -i apps/SATSADemos/sat.eeprom

        In the emulator, highlight SATSA-PKI Sign test and choose SELECT. 
        The following confirmation message appears:
        - This certificate will be used: MohairAuth
        - Select the OK soft key.
        - For PIN 1, type: 1234
        - Select the OK soft key. The following confirmation message appears:
        - This string will be signed: JSR 177 Approved
        - Select the OK soft key. The following confirmation message appears:
        - This certificate will be used: MohairAuth
        - Select the OK soft key.
        - For non repudiation key 1 PIN, type: 2345 
        

3. Required APIs
    
    JSR 139 - Connected Limited Device Configuration (CLDC) 1.1
    JSR 118 - Mobile Information Device Profile (MIDP) 2.0
    JSR 177 - Security and Trust Services API for J2ME
