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
package example.imageviewer;

import java.io.*;

import javax.microedition.content.*;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;


/**
 * Simple Image Viewer for PNG images.
 * This application uses the CHAPI API both
 * to invoke a content handler via a URL and to handle
 * an Invocation request.
 *
 * The application implements the CHAPI request listener to respond
 * to requests. The listener constructs an Image from the
 * URL and displays it.  A "Back" command is made available
 * to finish the request.
 *
 * A response listener is used to handle the response
 * as an informational alert to the user.
 *
 * The application provides a simple user interface to enter a URL
 * and a "Go" command to invoke the URL.
 *
 * A "Save" command is used to invoke a ScreenSaver application.
 *
 */
public class ImageViewer extends MIDlet implements CommandListener, RequestListener,
    ResponseListener {
    /** The type for PNG. */
    static final String PNG_TYPE = "image/png";

    /** The suffix supported. */
    static final String PNG_SUFFIX = ".png";

    /** The Content Handler ID. */
    static final String CHID = "com.sun.example.imageviewer";

    /** The list of applications allowed to access. */
    static final String[] ACCESS_ALLOWED = { "com.sun.example" };

    /** The content handlers class that implements the image viewer. */
    static final String CH_CLASSNAME = "example.imageviewer.ImageViewer";

    /** The application class that will be calling Registry functions. */
    static final String CALLER_CLASSNAME = "example.imageviewer.ImageViewer";

    /** Current invocation, null if no current Invocation. */
    Invocation invoc;

    /** ContentHandlerServer from which to get requests. */
    ContentHandlerServer handler;

    /** Access to Registry functions and responses. */
    Registry registry;

    /** The Display for the viewer. */
    Display display;

    /** The Form to display the image. */
    Form form;

    /** The ImageItem displaying the image. */
    ImageItem imageItem;

    /** The TextField to input a URL. */
    TextField urlField;

    /** The Back Command to dismiss the viewer. */
    Command backCommand = new Command("Back", Command.BACK, 1);

    /** The Go Command to invoke the link. */
    Command goCommand = new Command("Go", Command.OK, 1);

    /** The Save Command to invoke the screen saver. */
    Command saveCommand = new Command("Save", Command.SCREEN, 2);

    /**
     * Initialize the viewer user interface and listeners for requests
     * and responses.
     */
    public ImageViewer() {
        // Setup the user interface components
        display = Display.getDisplay(this);

        form = new Form("Image Viewer");
        urlField = new TextField("Enter a link to an image", "http://", 80, TextField.HYPERLINK);
        imageItem = new ImageItem(null, null, Item.LAYOUT_CENTER, "-no image-");
        form.setCommandListener(this);

        showURL();

        /*
         * Get access to the registry for this application
         * and setup the listener for responses to invocations.
         */
        registry = Registry.getRegistry(CALLER_CLASSNAME);
        registry.setListener(this);

        /*
         * Get access to the ContentHandlerServer for
         * incoming invocation requests.
         */
        try {
            handler = Registry.getServer(CH_CLASSNAME);
        } catch (ContentHandlerException che) {
            // Our registration is missing, reinstate it
            register();
        }

        /*
         * Register the listener to be notified of new requests.
         * If there is a pending request the listener will be
         * notified immediately.
         */
        if (handler != null) {
            handler.setListener(this);
        }
    }

    /**
     * Switch to show the URL input field.
     * Enable the "Go" command.
     */
    void showURL() {
        form.deleteAll();
        form.removeCommand(backCommand);
        form.removeCommand(saveCommand);

        form.addCommand(goCommand);
        form.append(urlField);
        display.setCurrent(form);
    }

    /**
     * Show an image.
     * Enable the "Go" command.
     * @param image the Image to display
     */
    void showImage(Image image) {
        form.deleteAll();
        form.removeCommand(goCommand);

        imageItem.setImage(image);
        form.addCommand(backCommand);
        form.addCommand(saveCommand);
        form.append(imageItem);
        display.setCurrent(form);
    }

    /**
     * Start the application; no additional action needed.
     */
    public void startApp() {
    }

    /**
     * Pause the application; no additional action needed.
     */
    public void pauseApp() {
    }

    /**
     * Cleanup and destroy the application.
     *
     * @param force true to force the exit (always exit)
     */
    public void destroyApp(boolean force) {
        // Reset the listeners
        if (handler != null) {
            handler.setListener(null);
        }

        if (registry != null) {
            registry.setListener(null);
        }

        /*
         * Finish any pending invocation;
         * ignore the mustExit return since the application is exiting
         */
        finish(Invocation.OK);
    }

    /**
     * Process a new Invocation request.
     * In this example, the new request may interrupt the display of a
     * current request.
     * If so, the current request is "finished" so the new
     * request can be displayed.
     *
     * To avoid application thrashing do not exit, even if requested,
     * until the new request is finished.
     *
     * @param h the ContentHandlerServer with the new request
     */
    public void invocationRequestNotify(ContentHandlerServer h) {
        /*
         * If there is a current Invocation finish it
         * so the next Contact can be displayed.
         */
        if (invoc != null) {
            handler.finish(invoc, Invocation.OK);
        }

        // Dequeue the next invocation
        invoc = handler.getRequest(false);

        if (invoc != null) {
            // Display the content of the image
            displayImage(invoc);
        }
    }

    /**
     * Process a response to a previous request.
     * Put up the prompt for URL again.
     * @param r the Registry with the response
     */
    public void invocationResponseNotify(Registry r) {
        Invocation resp = r.getResponse(false);

        if (resp != null) {
            int st = resp.getStatus();
            String msg;

            if (st == Invocation.OK) {
                msg = "Request successful";
            } else if (st == Invocation.CANCELLED) {
                msg = "Request cancelled";
            } else {
                msg = "Request failed";
            }

            // Restore the URL prompt after the alert
            showURL();

            Alert alert = new Alert("Request completed", msg, null, AlertType.INFO);
            display.setCurrent(alert, form);
        }
    }

    /**
     * Handle command on the Form.
     * On user command "Back", finish the Invocation request.
     * On user command "Go", invoke the supplied URL.
     * @param c the Command
     * @param s the Displayable the command occurred on
     */
    public void commandAction(Command c, Displayable s) {
        if (c == backCommand) {
            finish(Invocation.OK);
            /*
             * Relinquish the display until this application
             * receives another request to display.
             * The image will stay visible until the next request
             * or the application is destroyed.
             */
            display.setCurrent(null);
        }

        if (c == goCommand) {
            /*
             * Invoke the URL in a new Thread to prevent blocking the
             * user interface.
             */
            Runnable r =
                new Runnable() {
                    public void run() {
                        doInvoke(urlField.getString());
                    }
                    ;
                };

            (new Thread(r)).start();
        }

        if (c == saveCommand) {
            /*
             * Use a new thread to send the URL from the
             * current invocation to the ScreenSaver.
             */
            Runnable r =
                new Runnable() {
                    public void run() {
                        doSave();
                    }
                    ;
                };

            (new Thread(r)).start();
        }
    }

    /**
     * Invoke the URL provided.
     * @param url the URL of an image
     */
    void doInvoke(String url) {
        try {
            Invocation invoc = new Invocation(url);
            boolean mustExit = registry.invoke(invoc);

            if (mustExit) {
                // App must exit before invoked application can run
                destroyApp(true); // cleanup
                notifyDestroyed(); // inform the application manager
            } else {
                // Application does not need to exit
            }
        } catch (IOException ex) {
            Alert alert = new Alert("Image not available", "Could not link to " + url, null, null);
            display.setCurrent(alert);
        }
    }

    /**
     * Finish the current invocation if any.
     * If the application is ask to exit then
     * the cleanup in destroyApp is done and the application
     * manager notified of the exit.
     *
     * @param status the status to pass to finish
     * @return true if the application should exit
     */
    boolean finish(int status) {
        if (invoc != null) {
            boolean mustExit = handler.finish(invoc, status);
            invoc = null;

            if (mustExit) {
                // Viewer must exit before response can be delivered
                destroyApp(true);
                notifyDestroyed(); // inform the application manager

                return true;
            } else {
                // Application does not need to exit
            }
        }

        return false;
    }

    /**
     * Fetch the Image and display.
     * @param invoc an Invocation with the URL and contents
     */
    void displayImage(Invocation invoc) {
        HttpConnection conn = null;
        InputStream is = null;

        if (imageItem != null) {
            imageItem.setImage(null); // remove any old image
        }

        try {
            conn = (HttpConnection)invoc.open(false);

            // Check the status and read the content
            int status = conn.getResponseCode();

            if (status != conn.HTTP_OK) {
                Alert alert =
                    new Alert("Can not display the image", "image not found at " + invoc.getURL(),
                        null, AlertType.ERROR);
                display.setCurrent(alert);
                finish(Invocation.CANCELLED);

                return;
            }

            String type = conn.getType();

            if (!PNG_TYPE.equals(type)) {
                Alert alert =
                    new Alert("Can not display the image", "Unknown type " + type, null,
                        AlertType.ERROR);
                display.setCurrent(alert);
                finish(Invocation.CANCELLED);

                return;
            }

            // Get the image from the connection
            is = conn.openInputStream();

            Image image = Image.createImage(is);

            // Display the image
            showImage(image);

            display.setCurrent(form);
        } catch (IOException e) {
            Alert alert =
                new Alert("Can not display the image", "Image not available", null, AlertType.ERROR);
            display.setCurrent(alert);
            finish(Invocation.CANCELLED);

            return;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }

                if (conn != null) {
                    conn.close();
                }
            } catch (IOException ioe) {
                // Ignore exceptions in close
            }
        }
    }

    /**
     * Dynamic registration of content handler.
     * This example shows the registration of a MIDlet as a handler with
     * the application ID "com.sun.example.imageviewer",
     * for the type "image/png", suffix ".png", and access
     * allowed to the "com.sun.example".
     */
    void register() {
        try {
            // Create a content handler instance for our Generic PNG Handler
            String[] chTypes = { PNG_TYPE };
            String[] chSuffixes = { PNG_SUFFIX };
            String[] chActions = { ContentHandler.ACTION_OPEN };
            String chClassname = CH_CLASSNAME;

            handler =
                registry.register(chClassname, chTypes, chSuffixes, chActions, null, /* action name maps */
                    CHID, ACCESS_ALLOWED);
        } catch (ContentHandlerException ex) {
            Alert alert =
                new Alert("Unable to register handler", "Handler conflicts with another handler",
                    null, AlertType.ERROR);
            display.setCurrent(alert);
        } catch (ClassNotFoundException cnf) {
            Alert alert =
                new Alert("Unable to register handler", "Handler class not found", null,
                    AlertType.ERROR);
            display.setCurrent(alert);
        }
    }

    /**
     * Saving the image to a ScreenSaver application.
     *
     * Suppose there is a ScreenSaver application that is used to
     * control the idle image.  This Image viewer application
     * should be able to invoke the screen saver so the current image
     * can be set as the idle screen.  An additional command is
     * added to invoke the screensaver.
     * Invoking the screen saver MUST not be called
     * from the UI thread because it may block while I/O completes.
     */
    void doSave() {
        try {
            /*
             * Invoke the application for the ID "ScreenSaver"
             * passing the URL if the Image.
             */
            Invocation nextInvoc = new Invocation();
            nextInvoc.setID("ScreenSaver");
            nextInvoc.setURL(invoc.getURL());
            nextInvoc.setResponseRequired(false);

            // Chain the new invocation to the previous invocation
            boolean mustExit = registry.invoke(nextInvoc, invoc);

            if (mustExit) {
                // App must exit before invoked application can run
                destroyApp(true); // cleanup
                notifyDestroyed(); // inform the application manager
            } else {
                // Application does not need to exit
            }
        } catch (IOException ex) {
            Alert alert =
                new Alert("Invoking ScreenSaver", "Could not save the image", null, AlertType.ERROR);
            display.setCurrent(alert);
        }
    }
}
