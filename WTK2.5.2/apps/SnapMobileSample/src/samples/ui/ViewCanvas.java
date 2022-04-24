/* ------------------------------------------------------------------------- *
          Copyright 2004-2005 Nokia Corporation  All rights reserved.
          Nokia Mobile Phones

          Restricted Rights: Use, duplication, or disclosure by the
          U.S. Government is subject to restrictions as set forth in
          subparagraph (c)(1)(ii) of DFARS 252.227-7013, or in FAR
          52.227-19, or in FAR 52.227-14 Alt. III, as applicable.

          This software is proprietary to and embodies the
          confidential technology of Nokia Possession, use, or copying
          of this software and media is authorized only pursuant to a
          valid written license from Nokia or an authorized
          sublicensor.

          Nokia  - Wireless Software Solutions
 * ------------------------------------------------------------------------- */

package samples.ui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

/**
 * This class implements a full-screen canvas with
 * multiple views. Calling <code>Canvas.setFullScreenMode()</code>
 * and <code>Display.setCurrent()</code> are potentially time-consuming
 * operations. We deal with this problem by introducing the concept of
 * views. Applications create an instance of this class and set it as
 * the current <code>Displayable</code>. When a new screen needs to be
 * drawn, the application calls the <code>setView()</code> method. This
 * call incurs to timing penalty, and lets the application switch screens
 * easily and quickly.
 */
public class ViewCanvas extends Canvas {
    private View view;
    private int ow;
    private int oh;

    /**
     * Create a new instance with the provide View object.
     *
     */
    public ViewCanvas() {
        ow = getWidth();
        oh = getHeight();

        setFullScreenMode(true);
    }

    /**
     * Create a new instance with the provide View object.
     *
     * @param view The View object associated with this instance.
     */
    public ViewCanvas(View view) {
        this();

        setView(view);
    }

    public void waitForResize() {
        for (int i=0; i<8; i++) {
            if (getWidth() != ow || getHeight() != oh) break;

            try {Thread.sleep(250);}
            catch (InterruptedException e) {}
        }
    }

    /**
     * Set the View object associated with this instance.
     *
     * @param view The View object associated with this instance.
     */
    public void setView( View view) {
        this.view = view;
        repaint();
    }

    /**
     * Get the View object associated with this instance.
     *
     * @return The View object associated with this instance.
     */
    public View getView() {
        return view;
    }

    /**
     * Paint this instance.
     *
     * @param g The Graphics object to use for painting operations.
     */
    public void paint(Graphics g) {
        view.paint(g);
    }

    /**
     * Update the state of this instance in light of a key press action.
     *
     * @param key The key pressed.
     */
    public void keyPressed(int key) {
        view.keyPressed(key);
    }

    /**
     * Update the state of this instance in light of a key release action.
     *
     * @param key The key released.
     */
    public void keyReleased(int key) {
        view.keyReleased(key);
    }
}

