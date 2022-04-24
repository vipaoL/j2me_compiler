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

import javax.microedition.io.Connector;
import javax.microedition.jcrmi.JavaCardRMIConnection;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

import com.sun.javacard.samples.RMIDemo.Purse;


public class JCRMIMIDlet extends MIDlet implements CommandListener, Runnable {
    private final String kRMIURL = "jcrmi:0;AID=a0.0.0.0.62.3.1.c.8.1";
    private JavaCardRMIConnection mConnection;
    private Display mDisplay;
    private Form mMainForm;
    private Command mExitCommand;
    private Command mGoCommand;
    private Command mBackCommand;
    private Form mProgressForm;

    public JCRMIMIDlet() {
        mExitCommand = new Command("Exit", Command.EXIT, 0);
        mGoCommand = new Command("Go", Command.SCREEN, 0);
        mBackCommand = new Command("Back", Command.BACK, 0);

        mMainForm = new Form("JCRMI Example");
        mMainForm.append("Press Go to use the SATSA-JCRMI API " +
            "to connect to a card application.");
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
            setProgress("Opening a connection");
            mConnection = (JavaCardRMIConnection)Connector.open(kRMIURL);

            setProgress("Getting an initial reference");

            Purse purse = (Purse)mConnection.getInitialReference();

            short balance = purse.getBalance();
            setProgress("Balance = " + balance);

            setProgress("Crediting 20");
            purse.credit((short)20);
            setProgress("Debiting 15");
            purse.debit((short)15);

            balance = purse.getBalance();
            setProgress("Balance = " + balance);

            setProgress("Setting account number to 54321");
            purse.setAccountNumber(new byte[] { 5, 4, 3, 2, 1 });

            setProgress("Closing connection");
            mConnection.close();

            mProgressForm.setTitle("Working...done.");
            mProgressForm.addCommand(mBackCommand);
            mProgressForm.setCommandListener(this);
        } catch (Exception e) {
            try {
                mConnection.close();
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
