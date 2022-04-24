/******************************************************************************/
/**
 *        @file        RetainModeMidlet.java
 *        @brief        A simple example of a retained mode M3G midlet
 *
 *        Copyright (C) 2004 Superscape plc
 *
 *        This file is intended for use as a code example, and
 *        may be used, modified, or distributed in source or
 *        object code form, without restriction, as long as
 *        this copyright notice is preserved.
 *
 *        The code and information is provided "as-is" without
 *        warranty of any kind, either expressed or implied.
 */

/******************************************************************************/
package com.superscape.m3g.wtksamples.retainedmode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.lang.IllegalArgumentException;

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.m3g.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;


public class RetainedModeMidlet extends MIDlet implements CommandListener {
    private Display myDisplay = null;
    private JesterCanvas myCanvas = null;
    private Timer myRefreshTimer = new Timer();
    private TimerTask myRefreshTask = null;
    private Command exitCommand = new Command("Exit", Command.ITEM, 1);
    Graphics3D myGraphics3D = Graphics3D.getInstance();
    World myWorld = null;
    private long worldStartTime = 0;
    private long lastPauseTime = 0;
    private boolean paused = false;
    int viewport_x;
    int viewport_y;
    int viewport_width;
    int viewport_height;
    int currentContent = 0;

    /**
     * RetainedModeMidlet - default constructor.
     */
    public RetainedModeMidlet() {
        super();

        // Set up the user interface.
        myDisplay = Display.getDisplay(this);
        myCanvas = new JesterCanvas(this);
        myCanvas.setCommandListener(this);
        myCanvas.addCommand(exitCommand);
    }

    /**
     * startApp()
     */
    public void startApp() throws MIDletStateChangeException {
        myDisplay.setCurrent(myCanvas);

        if (!paused) {
            // executed for the first time
            try {
                myWorld = (World)Loader.load(
                        "/com/superscape/m3g/wtksamples/retainedmode/content/swerve.m3g")[0];
                setupAspectRatio();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        worldStartTime = System.currentTimeMillis();
        paused = false;
        myCanvas.repaint();
    }

    /**
     * pauseApp()
     */
    public void pauseApp() {
        paused = true;
        lastPauseTime += (System.currentTimeMillis() - worldStartTime);
    }

    /**
     * destroyApp()
     */
    public void destroyApp(boolean unconditional) throws MIDletStateChangeException {
        myRefreshTimer.cancel();
        myRefreshTimer = null;
    }

    /**
     * Make sure that the content is rendered with the correct aspect ratio.
     */
    void setupAspectRatio() {
        viewport_x = 0;
        viewport_y = 0;
        viewport_width = myCanvas.getWidth();
        viewport_height = myCanvas.getHeight();

        Camera cam = myWorld.getActiveCamera();

        float[] params = new float[4];
        int type = cam.getProjection(params);

        if (type != Camera.GENERIC) {
            //calculate window aspect ratio
            float waspect = viewport_width / viewport_height;

            if (waspect < params[1]) {
                float height = viewport_width / params[1];
                viewport_height = (int)height;
                viewport_y = (myCanvas.getHeight() - viewport_height) / 2;
            } else {
                float width = viewport_height * params[1];
                viewport_width = (int)width;
                viewport_x = (myCanvas.getWidth() - viewport_width) / 2;
            }
        }
    }

    /**
     * MIDlet paint method.
     */
    public void paint(Graphics g) {
        if ((myCanvas == null) || (myGraphics3D == null) || (myWorld == null)) {
            return;
        }

        if ((g.getClipWidth() != viewport_width) || (g.getClipHeight() != viewport_height) ||
                (g.getClipX() != viewport_x) || (g.getClipY() != viewport_y)) {
            g.setColor(0x00);
            g.fillRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
        }

        // Delete any pending refresh tasks.
        if (myRefreshTask != null) {
            myRefreshTask.cancel();
            myRefreshTask = null;
        }

        if (paused) {
            // don't update the animation if paused
            myGraphics3D.bindTarget(g);
            myGraphics3D.setViewport(viewport_x, viewport_y, viewport_width, viewport_height);
            myGraphics3D.render(myWorld);
            myGraphics3D.releaseTarget();

            return;
        }

        // Update the world to the current time.
        long startTime = System.currentTimeMillis() - worldStartTime + lastPauseTime;

        if ((currentContent == 0) && (startTime > 5000)) {
            currentContent++;

            try {
                myWorld = (World)Loader.load(
                        "/com/superscape/m3g/wtksamples/retainedmode/content/skaterboy.m3g")[0];
                setupAspectRatio();
            } catch (Exception e) {
                e.printStackTrace();
            }

            g.setColor(0x00);
            g.fillRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
        }

        // update any animations in the world
        int validity = myWorld.animate((int)startTime);

        // render the 3d scene
        myGraphics3D.bindTarget(g);
        myGraphics3D.setViewport(viewport_x, viewport_y, viewport_width, viewport_height);
        myGraphics3D.render(myWorld);
        myGraphics3D.releaseTarget();

        if (validity < 1) { // The validity too small; allow a minimum of 1ms.
            validity = 1;
        }

        if (validity == 0x7fffffff) { // The validity is infinite; schedule a refresh in 1 second.
            myRefreshTask = new RefreshTask();
            myRefreshTimer.schedule(myRefreshTask, 1000);
        } else { // Schedule a refresh task.
            // Create a new refresh task.
            myRefreshTask = new RefreshTask();
            // Schedule an update.
            myRefreshTimer.schedule(myRefreshTask, validity);
        }
    }

    /**
     * Handle commands.
     */
    public void commandAction(Command cmd, Displayable disp) {
        if (cmd == exitCommand) {
            try {
                destroyApp(false);
                notifyDestroyed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Inner class for refreshing the view.
     */
    private class RefreshTask extends TimerTask {
        public void run() {
            // Get the canvas to repaint itself.
            myCanvas.repaint(viewport_x, viewport_y, viewport_width, viewport_height);
        }
    }

    /**
     * Inner class for handling the canvas.
     */
    class JesterCanvas extends Canvas {
        RetainedModeMidlet myTestlet;

        /**
         * Construct a new canvas
         */
        JesterCanvas(RetainedModeMidlet Testlet) {
            myTestlet = Testlet;
        }

        /**
         * Initialize self.
         */
        void init() {
        }

        /**
         * Cleanup and destroy.
         */
        void destroy() {
        }

        /*
         * Ask myTestlet to paint itself
         */
        protected void paint(Graphics g) {
            myTestlet.paint(g);
        }

        /*
         * Ask myTestlet to handle keyPressed events
         */
        protected void keyPressed(int keyCode) {
        }

        /*
         * Ask myTestlet to handle keyReleased events
         */
        protected void keyReleased(int keyCode) {
        }

        /*
         * Ask myTestlet to handle keyRepeated events
         */
        protected void keyRepeated(int keyCode) {
        }

        /*
         * Ask myTestlet to handle pointerDragged events
         */
        protected void pointerDragged(int x, int y) {
        }

        /*
         * Ask myTestlet to handle pointerPressed events
         */
        protected void pointerPressed(int x, int y) {
        }

        /*
         * Ask myTestlet to handle pointerReleased events
         */
        protected void pointerReleased(int x, int y) {
        }
    }
}
