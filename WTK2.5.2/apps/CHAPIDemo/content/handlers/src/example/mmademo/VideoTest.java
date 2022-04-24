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
package example.mmademo;

import java.util.Vector;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;


/**
 * An example MIDlet to demo MMAPI video features
 *
 * @version 1.5
 */
public class VideoTest extends MIDlet implements CommandListener, Runnable {
    private static VideoCanvas videoCanvas = null;
    private static VideoPlayer videoPlayer = null;
    private static VideoTest instance = null;
    private static final String INITIAL_URL = "http://";

    /** The current title to playing. */
    private static String currTitle;

    /** The current url playing. */
    private static String currURL;

    /** Show using a VideoPlayer; (otherwise use a CanvasPlayer) */
    private static boolean usePlayer = true;
    private final Command exitCommand = new Command("Exit", Command.EXIT, 1);
    private final Command playCommand = new Command("Play", Command.SCREEN, 1);
    private final Command backCommand = new Command("Back", Command.BACK, 1);
    private Display display;
    private final Form entryForm;
    private final TextField urlTextField;

    /** The Handler for incoming content handler requests. */
    private VideoHandler handler;

    public VideoTest() {
        instance = this;
        display = Display.getDisplay(this);

        entryForm = new Form("MMAPI Video Player");
        urlTextField = new TextField("Enter the URL to a media file", INITIAL_URL, 100,
                TextField.ANY);
        entryForm.append(urlTextField);

        entryForm.addCommand(playCommand);
        entryForm.addCommand(exitCommand);
        entryForm.setCommandListener(this);

        /* Start the async listener for play requests. */
        handler = new VideoHandler(this);

        if (handler.current() == null) {
            display.setCurrent(entryForm);
        }
    }

    public static VideoTest getInstance() {
        return instance;
    }

    /**
     * Called when this MIDlet is started for the first time,
     * or when it returns from paused mode.
     * If there is currently a Form or Canvas displaying
     * video, call its startApp() method.
     */
    public void startApp() {
        if (videoPlayer != null) {
            videoPlayer.startApp();
        }

        if (videoCanvas != null) {
            videoCanvas.startApp();
        }
    }

    /**
     * Called when this MIDlet is paused.
     * If there is currently a Form or Canvas displaying
     * video, call its startApp() method.
     */
    public void pauseApp() {
        if (videoPlayer != null) {
            videoPlayer.pauseApp();
        }

        if (videoCanvas != null) {
            videoCanvas.pauseApp();
        }
    }

    /**
     * Destroy must cleanup everything not handled
     * by the garbage collector.
     */
    public synchronized void destroyApp(boolean unconditional) {
        if (videoPlayer != null) {
            videoPlayer.close();
        }

        if (videoCanvas != null) {
            videoCanvas.close();
        }

        nullPlayer();
        handler.stop();
    }

    public synchronized void nullPlayer() {
        videoPlayer = null;
        videoCanvas = null;
    }

    public void run() {
        boolean reportError = true;

        if (usePlayer) {
            videoPlayer = new VideoPlayer(display);
            videoPlayer.addCommand(backCommand);
            videoPlayer.setCommandListener(this);
            videoPlayer.open(currURL);

            if (videoPlayer != null) {
                display.setCurrent(videoPlayer);
                videoPlayer.start();
                reportError = false;
            }
        } else {
            videoCanvas = new VideoCanvas(display);
            videoCanvas.addCommand(backCommand);
            videoCanvas.setCommandListener(this);
            videoCanvas.open(currURL);

            if (videoCanvas != null) {
                display.setCurrent(videoCanvas);
                videoCanvas.start();
                reportError = false;
            }
        }

        if (reportError) {
            Alert alert =
                new Alert("Can not play the media file",
                    "Check if the URL points to a supported media file", null, AlertType.ERROR);
            alert.setCommandListener(this);
            display.setCurrent(alert);
        }
    }

    /**
     * Respond to a request to display a URL.
     * @param url the url to display
     */
    public void newRequest(String url) {
        currURL = url;
        currTitle = url;
        new Thread(this).start();
    }

    /*
     * Respond to commands, including exit
     * On the exit command, cleanup and notify that the MIDlet has
     * been destroyed.
     */
    public void commandAction(Command c, Displayable s) {
        if (c == exitCommand) {
            synchronized (this) {
                if ((videoPlayer != null) || (videoCanvas != null)) {
                    new Thread(new Runnable() {
                            public void run() {
                                if (videoPlayer != null) {
                                    videoPlayer.stopVideoPlayer();
                                    videoPlayer = null;
                                } else { //videoCanvas != null
                                    videoCanvas.stopVideoCanvas();
                                    videoCanvas = null;
                                }

                                destroyApp(false);
                                notifyDestroyed();
                            }
                        }).start();
                } else {
                    destroyApp(false);
                    notifyDestroyed();
                }
            }
        } else if (c == playCommand) {
            synchronized (this) {
                if ((videoPlayer != null) || (videoCanvas != null)) {
                    return;
                }

                currURL = urlTextField.getString();
                currTitle = currURL;

                // need to start the players in a separate thread to
                // not block the command listener thread during
                // Player.realize: if it requires a security
                // dialog (like "is it OK to use airtime?"),
                // it would block the VM
                (new Thread(this)).start();
            }
        } else if ((c == backCommand) || (c == Alert.DISMISS_COMMAND)) {
            // Finish the Handler's request, if there was one
            handler.finish(true);

            if (videoPlayer != null) {
                videoPlayer.close();
            }

            if (videoCanvas != null) {
                videoCanvas.close();
            }

            nullPlayer();

            display.setCurrent(entryForm);
        }
    }
}
