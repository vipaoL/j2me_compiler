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
package datagram;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

public class Server implements Runnable, CommandListener {
    private DatagramMIDlet parent;

    private Display display;

    private Form f;

    private StringItem si;

    private TextField tf;

    private Command sendCommand = new Command("Send", Command.ITEM, 1);

    private Command exitCommand = new Command("Exit", Command.EXIT, 1);

    Sender sender;

    private String address;

    private int port;

    public Server(DatagramMIDlet m, int p) {
	parent = m;
	port = p;
	display = Display.getDisplay(parent);
	f = new Form("Datagram Server");
	si = new StringItem("Status:", " ");
	tf = new TextField("Send:", "", 30, TextField.ANY);
	f.append(si);
	f.append(tf);
	f.addCommand(sendCommand);
	f.addCommand(exitCommand);
	f.setCommandListener(this);
	display.setCurrent(f);
    }

    public void start() {
	Thread t = new Thread(this);
	t.start();
    }

    public void run() {
	String portString = String.valueOf(port);
	try {
	    si.setText("Waiting for connection on port "+portString);

	    DatagramConnection dc = (DatagramConnection) Connector
		    .open("datagram://:" + portString);

	    sender = new Sender(dc);

	    while (true) {
		Datagram dg = dc.newDatagram(100);
		dc.receive(dg);
		address = dg.getAddress();

		si.setText("Message received - "
			+ new String(dg.getData(), 0, dg.getLength()));
	    }
	} catch (IOException ioe) {
	    Alert a = new Alert("Server", "Port " + portString
		    + " is already taken.", null, AlertType.ERROR);
	    a.setTimeout(Alert.FOREVER);
	    a.setCommandListener(this);
	    display.setCurrent(a);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void commandAction(Command c, Displayable s) {
	if ((c == sendCommand) && !parent.isPaused()) {
	    if (address == null) {
		si.setText("No destination address");
	    } else {
		sender.send(address, tf.getString());
	    }
	}

	if ((c == Alert.DISMISS_COMMAND) || (c == exitCommand)) {
	    parent.destroyApp(true);
	    parent.notifyDestroyed();
	}
    }

    public void stop() {
    }
}
