--------------------------------------------------------------------------------
                  Payment API (JSR 229 API) Demonstration
--------------------------------------------------------------------------------

1. Introduction
	
	JBricks is a brick game (Arkanoid) using JSR229 Payment API for buying 
	additional lives and levels.


2. Project Structure

	./bin/jbricks.jad	JAD file
	./bin/MANIFEST.MF	MANIFEST file
	./bin/jbricks.jpp	Payment Update file
	./src			Sources


3. Usage

	It can be paid for additional lives and levels either by Premium Priced SMS 
	(PPSMS) or by Credit Card. 
	It is necessary to have set Mobile Country Code (MCC) and Mobile Network 
	Code (MNC) to be PPSMS Adapter accessible. These parameters can be set in 
	WTK, Edit -> Preferences -> Payment -> Operator. Demo is configured for
	MCC = 928 and MNC = 99. If just emulator is run, it has to be started 
	with parameters: payment.mcc=928 payment.mnc=99.
	It is necessary to have started WTK for paying by Credit Card, because Credit
	Card Adapter uses HTTPS server integrated in WTK.


4. Required APIs
    
    JSR 139 - Connected Limited Device Configuration (CLDC) 1.1
    JSR 118 - Mobile Information Device Profile (MIDP) 2.0
    JSR 205 - Wireless Messaging API (WMA) 2.0
    JSR 229 - Payment API
