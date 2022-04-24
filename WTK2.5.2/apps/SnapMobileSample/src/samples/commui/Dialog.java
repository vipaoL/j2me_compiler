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
 * Implements a full-screen multi-line text or data input dialog, with 
 * configurable softkeys (labels and targets.)
 * 
 */
public class Dialog extends CommunityView implements EventListener {
    
    private String targetView = null;

    private static Image[] IMAGE_LIST;
    private static Image textOff;

    protected EventListener listener;
    private Component comp;
    private Object arg;
    private Button button1;
    private int type;
    
    static public final int	ALERT			= 1;
    static public final int ALERT_LOGOUT	= 2;
    static public final int ALERT_FATAL		= 3;
    static public final int DATA_ENTRY		= 4;
    static public final int YES_NO			= 5;
    static public final int OK_CANCEL		= 6;
    
    public static void initialize() {
        textOff = ResourceManager.getImage("/text_off.png");

        IMAGE_LIST = new Image[] {
            ResourceManager.getImage("/short_button_on.png"),
            ResourceManager.getImage("/short_button_off.png"),
            ResourceManager.getImage("/short_button_pressed.png")
        };
    }

    /**
     * Creates and sets up widgets and softkeys for dialog.
     *  
	 * @param community <code>Community</code> main instance
	 * @param name Name of screen (used for inter-screen navigation by <SnapLogin>)
     * @param listener Eventlistener
     * @param left Target view visited when left softkey pressed
     * @param right Target view visited when right softkey pressed
     * @param type Body of the message to display in the dialog
     *
    */
    public Dialog(Community community, String name, EventListener listener, String left, String right, Component comp, Object arg, int type, String target) {
        super(community, name);

        this.comp = comp;
        this.arg = arg;
        this.type = type;
        this.targetView = target;
        this.listener = listener;
        
        Button button2;
        Label label;

        showTitle = false;
        setLeftSoftButton(left);
        setRightSoftButton(right);
        setBackgoundImage(Community.OVERLAY_BACKGROUND);

        textOff = ResourceManager.getImage("/text_off.png");
        label = new Label(name, true);
        label.setBackgroundImage(new Image[] {textOff});
        label.setDimension(textOff.getWidth(), textOff.getHeight());
        // Center the dialog contents relative to the the background image
        label.setLocation(
                (getWidth() - label.getWidth()) / 2,
                ((getHeight() - getBackgroundImage().getHeight()) / 2) + 7);
        add(label);

        comp.setLocation(label.getX(), label.getY() + label.getHeight());
        comp.setDimension(label.getWidth(), comp.getHeight());
        if (comp instanceof TextField) setFocus(comp);
        add(comp);        
    }
    
    public int getType() {
    	return type;
    }

    public Object getArg() {
        return arg;
    }

    public Component getComponent() {
        return comp;
    }
    
    /**
     * EventHandler 
     * @param e Event to handle
     */

    public boolean handleEvent(Event e) {
        //System.out.println("Dilaog.handleEvent(), source = " + e.getSource());

        if (e.getType() == Event.ITEM_DESELECTED) {
        	if (targetView==null) {
        		return listener.handleEvent(new Event(this, e.getType(), e.getValue()));
            } else {
           		community.switchToView( targetView);
           		return true;
           	}
        }
        return false;
    }
    
    /**
     * If there is a <code>leftTarget</code> set, visits the 
     * <code>View</code> with that name.
     */
    public void leftSoftButtonPressed(String label) {
        //System.out.println("Dilaog.leftSoftButtonPressed(): " + label);

       	if (targetView==null) {
       		if (leftSoft != null) {
       			listener.handleEvent(new Event(this, Event.ITEM_DESELECTED, leftSoft));
       		}
        } else {
       		community.switchToView( targetView);
       	}
     }

    /**
     * If there is a <code>rightTarget</code> set, visits the 
     * <code>View</code> with that name.
     */
    public void rightSoftButtonPressed(String label) {
        //System.out.println("Dilaog.rightSoftButtonPressed(): " + label);
    	if (label == null) return;
    	// If listener doesn't handle it, treat it as a "BACK" press.
    	// (A "no" could mean either: take a particular action and go back,
    	// or else take no action and go back... it's up to the listener...
    	// if the listener doesn't handle, we assume it's just a "back").
    	if (!listener.handleEvent(new Event(this, Event.ITEM_DESELECTED, rightSoft))) {
			if (label.equals(Community.CANCEL) || label.equals(Community.BACK)
					|| label.equals(Community.NO)) 
			{
	    		community.switchToView( Community.BACK);
			}
    	}
    }
}