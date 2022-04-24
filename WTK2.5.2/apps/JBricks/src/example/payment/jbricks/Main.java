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
package example.payment.jbricks;

import java.util.Date;
import java.io.*; 

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.payment.TransactionListener;
import javax.microedition.payment.TransactionListenerException;
import javax.microedition.payment.TransactionModule;
import javax.microedition.payment.TransactionModuleException;
import javax.microedition.payment.TransactionRecord;


/** 
 * Main game wrapper 
 */
public class Main extends MIDlet implements TransactionListener, CommandListener, Runnable {
    /* Resource to test if the MIDlet is executed in the OTA mode. */
    private static final String JAR_RESOURCE = "/META-INF/MANIFEST.MF"; 
    
    // definition of features to pay for
    protected static final int FEATURE_1_LIFE = 0;
    protected static final int FEATURE_3_LIVES = 1;
    protected static final int FEATURE_1_LEVEL = 2;
    protected static final int FEATURE_3_LEVELS = 3;
    
    // transaction status
    protected static final int TRAN_SUCCESSFUL = 0x08;
    protected static final int TRAN_REJECTED = 0x10;
    protected static final int TRAN_FAILED = 0x20;

    private Command exit = new Command("Exit", Command.EXIT, 0);
    private Command continueAlert = new Command("Continue", Command.OK, 0);
    private Command exitAlert = new Command("Exit", Command.EXIT, 0);
    private Screen screen;
    private Engine engine;
    private TransactionModule txModule;
    private int feature = 0;
    private boolean enableTranListenerNull = false;
    private String title = "";
    private String description = "";
    private int lastTransState;

    /**
     * Creates game wrappper
     */
    public Main() {
        // test if the application is executed in the OTA mode
        InputStream is = getClass().getResourceAsStream(JAR_RESOURCE);

        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
            }
            init();
        } else {
            // application was not executed via OTA, shows warning
            String textAlert = "Please execute this application via OTA, otherwise" +
                    "\n* You will not see transactions in Payment Transactions monitor" +
                    "\n* Your payment transactions will not be stored after application exits";
            Alert alert = new Alert("JBricks", textAlert, null, AlertType.WARNING);
            alert.addCommand(exitAlert);
            alert.addCommand(continueAlert);
            alert.setCommandListener(this);
            Display.getDisplay(this).setCurrent(alert);
        } 
    }

    /**
     * Enables/disables "Exit" command in application
     *
     * @param show false for hiding "Exit" command, true otherwise 
     */
    public void showCommandExit(boolean show) {
        if (show) {
            screen.addCommand(exit);
        } else {
            screen.removeCommand(exit);
        }
    }

    /**
     * Signals the MIDlet that it has entered the Active state. In the Active 
     * state the MIDlet may hold resources. The method will only be called 
     * when the MIDlet is in the Paused state.
     */
    public void startApp() {
        if (engine != null) {
            engine.setPaused(false);
        }
    }

    /**
     * Signals the MIDlet to enter the Paused state. In the Paused state 
     * the MIDlet must release shared resources and become quiescent. 
     * This method will only be called called when the MIDlet is in the Active state.
     */
    public void pauseApp() {
        if (engine != null) {
            engine.setPaused(true);
        }
    }

    /**
     * Signals the MIDlet to terminate and enter the Destroyed state. 
     * In the destroyed state the MIDlet must release all resources and save 
     * any persistent state. This method may be called from the Paused or Active states.
     *
     * @param unconditional If true when this method is called, the MIDlet must 
     * cleanup and release all resources. If false the MIDlet may throw 
     * MIDletStateChangeException to indicate it does not want to be destroyed at this time.
     */
    public void destroyApp(boolean unconditional) {
        if (engine != null) {
            engine.stop();
        }
        Display.getDisplay(this).setCurrent(null);
        notifyDestroyed();
    }

    /**
     * Indicates that a command event has occurred on Displayable dis.
     *
     * @param cmd a Command object identifying the command. This is either 
     * one of the applications have been added to Displayable with 
     * addCommand(Command) or is the implicit SELECT_COMMAND of List.
     * @param dis - the Displayable on which this event has occurred
     */
    public void commandAction(Command cmd, Displayable dis) {
        if (cmd == exit) {
            engine.showMenu();
            showCommandExit(false);
        } else if (cmd == continueAlert) {
            init();
        } else if (cmd == exitAlert) {
            destroyApp(true);
        }
    }

    /**
     * Called by the payment module (as interfaced by the TransactionModule) 
     * to indicate that a transaction-related event has occurred. The parameter 
     * record holds (by its getState() method) one of the values defined in 
     * the TransactionRecord interface:
     *   · TransactionRecord.TRANSACTION_SUCCESSFUL,
     *   · TransactionRecord.TRANSACTION_FAILED,
     *   · TransactionRecord.TRANSACTION_REJECTED.
     * The record is identifiable through the transaction ID that has been 
     * returned by the process() method call.
     *
     * @param record - the original TransactionRecord containing the feature ID, 
     * the final state (TransactionRecord.TRANSACTION_* value) and timestamp. 
     * This record is created by the payment module.
     */
    public void processed(TransactionRecord record) {
        switch (record.getState()) {
            case TransactionRecord.TRANSACTION_SUCCESSFUL:
                lastTransState = record.getFeatureID();
                switch (lastTransState) {
                    case FEATURE_1_LIFE:
                        engine.increaseNumOfLives(1);
                        break;

                    case FEATURE_3_LIVES:
                        engine.increaseNumOfLives(3);
                        break;

                    case FEATURE_1_LEVEL:
                        engine.increaseNumOfLevels(1);
                        break;

                    case FEATURE_3_LEVELS:
                        engine.increaseNumOfLevels(3);
                        break;
                }

                break;
            
            case TransactionRecord.TRANSACTION_REJECTED:
                lastTransState = TRAN_REJECTED;
                break;
                
            case TransactionRecord.TRANSACTION_FAILED:
                lastTransState = TRAN_FAILED;
                break;
        }
        
        // inform the user about transaction status
        engine.setState(Engine.FEEDBACK);
        try { Thread.sleep(3000);} catch (Throwable t) {}
        engine.setState(Engine.MENU);
    }

    /**
     * Starts a new thread to conduct a payment
     */
    public void run() {
        try {
            txModule.setListener(this);
            txModule.process(feature, title, description);

            if (enableTranListenerNull) {
                txModule.setListener(null);
            }
        } catch (TransactionListenerException tle) {
            System.err.println("Transaction Listener is not set");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called to buy life
     *
     * @param count number of lives to be bought
     */
    protected void buyLife(int count) {
        title = "Buy Life";

        if (count == 1) {
            feature = FEATURE_1_LIFE;
            description = "You are able to increase number of " + "your lives by 1 life.";
        } else {
            feature = FEATURE_3_LIVES;
            description = "You are able to increase number of " + "your lives by 3 lives.";
        }

        if (txModule != null) {
            new Thread(this).start();
        }
    }

    /**
     * Called to buy level
     *
     * @param count number of levels to be bought
     */
    protected void buyLevel(int count) {
        title = "Buy Level";

        if (count == 1) {
            feature = FEATURE_1_LEVEL;
            description = "You are able to increases number of " +
                "levels you can access by 1 level.";
        } else {
            feature = FEATURE_3_LEVELS;
            description = "You are able to increases number of " +
                "levels you can access by 3 levels.";
        }

        if (txModule != null) {
            new Thread(this).start();
        }
    }

    /**
     * Enables / disables TransactionListener
     *
     * @param b true for enabled TransactionListener, false otherwise
     */
    protected void setTransactionListenerNull(boolean b) {
        enableTranListenerNull = b;
    }

    /**
     * Retrieves history of conducted payment transactions
     *
     * @return array of strings, one item per transaction
     */
    protected String[] getHistory() {
        TransactionRecord[] record = txModule.getPastTransactions(6);
        String[] stringRecord = null;

        if (record != null) {
            String feature = "";
            String when = "";
            Date date = new Date();
            stringRecord = new String[record.length];

            for (int i = 0; i < record.length; i++) {
                switch (record[i].getState()) {
                case TransactionRecord.TRANSACTION_FAILED:
                    // Technical problem - try again!
                    feature = "Failed ";

                    break;

                case TransactionRecord.TRANSACTION_REJECTED:
                    // Why? You have to pay to play!
                    feature = "Rejected ";

                    break;

                default:
                    feature = "";
                }

                switch (record[i].getFeatureID()) {
                case FEATURE_1_LIFE:
                    feature += "1 life";

                    break;

                case FEATURE_3_LIVES:
                    feature += "3 lives";

                    break;

                case FEATURE_1_LEVEL:
                    feature += "1 level";

                    break;

                case FEATURE_3_LEVELS:
                    feature += "3 levels";
                }

                date.setTime(record[i].getFinishedTimestamp());
                when = date.toString();
                stringRecord[i] = feature + " on " + when.substring(0, when.lastIndexOf(':'));
            }
        }

        return stringRecord;
    }
    
    /**
     * Returns information about last conducted payment transaction.
     */
    protected int getLastTransactionState() {
        return lastTransState;
    }

    /**
     * restores missed transaction due to application crash, etc.
     */
    private void restoreBoughtFeatures() {
        TransactionRecord[] record = txModule.getPastTransactions(10);

        if (record != null) {
            for (int i = 0; i < record.length; i++) {
                processed(record[i]);
            }
        }
    }

    /**
     * Initiates graphics UI, business logic and transaction module
     */
    private void init() {
        screen = new Screen(this);
        engine = new Engine(screen);

        screen.setCommandListener(this);

        try {
            txModule = new TransactionModule(this);
            txModule.setListener(this);
            txModule.deliverMissedTransactions();
            restoreBoughtFeatures();
        } catch (TransactionListenerException tle) {
            tle.printStackTrace();
        } catch (TransactionModuleException tme) {
            /* The payment module thereby signalsthat it refused a
             * connection from the TransactionModule class. Reasons
             * may be, for instance, that the provisioning data is
             * corrupt or incomplete or that there are too many
             * applications linked to that payment module. The
             * payment module should interact with the user to show
             * the reason.
             */
            tme.printStackTrace();
        }

        Display.getDisplay(this).setCurrent(screen);
    }
}
