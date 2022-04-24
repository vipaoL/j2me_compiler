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
package example.sip;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.SocketConnection;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import javax.microedition.sip.SipClientConnection;
import javax.microedition.sip.SipClientConnectionListener;
import javax.microedition.sip.SipConnectionNotifier;
import javax.microedition.sip.SipDialog;
import javax.microedition.sip.SipException;
import javax.microedition.sip.SipServerConnection;
import javax.microedition.sip.SipServerConnectionListener;


/**
 * Application demonstrates sip clients talking via sip server and registrar.
 * Both clients register to sip registrar. Send connection informations
 * in INVITE message to each other and start to communicate via sockets.
 *
 * Application demonstrates REGISTER, INVITE, BYE requests and
 * OK, RINGING responses.
 *
 * @version 1.2
 */
public abstract class BaseUAC extends MIDlet implements CommandListener,
    SipClientConnectionListener, SipServerConnectionListener {
    /** client states */
    private static final int DISCONNECTED = 0;
    private static final int REGISTERING = 1;
    private static final int REGISTERED = 2;
    private static final int INVITING = 3;
    private static final int TALKING = 4;
    private static final int RINGING = 5;
    private static final int BYE = 6;

    /** user name if the client */
    public String myName = "a";

    /** display name of the client */
    public String myDisplayName = "A";

    /** socket used by this client */
    public int mySocket = 1111;

    /** sip port the client is listening on */
    public int mySipPort = 9000;

    /** user name of second client */
    public String friendName = "B";

    /** socket used by second client */
    public int friendSocket = 2222;

    /** sip port of second client
     * we need this because running both
     * clients on the same machine. That isn't
     * typical use case.
     */
    public int friendSipPort = 9090;

    /** second client's domain */
    public String friendDomain = "localhost";

    /** forms used in ui */
    protected Form proxyFrm = null;
    protected Form registerFrm = null;
    protected Form waitScreen = null;
    protected Form inviteFrm = null;
    protected Form talkFrm = null;
    protected Form sendFrm = null;
    protected Form failFrm = null;
    protected Form ringingFrm = null;
    protected Form byeFrm = null;
    private Form backupForm = null;
    private Gauge progressGauge = null;

    /** commands used in ui */
    private Command exitCmd = new Command("Exit", Command.EXIT, 1);
    private Command registerCmd = new Command("Register", Command.OK, 1);
    private Command backCmd = new Command("Back", Command.BACK, 1);
    private Command nextCmd = new Command("Next", Command.SCREEN, 1);
    private Command inviteCmd = new Command("Invite", Command.OK, 1);
    private Command sendCmd = new Command("Send", Command.OK, 1);
    private Command okCmd = new Command("Ok", Command.OK, 1);
    private Command failedCmd = new Command("Failed", Command.BACK, 1);
    private Command denyCmd = new Command("Deny", Command.EXIT, 1);
    private Command answerCmd = new Command("Answer", Command.OK, 1);
    private Command byeCmd = new Command("Bye", Command.BACK, 1);
    private Displayable currentDisplay;
    private Displayable backDisplay;
    private Display display;

    /** receive thread runs forever */
    private Thread receiveThread = null;

    /** gauge  */
    private Thread gaugeThread = null;
    private boolean progressGaugeFinished = true;

    /** socket streams */
    private InputStream socketIStream;
    private OutputStream socketOStream;

    /** server socket */
    private ServerSocketConnection serverSocket = null;

    /** client socket */
    private SocketConnection sc = null;

    /** sip variables */
    private String proxyAddress = "";
    private SipConnectionNotifier scn;
    private String failMessage;
    private SipClientConnection scc;
    private SipServerConnection ssc;
    private SipDialog dialog;
    private String clientSockParams;
    private Sender sender;

    /** application status */
    private Status uaStatus = new Status();

    /** how long the gauge should run (simplified)*/
    private int finishGauge;

    public void start() {
        display = Display.getDisplay(this);

        if ((proxyAddress == null) || (proxyAddress.length() == 0)) {
            setDisplay(getProxyFrm());
        } else {
            setDisplay(getRegisterForm());
        }
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
        System.out.println("Closing app ...");
        tearDown();
        notifyDestroyed();
    }

    public void setProxyAddress(String address) {
        proxyAddress = address;
    }

    public void setSocket(int socket) {
        mySocket = socket;
    }

    /** Setup all params of the client.
     *
     * @param name username of the client.
     * @param displayName the display name of the user.
     * @param port sip listener port
     */
    public void setUserIdentity(String name, String displayName, int port) {
        myName = name;
        myDisplayName = displayName;
        mySipPort = port;
    }

    /** Setup all params of 2nd client.
     *
     * @param name the username of 2nd client
     * @param domain the domain of 2nd client
     * @param port the listener sip port of 2nd client
     */
    public void setFriendIdentity(String name, String domain, int port) {
        friendName = name;
        friendDomain = domain;
        friendSipPort = port;
    }

    /**
     * Display another screen
     */
    private void setDisplay(Displayable d) {
        if (currentDisplay != waitScreen) {
            backDisplay = currentDisplay;
        }

        display.setCurrent(d);
        currentDisplay = d;
    }

    private Form getProxyFrm() {
        if (proxyFrm == null) {
            if (proxyAddress.length() == 0) {
                proxyAddress = System.getProperty("microedition.hostname");

                if (proxyAddress == null) {
                    proxyAddress = "";
                }
            }

            proxyFrm = new Form("Proxy setup",
                    new Item[] { new TextField("Proxy host:", proxyAddress, 20, TextField.ANY) });
            proxyFrm.addCommand(nextCmd);
            proxyFrm.addCommand(exitCmd);
            proxyFrm.setCommandListener(this);
        }

        return proxyFrm;
    }

    private Form getRingingFrm(String message) {
        if (ringingFrm == null) {
            ringingFrm = new Form("Ringing ...");
            ringingFrm.append(new StringItem("Message:", message));
            ringingFrm.addCommand(denyCmd);
            ringingFrm.addCommand(answerCmd);
            ringingFrm.setCommandListener(this);
        }

        StringItem si = (StringItem)ringingFrm.get(0);
        si.setText(message);

        return ringingFrm;
    }

    private Form getRegisterForm() {
        if (registerFrm == null) {
            registerFrm = new Form("Welcome",
                    new Item[] {
                        new StringItem(null,
                            "You need to register to the registrar server.\n" +
                            "Please press register and wait for response.")
                    });

            registerFrm.addCommand(exitCmd);
            registerFrm.addCommand(registerCmd);
            registerFrm.setCommandListener(this);
        }

        return registerFrm;
    }

    private Form getWaitScreen(String title, int finishAfter, Form bForm) {
        if (waitScreen == null) {
            progressGauge = new Gauge(title, false, 10, 0);
            progressGauge.setLayout(Item.LAYOUT_CENTER | Item.LAYOUT_EXPAND | Item.LAYOUT_VCENTER);
            waitScreen = new Form("");
            waitScreen.append(progressGauge);
            waitScreen.addCommand(backCmd);
            waitScreen.setCommandListener(this);
        }

        finishGauge = (finishAfter == 0) ? Integer.MAX_VALUE : finishAfter;
        backupForm = bForm;
        progressGauge.setLabel(title);
        progressGauge.setValue(0);

        return waitScreen;
    }

    private Form getInviteForm() {
        if (inviteFrm == null) {
            inviteFrm = new Form("Invite",
                    new Item[] {
                        new StringItem(null, "Invite your sip friend " + friendName + " to talk.")
                    });
            inviteFrm.addCommand(exitCmd);
            inviteFrm.addCommand(inviteCmd);
            inviteFrm.setCommandListener(this);
        }

        return inviteFrm;
    }

    private Form getTalkForm() {
        if (talkFrm == null) {
            talkFrm = new Form("Talking", new Item[] {  });
            talkFrm.addCommand(sendCmd);
            talkFrm.addCommand(byeCmd);
            talkFrm.setCommandListener(this);
        }

        return talkFrm;
    }

    private Form getSendForm() {
        if (sendFrm == null) {
            sendFrm = new Form("Send",
                    new Item[] { new TextField("Enter message:", "", 255, TextField.ANY) });
            sendFrm.addCommand(okCmd);
            sendFrm.addCommand(byeCmd);
            sendFrm.setCommandListener(this);
        }

        TextField tfield = (TextField)sendFrm.get(0);

        if (tfield != null) {
            tfield.setString("");
        }

        return sendFrm;
    }

    private Form getFailFrm(String msg) {
        if (failFrm == null) {
            failFrm = new Form("Error");
            failFrm.addCommand(exitCmd);
            failFrm.setCommandListener(this);
            failFrm.append(new StringItem("", ""));
        }

        StringItem si = (StringItem)failFrm.get(0);
        si.setText(msg);

        return failFrm;
    }

    private Form getByeFrm() {
        if (byeFrm == null) {
            byeFrm = new Form("BYE");
            byeFrm.append("Received BYE from 2nd terminal.\nTerminating GoSIP session ...");
            byeFrm.addCommand(okCmd);
            byeFrm.setCommandListener(this);
        }

        return byeFrm;
    }

    public void commandAction(Command command, Displayable displayable) {
        if ((command == exitCmd) && (displayable == registerFrm)) {
            destroyApp(false);
        } else if ((command == exitCmd) && (displayable == failFrm)) {
            destroyApp(false);
        } else if ((command == registerCmd) && (displayable == registerFrm)) {
            setDisplay(getWaitScreen("Registration pending ...", 0, null));

            Thread t = listen(this);
            register(this, t);
        } else if ((command == backCmd) && (displayable == waitScreen)) {
            setDisplay(backDisplay);
        } else if ((command == nextCmd) && (displayable == waitScreen) &&
                (backDisplay == registerFrm)) {
            setDisplay(getInviteForm());
        } else if ((command == nextCmd) && (displayable == waitScreen) &&
                (backDisplay == inviteFrm)) {
            setDisplay(getTalkForm());
        } else if ((command == backCmd) && (displayable == waitScreen) &&
                (backDisplay == registerFrm)) {
            setDisplay(getRegisterForm());
        } else if ((command == backCmd) && (displayable == waitScreen) &&
                (backDisplay == inviteFrm)) {
            setDisplay(getInviteForm());
        } else if ((command == failedCmd) && (displayable == waitScreen) &&
                (backDisplay == registerFrm)) {
            setDisplay(getFailFrm("Failed to register:\n Cause: " + failMessage));
        } else if ((command == failedCmd) && (displayable == waitScreen) &&
                (backDisplay == inviteFrm)) {
            setDisplay(getFailFrm("Failed to invite:\n Cause: " + failMessage));
        } else if ((command == byeCmd) && (displayable == waitScreen)) {
            setDisplay(getInviteForm());
        } else if ((command == denyCmd) && (displayable == ringingFrm)) {
            sendCancel();
        } else if ((command == exitCmd) && (displayable == proxyFrm)) {
            destroyApp(false);
        } else if ((command == nextCmd) && (displayable == proxyFrm)) {
            TextField tfield = (TextField)proxyFrm.get(0);
            String proxy = tfield.getString();

            if ((proxy.length() == 0) || isIPAddress(proxy)) {
                if (proxyFrm.size() == 1) {
                    proxyFrm.append(new StringItem(null,
                            "Proxy name can't be empty or plain ip address. Use valid hostname."));
                    setDisplay(getProxyFrm());
                }
            } else {
                if (proxyFrm.size() > 1) {
                    proxyFrm.delete(1);
                }

                setProxyAddress(proxy);
                setDisplay(getRegisterForm());
            }
        } else if ((command == answerCmd) && (displayable == ringingFrm)) {
            setDisplay(getWaitScreen("Accepting ...", 0, null));
            sendAccepted();

            try {
                Thread.currentThread().sleep(1500);
            } catch (Exception e) {
            }

            if (uaStatus.getStatus() == RINGING) {
                openClientConnection(clientSockParams);
                uaStatus.setStatus(TALKING);
                stopGauge();
                commandAction(nextCmd, currentDisplay);
            }
        } else if ((command == inviteCmd) && (displayable == inviteFrm)) {
            setDisplay(getWaitScreen("Invite pending ...", 0, null));
            invite(this);
        } else if ((command == byeCmd) && (displayable == talkFrm)) {
            if (uaStatus.getStatus() == TALKING) {
                setDisplay(getWaitScreen("Bye ...", 10, getInviteForm()));
                sendBye();
            } else if (uaStatus.getStatus() == REGISTERED) {
                //do nothing
                setDisplay(getInviteForm());
            }
        } else if ((command == sendCmd) && (displayable == talkFrm)) {
            setDisplay(getSendForm());
        } else if ((command == nextCmd) && (displayable == waitScreen) &&
                (backDisplay == ringingFrm)) {
            setDisplay(getTalkForm());
        } else if ((command == okCmd) && (displayable == byeFrm)) {
            setDisplay(getInviteForm());
        } else if ((command == okCmd) && (displayable == sendFrm)) {
            TextField txtField = (TextField)sendFrm.get(0);

            if ((txtField != null) && (txtField.getString().length() > 0)) {
                send(txtField.getString());
            }

            setDisplay(getTalkForm());
        } else if ((command == backCmd) && (displayable == sendFrm)) {
            setDisplay(backDisplay);
        } else if ((command == exitCmd) && (displayable == inviteFrm)) {
            destroyApp(false);
        }
    }

    private Thread listen(final SipServerConnectionListener listener) {
        Thread t =
            new Thread() {
                public void run() {
                    try {
                        if (scn != null) {
                            scn.close();
                        }

                        scn = (SipConnectionNotifier)Connector.open("sip:" + mySipPort);
                        scn.setListener(listener);

                        try {
                            Thread.currentThread().sleep((1000));
                        } catch (Exception e) {
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            };

        t.start();

        return t;
    }

    private void register(final SipClientConnectionListener listener, final Thread waitFor) {
        Thread t =
            new Thread() {
                public void run() {
                    runGauge();

                    try {
                        try {
                            if (waitFor != null) {
                                waitFor.join();
                            } else {
                            }
                        } catch (InterruptedException ie) {
                        }

                        scc = (SipClientConnection)Connector.open("sip:" + proxyAddress +
                                ":5060;transport=udp");
                        scc.setListener(listener);
                        scc.initRequest("REGISTER", scn);

                        String adr =
                            myDisplayName + " <sip:" + myName + "@" + scn.getLocalAddress() + ":" +
                            scn.getLocalPort() + ">";
                        scc.setHeader("To", adr);
                        scc.setHeader("From", adr);
                        scc.setHeader("Content-Length", "0");
                        scc.setHeader("Max-Forwards", "6");
                        uaStatus.setStatus(REGISTERING);
                        scc.send();
                        uaStatus.waitUntilChanged();
                        progressGaugeFinished = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        failMessage = e.getMessage();
                        commandAction(failedCmd, currentDisplay);

                        return;
                    }
                }
            };

        t.start();
    }

    private void invite(final SipClientConnectionListener listener) {
        Thread t =
            new Thread() {
                public void run() {
                    runGauge();

                    try {
                        String host = scn.getLocalAddress();
                        String adr = "sip:" + myName + "@" + host + ":" + scn.getLocalPort();

                        String toAdr =
                            "sip:" + friendName + "@" + friendDomain + ":" + friendSipPort;

                        scc = (SipClientConnection)Connector.open(toAdr);
                        scc.setListener(listener);

                        scc.initRequest("INVITE", scn);

                        String message = "socket://" + host + ":" + mySocket;

                        scc.setHeader("To", toAdr);
                        scc.setHeader("From", adr);
                        scc.setHeader("Content-Type", "text/plain");
                        scc.setHeader("Content-Length", Integer.toString(message.length()));
                        scc.setHeader("Max-Forwards", "6");

                        OutputStream os = scc.openContentOutputStream();
                        os.write(message.getBytes());
                        uaStatus.setStatus(INVITING);
                        os.close(); // close and send
                        uaStatus.waitUntilChanged();

                        //Thread.currentThread().sleep(4000);
                        progressGaugeFinished = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        failMessage = e.getMessage();
                        stopGauge();
                        commandAction(failedCmd, currentDisplay);

                        return;
                    }
                }
            };

        t.start();
    }

    private synchronized void tearDown() {
        try {
            if (getSocketIStream() != null) {
                getSocketIStream().close();
            }

            if (getSocketOStream() != null) {
                getSocketOStream().close();
            }

            if (sender != null) {
                sender.stop();
            }

            if (scc != null) {
                scc.close();
            }

            if (ssc != null) {
                ssc.close();
            }

            if (scn != null) {
                scn.close();
            }

            if (sc != null) {
                sc.close();
            }

            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (Exception e) {
        }
    }

    private void openServerConnection(int socket) {
        Thread t =
            new Thread() {
                public void run() {
                    try {
                        serverSocket = (ServerSocketConnection)Connector.open("socket://:" +
                                mySocket);
                        System.out.println("waiting for connection socket://:" + mySocket);

                        SocketConnection sc = (SocketConnection)serverSocket.acceptAndOpen();
                        System.out.println("connection accepted from: " + sc.getAddress());
                        socketIStream = sc.openInputStream();
                        socketOStream = sc.openOutputStream();
                        sender = new Sender(getSocketOStream());

                        while (true) {
                            StringBuffer sb = new StringBuffer();
                            int c = 0;

                            while (((c = getSocketIStream().read()) != '\n') && (c != -1)) {
                                sb.append((char)c);
                            }

                            if (c == -1) {
                                break;
                            }

                            getTalkForm().append(new StringItem(friendName + ":", sb.toString()));
                        } //while
                    } catch (IOException ioe) {
                        failMessage = "Cannot open server socket";
                    } finally {
                        tearDown();
                    }
                }
            };

        t.start();
    }

    private void openClientConnection(final String params) {
        receiveThread =
            new Thread() {
                    public void run() {
                        try {
                            //open socket
                            sc = (SocketConnection)Connector.open(params);
                            System.out.println("Client opened connection " + params);
                            socketIStream = sc.openInputStream();
                            socketOStream = sc.openOutputStream();
                            sender = new Sender(getSocketOStream());

                            //Loop forever, receiving data
                            while (true) {
                                StringBuffer sb = new StringBuffer();
                                int c = 0;

                                while (((c = getSocketIStream().read()) != '\n') && (c != -1)) {
                                    sb.append((char)c);
                                }

                                if (c == -1) {
                                    break;
                                }

                                // Display message to user
                                getTalkForm().append(new StringItem(friendName, sb.toString()));
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        } finally {
                            tearDown();
                        }
                    }
                };
        receiveThread.start();
    }

    private void send(final String message) {
        getTalkForm().append(new StringItem(myName + ":", message));

        if (sender != null) {
            sender.send(message);
        }
    }

    private void sendBye() {
        Thread t =
            new Thread() {
                public void run() {
                    runGauge();

                    if ((dialog != null) && (dialog.getState() == SipDialog.CONFIRMED)) {
                        try {
                            SipClientConnection sc = dialog.getNewClientConnection("BYE");
                            sc.send();
                            System.out.println("Sending BYE ...");
                            uaStatus.setStatus(BYE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Dialog isn't initialized. Cannot send BYE.");
                    }
                }
            };

        t.start();
    }

    private void runGauge() {
        if ((gaugeThread == null) || !gaugeThread.isAlive()) {
            gaugeThread =
                new Thread() {
                        public void run() {
                            progressGaugeFinished = false;
                            progressGauge.setValue(0);

                            boolean up = true;
                            int i = 0;
                            int c = 0;

                            while (!progressGaugeFinished && (c < finishGauge)) {
                                progressGauge.setValue((up) ? i++ : i--);
                                up = ((i == 0) || (i == progressGauge.getMaxValue())) ? (up = !up)
                                                                                      : up;

                                try {
                                    Thread.currentThread().sleep(100);
                                } catch (InterruptedException e) {
                                }

                                c++;
                            } //while

                            if (backupForm != null) {
                                setDisplay(backupForm);
                            }

                            progressGaugeFinished = true;
                        }
                    };
            gaugeThread.start();
        }
    }

    private void stopGauge() {
        progressGaugeFinished = true;
    }

    public void notifyResponse(SipClientConnection sipClientConnection) {
        try {
            boolean ok = scc.receive(1000);

            if (!ok) {
                System.out.println("Response not received");

                return;
            }

            int code = scc.getStatusCode();
            System.out.println("Received status code: " + code);

            if (uaStatus.getStatus() == REGISTERING) {
                if (code == 200) {
                    System.out.println("Received OK after REGISTER");
                    uaStatus.setStatus(REGISTERED);
                    stopGauge();
                    commandAction(nextCmd, currentDisplay);
                } else {
                    stopGauge();
                    failMessage = "Unknown response: " + code;
                    commandAction(failedCmd, currentDisplay);
                }
            } else if (uaStatus.getStatus() == INVITING) {
                if ((code >= 100) && (code < 200)) {
                    System.out.println("Provisioning response: " + code);
                } else if (code == 200) {
                    System.out.println("Received OK after INVITE");
                    scc.initAck(); // initialize and send ACK
                    scc.send();
                    dialog = scc.getDialog();
                    uaStatus.setStatus(TALKING);
                    openServerConnection(mySocket);
                    stopGauge();
                    commandAction(nextCmd, currentDisplay);
                } else if (code == 486) { //BUSY HERE
                    stopGauge();
                    failMessage = "2nd client is busy.";
                    commandAction(failedCmd, currentDisplay);
                } else {
                    stopGauge();
                    failMessage = "Unknown response: " + code;
                    commandAction(failedCmd, currentDisplay);
                }
            } else if (uaStatus.getStatus() == BYE) {
                if (code == 200) {
                    System.out.println("Received OK after BYE");
                    uaStatus.setStatus(REGISTERED);
                    ssc.close();
                    stopGauge();
                    commandAction(byeCmd, currentDisplay);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyRequest(SipConnectionNotifier sipConnectionNotifier) {
        try {
            ssc = sipConnectionNotifier.acceptAndOpen(); // blocking
            System.out.println("Received request: " + ssc.getMethod().toString());

            if (ssc.getMethod().equals("INVITE")) {
                String contentType = ssc.getHeader("Content-Type");
                String contentLength = ssc.getHeader("Content-Length");
                int length = Integer.parseInt(contentLength);

                if (contentType.equals("text/plain")) {
                    InputStream is = ssc.openContentInputStream();
                    byte[] content = new byte[length];
                    is.read(content);
                    clientSockParams = new String(content);
                    //parse socket connection params
                    uaStatus.setStatus(RINGING);
                    setDisplay(getRingingFrm(clientSockParams));
                    ssc.initResponse(180); //RINGING
                    ssc.send();
                    dialog = ssc.getDialog();
                }
            } else if (ssc.getMethod().equals("ACK")) {
                if (uaStatus.getStatus() == RINGING) {
                    openClientConnection(clientSockParams);
                    uaStatus.setStatus(TALKING);
                    stopGauge();
                    commandAction(nextCmd, currentDisplay);
                }
            } else if (ssc.getMethod().equals("BYE")) {
                if (dialog.isSameDialog(ssc)) {
                    setDisplay(getByeFrm());

                    if (uaStatus.getStatus() == TALKING) {
                        ssc.initResponse(200);
                        ssc.send();
                        getTalkForm().setTitle("Client disconnected !");

                        try {
                            if (serverSocket != null) {
                                serverSocket.close();
                            } else if (sc != null) {
                                sc.close();
                            }
                        } catch (Exception e) {
                        }

                        ;
                        uaStatus.setStatus(REGISTERED);
                    }
                } else {
                    System.out.println("Not a same dialog");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendCancel() {
        Thread t =
            new Thread() {
                public void run() {
                    try {
                        ssc.initResponse(486); //BUSY HERE
                        ssc.send();
                    } catch (Exception e) {
                        System.out.println("Exception when sending cancel");
                        e.printStackTrace();
                    }
                }
            };

        t.start();
    }

    private void sendAccepted() {
        Thread t =
            new Thread() {
                public void run() {
                    try {
                        ssc.initResponse(200); //OK
                        ssc.send();
                        // save Dialog
                        dialog = ssc.getDialog();
                        ssc.close();
                    } catch (Exception e) {
                        System.out.println("Exception while sending OK");
                        e.printStackTrace();
                    }
                }
            };

        t.start();
    }

    private boolean isIPAddress(String address) {
        String addrex = address + ".";
        char c;
        int digcount = 0;
        int numcount = 0;

        for (int i = 0; i < addrex.length(); i++) {
            c = addrex.charAt(i);

            if (c == '.') {
                digcount = 0;
                numcount++;

                if (numcount > 4) {
                    return false;
                }
            } else {
                if (('0' <= c) && (c <= '9')) {
                    digcount++;

                    if (digcount > 3) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }

        return (numcount == 4);
    }

    /**
     * Help to keep status of application.
     *
     */
    class Status {
        private boolean changed = false;
        private int status = 0;

        public int getStatus() {
            return status;
        }

        public synchronized int waitUntilChanged() {
            while (!changed) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }

            changed = false;

            return status;
        }

        public synchronized void setStatus(int status) {
            this.status = status;
            changed = true;
            notify();
        }
    }

    public InputStream getSocketIStream() {
        return socketIStream;
    }

    public OutputStream getSocketOStream() {
        return socketOStream;
    }
}
