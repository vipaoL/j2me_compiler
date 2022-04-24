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
package example.text;

import java.io.*;

import java.lang.Thread;

import java.util.*;

import javax.microedition.content.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.*;


/**
 * A text viewer.
 */
public class TextViewer extends MIDlet implements CommandListener, ItemCommandListener, Runnable {
    /** Resource to test if the MIDlet is executed in the OTA mode. */
    private static final String JAR_RESOURCE = "/META-INF/MANIFEST.MF";

    /** Command to open the portal. */
    private Command favCommand = new Command("Home", Command.OK, 2);

    /** Command to return from Favorites screen. */
    private Command backCommand = new Command("Back", Command.BACK, 1);

    /** Command to exit the application. */
    private Command exitCommand = new Command("Exit", Command.EXIT, 1);

    /** The display for this MIDlet. */
    private Display display;

    /** The Form holding the contents. */
    private TextForm form;

    /** The Favorites form with the list of favorite places. */
    private Favorites favorites;

    /**
     * The next Displayable to be shown after an alert or <code>null</code>
     * if the previous should be restored.
     */
    private Displayable nextDisplayable;

    /** The handler thread used to offload processing of Invocations. */
    private TextInvocation invocationHandler;

    /** flag to indicate to thread that it should stop. */
    private boolean stopping;

    /** The registry for access to invocations and responses. */
    Registry registry;

    /**
     * The current response which is processed in the response processing
     * thread.
     */
    private Invocation response;

    /**
     * Create a new Text viewer and set the display.
     * Check for a new Invocation; if there is one pending
     * skip the splash screen.
     */
    public TextViewer() {
        display = Display.getDisplay(this);

        // test if the application is executed in the OTA mode
        InputStream is = getClass().getResourceAsStream(JAR_RESOURCE);

        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
            }
        } else {
            Alert alert =
                new Alert("CHAPI application", "Please execute this application via OTA", null,
                    AlertType.ERROR);
            alert.setCommandListener(this);
            display.setCurrent(alert);

            return;
        }

        form = new TextForm(this, "Text Viewer");
        form.addCommand(backCommand);
        form.addCommand(favCommand);
        form.setCommandListener(this);

        try {
            // Initialize the saved favorites list
            setupFavorites();

            // Get a reference to the registry
            registry = Registry.getRegistry(this.getClass().getName());

            /**
             * If there is an initial request or response skip the
             * splash screen.
             */
            ContentHandlerServer handler = Registry.getServer(this.getClass().getName());
            Invocation invoc = handler.getRequest(false);
            response = registry.getResponse(false);
            invocationHandler = new TextInvocation(this, invoc);

            if ((invoc == null) && (response == null)) {
                // we don't have any invocations, show the favorites form
                display.setCurrent(favorites);
            } else {
                // show the favorites form only if something goes wrong
                nextDisplayable = favorites;
            }

            // Start the Invocation Handler thread
            // When it gets started it will display the initial Invocation
            invocationHandler.start();

            // Start the thread to pick up responses
            new Thread(this).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Nothing to do, display already set
     */
    protected void startApp() {
    }

    /*
     * No resources to release;
     * Invocation handler Thread needs to run while paused
     * and will resume if any request arrives.
     */
    protected void pauseApp() {
    }

    /**
     * MIDlet is being destroyed.
     * Signal the Invocation handling thread to exit.
     * @param forced to exit; this midlet always cooperates
     */
    protected void destroyApp(boolean forced) {
        if (invocationHandler != null) {
            invocationHandler.stop();
            stopping = true;
        }
    }

    /**
     * Received a new Invocation, display the contents
     * of the URL in the Form.  Called from TextInvocation in
     * its thread; so it is ok to take a while to process
     * @param invoc the new ACTIVE invocation.
     */
    void show(Invocation invoc) {
        form.displayText(invoc);
        display.setCurrent(form);
        nextDisplayable = null;
    }

    /**
     * Show an alert with an error message.
     * @param message the message to show
     */
    void showAlert(String message) {
        Alert alert = new Alert("Alert", message, null, AlertType.WARNING);
        alert.setTimeout(Alert.FOREVER);

        if (nextDisplayable != null) {
            display.setCurrent(alert, nextDisplayable);
            nextDisplayable = null;
        } else {
            display.setCurrent(alert);
        }
    }

    /**
     * Show the favorites screen.
     */
    void setupFavorites() {
        if (favorites == null) {
            favorites = new Favorites("Favorite Links");
            favorites.addCommand(exitCommand);
            favorites.setCommandListener(this);
            favorites.setItemCommandListener(this);

            // Add one from the properties file if there are none saved
            if (favorites.size() == 1) {
                for (int i = 1;; i++) {
                    String title = getAppProperty("Favorite-Title-" + i);
                    String url = getAppProperty("Favorite-Link-" + i);

                    if ((title == null) || (url == null)) {
                        break;
                    }

                    favorites.addFavorite(title, url);
                }
            }
        }
    }

    /**
     * Respond to commands
     * @param c the command invoked
     * @param s the screen with the command
     */
    public void commandAction(Command c, Displayable s) {
        try { 
            if ((c == backCommand) && (s == form)) {
                Invocation invoc = invocationHandler.currentInvocation();

                if (invoc != null) {
                    if (invocationHandler.done(invoc, Invocation.OK)) {
                        /*
                         * Returning the response requires the MIDlet to exit.
                         * Cleanup with destroyApp and notify destroyed
                         */
                        destroyApp(true);
                        notifyDestroyed();
                    }
                }
            } else if (c == favCommand) {
                Invocation invoc = invocationHandler.currentInvocation();

                if (invoc != null) {
                    // cancel the current invocation, this will cancel all
                    // invocations until the favorites list is displayed or
                    // the text viewer is ended
                    if (invocationHandler.done(invoc, Invocation.CANCELLED)) {
                        /*
                         * Returning the response requires the MIDlet to exit.
                         * Cleanup with destroyApp and notify destroyed
                         */
                        destroyApp(true);
                        notifyDestroyed();
                    }
                } else {
                    // Show the favorites list
                    setupFavorites();
                    display.setCurrent(favorites);
                }
            } else if ((c == backCommand) && (s == favorites)) {
                display.setCurrent(form);
            } else if ((c == exitCommand) || (c == Alert.DISMISS_COMMAND)) {
                destroyApp(true);
                notifyDestroyed();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Handle selection of URL item.
     * Follow the link by invoking the URL.
     * @param c the command
     * @param item the Item to which the command applies
     */
    public void commandAction(Command c, Item item) {
        String url = favorites.getURL(item);

        if (url == null) {
            url = ((StringItem)item).getText();
        }

        // Fix relative links to be relative
        Invocation currentInvoc = invocationHandler.currentInvocation();

        if ((currentInvoc != null) && !url.startsWith("http://")) {
            String nohttp = url.substring(5);
            String base = currentInvoc.getURL();

            if (base != null) {
                int lastslash = base.lastIndexOf('/');
                base = base.substring(0, lastslash + 1);
                url = base.concat(nohttp);
            }
        }

        /*
         * Initialize a new invocation with the URL.
         * Create a new TextInvocation to put the Invocation in a new thread.
         */
        try {
            Invocation nextInvoc = new Invocation();
            nextInvoc.setURL(url);
            nextInvoc.setResponseRequired(true);

            TextInvocation ti = new TextInvocation(this, nextInvoc, currentInvoc);
            ti.start();
        } catch (Exception e) {
            Alert alert = new Alert("Link not available", url, null, AlertType.ERROR);
            display.setCurrent(alert);
        }
    }

    /**
     * Pickup the responses to previous Invocations.1
     */
    public void run() {
        while (!stopping) {
            // we can start with some already obtained response
            if (response != null) {
                invocationHandler.finishedInvocation(response);

                Invocation prevInvocation = response.getPrevious();

                if (prevInvocation != null) {
                    if (response.getStatus() == Invocation.CANCELLED) {
                        // cancel the previous as well
                        if (invocationHandler.done(prevInvocation, Invocation.CANCELLED)) {
                            /*
                             * Returning the response requires the MIDlet to exit.
                             * Cleanup with destroyApp and notify destroyed
                             */
                            destroyApp(true);
                            notifyDestroyed();
                        }
                    } else {
                        // Re-display
                        show(prevInvocation);
                    }
                } else {
                    // no previous invocation, show the favorites
                    setupFavorites();
                    display.setCurrent(favorites);
                }
            }

            response = registry.getResponse(true);
        }
    }
}
