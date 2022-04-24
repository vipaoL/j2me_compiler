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

package samples.commui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Image;
import samples.ui.*;

/**
 * This class implements a splash screen/image to display the Game image or the
 * Message of the Day
 * extends CommunityView 
 *
 */
public class SplashView extends CommunityView implements Runnable {
    private String next;
    private int delay;
    private EventListener listener;

    /**
     * initialize the instance of the class 
     * @param community Community object
     * @param name Name of View 
     * @param listener
     * @param img Image 
     * @param next 
     * @param delay Time delay for display
     */
    public SplashView(Community community, String name, EventListener listener, Image img, String next, int delay) {
        super(community, name);

        this.next = next;
        this.delay = delay;
        this.listener = listener;

        showTitle = false;
        setBackgoundImage(img);

        if (delay > 0) {
            try {
                Thread thread = new Thread(this);
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        Thread.yield();

        try {Thread.sleep(delay);}
        catch (InterruptedException e) {}

        setNextView();
    }

    private synchronized void setNextView() {
        if (active) community.switchToView(next);
    }

    public void keyReleased(int key) {

    	setNextView();    
    }
}