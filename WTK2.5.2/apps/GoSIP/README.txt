--------------------------------------------------------------------------------
               SIP API for J2ME (JSR 180 API) Demonstration
--------------------------------------------------------------------------------

1. Introduction

	GoSIP demo uses JSR180 SIP API. It demonstrates usage of SipServerConnectiom,
	SipClientConnection, SipConnectionNotifier and call flow between terminals
	and proxy & registrar server. 


2. Project Structure

	The structure of the project is following:
	./bin			jad and manifest
	./src			source files of the demo


3. Usage

	- Run the siptool.bat. This is proxy & registrar emulator.
	- Start the proxy & registrar.
	- You need to launch 2 emulators. Run the project twice.
	- 2 MIDlets appear in suite Sippy_A and Sippy_B. One is caller and the other is callee.
	- On one emulator run Sippy_A on another Sippy_B
	- After MIDlet starts enter the name of host computer where proxy & registrar is running
	- Follow instructions in MIDlet
	  1. register both terminals to registrar
	  2. invite Sippy_A for call
	  3. answer invite request in Sippy_B
	  4. terminals exchange connection informations and further communication runs on sockets
	  5. send messages from terminal to terminal
	  6. finish session with bye command


4. Required APIs
    
    JSR 139 - Connected Limited Device Configuration (CLDC) 1.1
    JSR 118 - Mobile Information Device Profile (MIDP) 2.0
    JSR 180 - SIP API for J2ME
