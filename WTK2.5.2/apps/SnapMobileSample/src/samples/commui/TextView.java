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

import java.io.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import samples.ui.*;

/**
 * Class that implements all text related GUI component layout.
 *
 */
public class TextView extends CommunityView implements Runnable {
    private ScrollView scrollView;
    private ScrollableComponent scrollable;
    private TextBox textBox;
    private BuddyList buddyList;
    private EventListener listener;
    private String textOrURL;
    private String next;

    static Image upArrow;
    static Image downArrow;
    static Image[] bar;

    public static void initialize() {
        upArrow = ResourceManager.getImage("/up_arrow.png");
        downArrow = ResourceManager.getImage("/down_arrow.png");

        bar = new Image[] {
            ResourceManager.getImage("/bar_top.png"),
            ResourceManager.getImage("/bar_template.png"),
            ResourceManager.getImage("/bar_bottom.png"),
        };
    }

    /**
     * 	
	 * Sets up widget appearance and layout.
	 * 
     * @param community <code>Community</code> main instance
     * @param name Name of screen
     * @param buddyList List of Buddies
     * @param listener
     * @param next
     * @param left
     * @param right
     * @param textOrURL
     * @param isURL
     */
    public TextView(Community community, String name, BuddyList buddyList, EventListener listener,
        String next, String left, String right, String textOrURL, boolean isURL)
    {
        super(community, name);

        int tfh, sfh;
        this.listener = listener;
        this.textOrURL = textOrURL;
        this.next = next;
        this.buddyList = buddyList;

        setLeftSoftButton(left);
        setRightSoftButton(right);
        setBackgoundImage(Community.FADING_BACKGROUND);

        textBox = new TextBox();
        textBox.setText(isURL ? "Retrieving message. Hang on..." : textOrURL);
        textBox.setBackground(0x00ffffff);
        textBox.setForeground(0x00c0c0c0);

        tfh = textBox.getFont().getHeight();
        sfh = SOFT_BUTTON_FONT.getHeight();

        scrollView = new ScrollView(upArrow, downArrow, bar, textBox);
        scrollView.setLocation(5, 20);
        scrollView.setDimension(getWidth() - 10, (getHeight() - textBox.getY() - (sfh + 15)) / tfh  * tfh + TextBox.WIDTH_OFFSET);

        add(scrollView);

        if (buddyList != null && community.isLoggedIn()) {
            buddyList.setLocation(getWidth() - buddyList.getWidth() - 2, 0);
            add(buddyList);
        }

        if (isURL) {
            //System.out.println("starting textview thread");
            try {
                Thread thread = new Thread(this);
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        String text = "Could not get message :(";

        try {
            int index;
            String name;

            // If the text starts with a directory slash, assume it points to
            // a resource within our .jar.  If it starts with http://, assume
            // it's a URL, but we'll still treat it as a local resource 
            // (remove the "http:/", then look for the remaineder of the string
            // as a resource location within our .jar).  Load the file from
            // the jar and display as our text.
            if (textOrURL.startsWith("/") || textOrURL.startsWith("http://")) {
	            index = textOrURL.lastIndexOf('/');
	            
	            name = textOrURL.substring(index);
	
	            text = new String(ResourceManager.getResource(name));
	            
            // If neither, assume the text passed in is the actual body of the
            // text to be displayed, and display it.
            } else {
            	text = textOrURL;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        textBox.setText(text);
    }

    public void leftSoftButtonPressed(String label) {
        if (label.equals( Community.OK)) 
        	community.switchToView(next);
    }

    public void rightSoftButtonPressed(String label) {
    	if (label==null) return;
        if (label.equals( Community.EXIT)) 
        	community.switchToView( Community.EXIT);
        else if (label.equals( Community.BACK)) 
        	community.switchToView(Community.BACK);
        else 
        	listener.handleEvent(new Event(this, Event.ITEM_SELECTED, label));
    }
}