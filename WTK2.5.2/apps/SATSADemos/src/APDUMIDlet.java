/*
 *
 * Copyright (c) 2007, Sun Microsystems, Inc.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of Sun Microsystems nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
import java.io.*;

import javax.microedition.apdu.APDUConnection;
import javax.microedition.io.Connector;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;


public class APDUMIDlet extends MIDlet implements CommandListener, Runnable {
    private final String kPurseSlot0 = "apdu:0;target=a0.00.00.00.62.03.01.0c.02.01";
    private final String kPurseSlot1 = "apdu:1;target=a0.00.00.00.62.03.01.0c.02.01";
    private final String kWalletSlot0 = "apdu:0;target=a0.00.00.00.62.03.01.0c.06.01";
    private APDUConnection mPurseConnection0;
    private APDUConnection mPurseConnection1;
    private APDUConnection mWalletConnection;
    private Display mDisplay;
    private Form mMainForm;
    private Command mExitCommand;
    private Command mGoCommand;
    private Command mBackCommand;
    private Form mProgressForm;
    private final byte[][] kPurseAPDUs =
        {
            // Verify PIN (User PIN 01020304)
            {
                (byte)0x00, (byte)0x20, (byte)0x00, (byte)0x82, (byte)0x04, (byte)0x01, (byte)0x02,
                (byte)0x03, (byte)0x04, (byte)0x00
            },
            // should return 90 00

            // Initialize Transaction: Credit $250.00 
            {
                (byte)0x80, (byte)0x20, (byte)0x01, (byte)0x00, (byte)0x0a, (byte)0x61, (byte)0xa8,
                (byte)0x22, (byte)0x44, (byte)0x66, (byte)0x88, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x7F
            },
            // 05 05 05 05 0c 1f 62 00 00 00 07 00 00 00 00 00 00 00 00 90 00 
            // = Purse ID : 0x05050505; ExpDate 12/31/98; TN=7

            // Complete Transaction: Date 10/27/97; Time 15:33
            {
                (byte)0x80, (byte)0x22, (byte)0x00, (byte)0x00, (byte)0x0d, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0a,
                (byte)0x1b, (byte)0x61, (byte)0x0f, (byte)0x21, (byte)0x7F
            },
            // 61 a8 00 00 00 00 00 00 00 00 90 00	= Purse Balance $250.00;

            // Initialize Transaction: Debit $25.00;
            {
                (byte)0x80, (byte)0x20, (byte)0x02, (byte)0x00, (byte)0x0a, (byte)0x09, (byte)0xc4,
                (byte)0x22, (byte)0x44, (byte)0x66, (byte)0x88, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x7F
            },
            // 05 05 05 05 0c 1f 62 61 a8 00 08 00 00 00 00 00 00 00 00 90 00;
            // = Purse ID : 0x05050505; ExpDate 12/31/98; TN=8

            // Complete Transaction: Date 10/27/97; Time 15:35
            {
                (byte)0x80, (byte)0x22, (byte)0x00, (byte)0x00, (byte)0x0d, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0a,
                (byte)0x1b, (byte)0x61, (byte)0x0f, (byte)0x23, (byte)0x7F
            }
        // 57 e4 00 00 00 00 00 00 00 00 90 00	= Purse Balance $225.00;
        };
    public final byte[][] kWalletAPDUs =
        {
            // verify PIN expects 90 00
            {
                (byte)0x80, (byte)0x20, (byte)0x00, (byte)0x00, (byte)0x05, (byte)0x01, (byte)0x02,
                (byte)0x03, (byte)0x04, (byte)0x05, (byte)0x7F
            },
            
            // get wallet balance
            {(byte)0x80, (byte)0x50, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x02 },
            
            // credit 128 to the wallet
            {(byte)0x80, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x80, (byte)0x7F },
            
            // get wallet balance
            {(byte)0x80, (byte)0x50, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x02 }
        };

    public APDUMIDlet() {
        mExitCommand = new Command("Exit", Command.EXIT, 0);
        mGoCommand = new Command("Go", Command.SCREEN, 0);
        mBackCommand = new Command("Back", Command.BACK, 0);

        mMainForm = new Form("APDU Example");
        mMainForm.append("Press Go to use the SATSA-APDU API " +
            "to connect to card applications.");
        mMainForm.addCommand(mExitCommand);
        mMainForm.addCommand(mGoCommand);
        mMainForm.setCommandListener(this);
    }

    public void startApp() {
        mDisplay = Display.getDisplay(this);

        mDisplay.setCurrent(mMainForm);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    public void commandAction(Command c, Displayable s) {
        if (c == mExitCommand) {
            notifyDestroyed();
        } else if (c == mGoCommand) {
            mProgressForm = new Form("Working...");
            mDisplay.setCurrent(mProgressForm);

            Thread t = new Thread(this);
            t.start();
        } else if (c == mBackCommand) {
            mDisplay.setCurrent(mMainForm);
        }
    }

    public void run() {
        try {
            setProgress("Opening purse slot 0");
            mPurseConnection0 = (APDUConnection)Connector.open(kPurseSlot0);
            setProgress("Opening purse slot 1");
            mPurseConnection1 = (APDUConnection)Connector.open(kPurseSlot1);
            setProgress("Opening wallet slot 0");
            mWalletConnection = (APDUConnection)Connector.open(kWalletSlot0);

            setProgress("Exchanging APDUs with purse slot 0");

            for (int i = 0; i < kPurseAPDUs.length; i++) {
                byte[] apdu = kPurseAPDUs[i];
                byte[] response = mPurseConnection0.exchangeAPDU(apdu);

                // Process response.
            }

            setProgress("Closing purse slot 0");
            mPurseConnection0.close();

            setProgress("Exchanging APDUs with wallet");

            for (int i = 0; i < kWalletAPDUs.length; i++) {
                byte[] apdu = kWalletAPDUs[i];
                byte[] response = mWalletConnection.exchangeAPDU(apdu);

                // Process response.
            }

            setProgress("Closing wallet");
            mWalletConnection.close();

            setProgress("Exchanging APDUs with purse slot 1");

            for (int i = 0; i < kPurseAPDUs.length; i++) {
                byte[] apdu = kPurseAPDUs[i];
                byte[] response = mPurseConnection1.exchangeAPDU(apdu);

                // Process response.
            }

            setProgress("Closing purse slot 1");
            mPurseConnection1.close();

            mProgressForm.setTitle("Working...done.");
            mProgressForm.addCommand(mBackCommand);
            mProgressForm.setCommandListener(this);
        } catch (Exception e) {
            try {
                mPurseConnection0.close();
            } catch (Throwable t) {
            }

            try {
                mPurseConnection1.close();
            } catch (Throwable t) {
            }

            try {
                mWalletConnection.close();
            } catch (Throwable t) {
            }

            Form f = new Form("Exception");
            f.append(e.toString());
            f.addCommand(mBackCommand);
            f.setCommandListener(this);
            mDisplay.setCurrent(f);
        }
    }

    private void setProgress(String s) {
        StringItem si = new StringItem(null, s);
        si.setLayout(Item.LAYOUT_2 | Item.LAYOUT_NEWLINE_AFTER);
        mProgressForm.append(si);
    }
}
