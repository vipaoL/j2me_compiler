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

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;

/** CommUI Main Test Midlet */

public class CommUITest extends MIDlet implements MainApp {
    private boolean paused;
    private Community community;

    /**
     * Creates a new <code>Community</code> instance, which immediately
     * takes control of the MIDlet user interface and presents the user
     * with LoginView and Registration options.  Control is returned back to
     * the MIDlet in the <code>handleEvent()</code> method, once the user
     * has finished logging in and/or creating a new account.
     */
    public void startApp() {
        if (!paused && community == null) {
        	community = new Community(this);
        } else {
        	community.resume();
        }
        paused = false;
    }
    /** 
     * Called by the handset when the MIDlet is paused.
     */
    public void pauseApp() {
        paused = true;
        if (community != null) community.pause();
    }

    /**
     * Destroys and exits MIDlet.
     */
    public void destroyApp(boolean unc) {
        Display.getDisplay(this).setCurrent(null);
        notifyDestroyed();
    }

    /**
     * Returns MIDlet reference.  Method inherited from interface
     * <code>com.nokia.sm.loginui.MainApp</code>.
     * 
     * @return MIDlet reference
     */
    public MIDlet getMIDlet() {
        return this;
    }

    /**
     * Returns MIDlet property.  Method inherited from interface
     * <code>com.nokia.sm.loginui.MainApp</code>.
     * 
     * @param string Name of property
     * @return Value of property
     */
    public String getProperty(String string) {
        return getAppProperty(string);
    }

    /**
     * Exits MIDlet.  Method inherited from interface
     * <code>com.nokia.sm.loginui.MainApp</code>.
     */
    public void exit() {
        destroyApp(true);
    }
}