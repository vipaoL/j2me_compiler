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


public class SATMIDlet extends MIDlet implements CommandListener, Runnable {
    private APDUConnection mSATConnection1;
    private APDUConnection mSATConnection2;
    private Display mDisplay;
    private Form mMainForm;
    private Command mExitCommand;
    private Command mGoCommand;
    private Command mBackCommand;
    private Form mProgressForm;

    // Update the binary file EFpuct (Price per unit and currency table)
    private final byte[] kEnvelope =
        {
            (byte)0xA0, (byte)0xC2, (byte)0x00, (byte)0x00, (byte)0x2f, (byte)0xD1, (byte)0x2d,
            (byte)0x82, (byte)0x02, (byte)0x83, (byte)0x81, (byte)0x06, (byte)0x05, (byte)0x80,
            (byte)0x11, (byte)0x22, (byte)0x33, (byte)0x44, (byte)0x8B, (byte)0x20, (byte)0x10,
            (byte)0x02, (byte)0x81, (byte)0x55, (byte)0x7F, (byte)0xF6, (byte)0x00, (byte)0x11,
            (byte)0x29, (byte)0x12, (byte)0x00, (byte)0x00, (byte)0x04, (byte)0x14, (byte)0x10,
            (byte)0x11, (byte)0x12, (byte)0x13, (byte)0x14, (byte)0x15, (byte)0x16, (byte)0x17,
            (byte)0x18, (byte)0x19, (byte)0x1a, (byte)0x1b, (byte)0x1c, (byte)0x1d, (byte)0x41,
            (byte)0xAB, (byte)0xCD, (byte)0xEF
        };

    public SATMIDlet() {
        mExitCommand = new Command("Exit", Command.EXIT, 0);
        mGoCommand = new Command("Go", Command.SCREEN, 0);
        mBackCommand = new Command("Back", Command.BACK, 0);

        mMainForm = new Form("SAT Example");
        mMainForm.append("Press Go to use the SATSA-APDU API " +
            "to connect to a SAT application.");
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
            String url = "apdu:0;target=SAT";

            setProgress("Opening first SAT connection");
            mSATConnection1 = (APDUConnection)Connector.open(url);
            // The second connection demonstrates that two SAT
            // connections can be open simultaneously.
            setProgress("Opening second SAT connection");
            mSATConnection2 = (APDUConnection)Connector.open(url);

            setProgress("Sending envelopes");

            byte[] response1 = mSATConnection1.exchangeAPDU(kEnvelope);
            byte[] response2 = mSATConnection2.exchangeAPDU(kEnvelope);

            setProgress("Closing first SAT connection");
            mSATConnection1.close();

            setProgress("Closing second SAT connection");
            mSATConnection2.close();

            mProgressForm.setTitle("Working...done.");
            mProgressForm.addCommand(mBackCommand);
            mProgressForm.setCommandListener(this);
        } catch (Exception e) {
            try {
                mSATConnection1.close();
            } catch (Throwable t) {
            }

            try {
                mSATConnection2.close();
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
