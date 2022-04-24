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
import java.util.*;
import javax.swing.JOptionPane;

import com.sun.kvem.midp.io.j2se.wma.*;
import com.sun.kvem.midp.io.j2se.wma.client.WMAClient;
import com.sun.kvem.midp.io.j2se.wma.client.WMAClient.MessageListener;
import com.sun.kvem.midp.io.j2se.wma.client.WMAClientFactory;


/*
 * This class shows an example usage of the Wireless Toolkit WMA Bridge API
 * for connecting J2SE clients to the toolkit's messaging environment.
 *
 * Use the build and run scripts in the same directory to build and run the
 * example.
 *
 * See the WMA Bridge API documentation included with the Toolkit for more
 * information on using the API.
 */
public class WMABridgeAPIExample implements WMAClient.MessageListener {
    public static int DEFAULT_SMS_PORT = 50000;
    public static int DEFAULT_CBS_MESS_ID = 50001;
    private WMAClient wtkClient;
    private BufferedReader inputReader;
    private int smsPort = DEFAULT_SMS_PORT;
    private int cbsMessID = DEFAULT_CBS_MESS_ID;

    public WMABridgeAPIExample() throws Exception {
        // null argument for the phone number - we'll be assigned one.
        wtkClient = WMAClientFactory.newWMAClient(null, WMAClient.SEND_AND_RECEIVE);

        wtkClient.connect();
        wtkClient.setMessageListener(this);

        // connect the standard input to our reader
        inputReader = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * shows help on using this utility
     */
    private void displayHelp() {
        System.out.println("Send and receive SMS and CBS messages " +
            "to and from Wireless Toolkit emulators.\n" + "Commands:\n" +
            " sms <phoneNumber> <message> " + ": send sms text message to phoneNumber\n" +
            " cbs <message>               " + ": send cbs text message broadcast\n" +
            " getPort                     " + ": display the port number to which sms is sent\n" +
            " getMessageID                " + ": display the message ID on which cbs is sent\n" +
            " setPort <port>              " + ": set the port to which sms is sent\n" +
            " setMessageID <messID>       " + ": set the message ID on which cbs is sent\n" +
            " help                        " + ": this help screen\n" +
            " quit                        " + ": exit the program");
    }

    /**
     * show the sms port
     */
    private void displayPort() {
        System.out.println("Using port " + smsPort + " for sms messages.");
    }

    /**
     * show the cbs message ID
     */
    private void displayMessageID() {
        System.out.println("Using message ID " + cbsMessID + " for cbs messages.");
    }

    /**
     * handle the setPort command
     */
    private void handleSetPort(String argString) {
        if (argString.length() > 0) {
            try {
                int port = Integer.parseInt(argString);

                if ((port >= 0) && (port <= 65535)) {
                    smsPort = port;
                    displayPort();
                } else {
                    System.out.println("Port must be in the range 0 to 65535.");
                }
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid number format");
            }
        } else {
            System.out.println("setPort requires a port number.");
        }
    }

    /**
     * handle the setMessageID command
     */
    private void handleSetMessageID(String argString) {
        if (argString.length() > 0) {
            try {
                int messID = Integer.parseInt(argString);

                if ((messID >= 0) && (messID <= 65535)) {
                    cbsMessID = messID;
                    displayMessageID();
                } else {
                    System.out.println("Message ID must be in the range 0 to 65535.");
                }
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid number format");
            }
        } else {
            System.out.println("setMessageID requires a message ID number.");
        }
    }

    /**
     * handle the sms command
     */
    private void handleSMS(String argString) {
        int spacePos = argString.indexOf(' ');
        String toPhoneNumber;
        String messageStr;

        if (spacePos > 0) {
            toPhoneNumber = argString.substring(0, spacePos);
            messageStr = argString.substring(spacePos + 1).trim();
        } else {
            System.out.println("sms requires a phone number and a message.");

            return;
        }

        Message message = new Message(messageStr);
        message.setToAddress("sms://" + toPhoneNumber + ":" + smsPort);
        message.setFromAddress("sms://" + wtkClient.getPhoneNumber());

        try {
            wtkClient.send(message);
            System.out.println("Sent sms.");
        } catch (Exception e) {
            System.err.println("Caught exception sending sms: ");
            e.printStackTrace();
        }
    }

    /**
     * handle the cbs command
     */
    private void handleCBS(String argString) {
        if (argString.length() > 0) {
            Message message = new Message(argString);
            message.setToAddress("cbs://:" + cbsMessID);

            try {
                wtkClient.send(message);
                System.out.println("Sent cbs.");
            } catch (Exception e) {
                System.err.println("Caught exception sending cbs: ");
                e.printStackTrace();
            }
        } else {
            System.out.println("cbs requires a message.");
        }
    }

    /**
     * get a command and perform it
     * returns false when it's time to quit
     */
    private boolean processNextCommand() throws IOException {
        System.out.print("Command: ");

        String commandLine = inputReader.readLine();

        if ((commandLine == null) || (commandLine.length() == 0)) {
            return true;
        }

        commandLine = commandLine.trim();

        int spacePos = commandLine.indexOf(' ');
        String firstWord;
        String args;

        if (spacePos > 0) { // more than 1 word
            firstWord = commandLine.substring(0, spacePos);
            args = commandLine.substring(spacePos + 1).trim();
        } else { // only 1 word
            firstWord = commandLine;
            args = "";
        }

        // perform the appropriate action for this command
        if (firstWord.equalsIgnoreCase("getport")) {
            displayPort();
        } else if (firstWord.equalsIgnoreCase("getmessageid")) {
            displayMessageID();
        } else if (firstWord.equalsIgnoreCase("help")) {
            displayHelp();
        } else if (firstWord.equalsIgnoreCase("quit") || firstWord.equalsIgnoreCase("exit")) {
            return false;
        } else if (firstWord.equalsIgnoreCase("setport")) {
            handleSetPort(args);
        } else if (firstWord.equalsIgnoreCase("setmessageid")) {
            handleSetMessageID(args);
        } else if (firstWord.equalsIgnoreCase("sms")) {
            handleSMS(args);
        } else if (firstWord.equalsIgnoreCase("cbs")) {
            handleCBS(args);
        }

        return true;
    }

    /**
     * the main loop to drive the program
     */
    public void driverLoop() throws Exception {
        System.out.println("Running WMABridgeAPIExample. Phone number is " +
            wtkClient.getPhoneNumber());
        displayHelp();
        displayPort();
        displayMessageID();

        while (true) {
            try {
                if (processNextCommand() == false) {
                    break;
                }
            } catch (Exception e) {
                System.err.println("Exception caught: " + e);
                e.printStackTrace();
            }
        }

        wtkClient.setMessageListener(null);
        wtkClient.unregisterFromServer();
    }

    /**
     * callback to report a received message
     * specified by the WMAClient.MessageListener interface
     */
    public void notifyIncomingMessage(WMAClient client) {
        try {
            WMAMessage mess = wtkClient.receive();

            if (mess instanceof Message) {
                Message message = (Message)mess;
                String title;

                if (message.isSMS()) {
                    title = "SMS from " + message.getFromAddress() + " on port " +
                        message.getToPort();
                } else {
                    title = "CBS with message ID " + message.getToPort();
                }

                // notify the user of the new message
                JOptionPane.showMessageDialog(null, message.toString(), title,
                    JOptionPane.PLAIN_MESSAGE);
            }
        } catch (IOException ioe) {
            System.err.println("Caught executing:");
            ioe.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            WMABridgeAPIExample example = new WMABridgeAPIExample();
            example.driverLoop();
        } catch (Exception e) {
            System.err.println("WMABridgeAPIExample caught:");
            e.printStackTrace();
        }

        System.exit(0);
    }
}
