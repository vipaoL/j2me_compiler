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

import java.util.Calendar;
import java.util.Date;

import javax.microedition.lcdui.Image;

import samples.ui.ChatBox;
import samples.ui.CustomFont;
import samples.ui.Label;
import samples.ui.ResourceManager;
import samples.ui.TextBox;
import samples.ui.TextField;

import com.nokia.sm.net.ItemList;

/**
 * This class implements chat window functionality 
 *
 */
public class Chat extends CommunityView {
    static final int PURPLE = 0x8800A8;
    static final int GRAY   = 0x606060;

    private static CustomFont CHAT_FONT;
    //private static Font CHAT_FONT;
    private static Image headerImage;
    private static Calendar calendar;
    private static String test_font= "Font1";

    private Label header;
    private ChatBox chatBox;
    private TextField textField;
    private BuddyList buddyList;
    private String buddyname;

    /**
     * initialize UI components.
     */
    public static void initialize() {
        //CHAT_FONT = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
    	CHAT_FONT = new CustomFont(test_font);
        headerImage = ResourceManager.getImage("/slim_button_off.png");
        calendar = Calendar.getInstance();
    }

    /**
     * New Chat instance creation with UI layout. 
     * @param community 
     * @param name User name
     * @param buddyList List of Buddies 
     */
    public Chat(Community community, String name, BuddyList buddyList) {
        super(community, name);

        this.buddyList = buddyList;

        setLeftSoftButton(Community.SEND);
        setRightSoftButton(Community.BACK);
        setBackgoundImage(Community.FADING_BACKGROUND);

        buddyList.setLocation(getWidth() - buddyList.getWidth() - 2, 0);
        add(buddyList);

        header = new Label("", false);
        header.setBackgroundImage(new Image[] {headerImage});
        header.setDrawShadows(false);
        header.setDimension(headerImage.getWidth(), headerImage.getHeight());
        header.setLocation((getWidth() - header.getWidth()) / 2, 23);

        add(header);

        chatBox = new ChatBox(25);
        chatBox.setDrawBorders(false);
        chatBox.setDrawShadows(false);
        chatBox.setFont(CHAT_FONT);
        chatBox.setLocation(header.getX(), header.getY() + header.getHeight());
        chatBox.setDimension(header.getWidth(), 7 * chatBox.getFont().getHeight() + TextBox.WIDTH_OFFSET);

        add(chatBox);

        textField = new TextField(25);
        textField.setDrawShadows(false);
        textField.setForeground(PURPLE);
        textField.setFont(LoginView.TEXTFIELD_FONT);
        textField.setFontColor(GRAY);
        textField.setLocation(chatBox.getX(), chatBox.getY() + chatBox.getHeight());
        textField.setDimension(header.getWidth(), 20);

        add(textField);

        setFocus(textField);
    }

    /**
     * Buddyname set with the details like time, message text 
     * @param buddyname Buddy Name
     */
    public void setBuddyname(String buddyname) {
        String msg, amPm, hour, minute;

        this.buddyname = buddyname;

        msg = buddyList.getChat(buddyname);
        if (msg != null) {
            chatBox.addEntry("[" + buddyname + "] " + msg, PURPLE);
        }

        calendar.setTime(new Date(System.currentTimeMillis()));
        hour = "" + calendar.get(Calendar.HOUR);
        minute = "" + calendar.get(Calendar.MINUTE);
        if (minute.length() < 2) minute = "0" + minute;
        amPm = calendar.get(Calendar.AM_PM) == Calendar.AM ? "am" : "pm";

        header.setText(buddyname + " -- " + hour + ":" + minute + " " + amPm);
    }

    public void addBuddyMessage(String from, String message) {
        chatBox.addEntry("[" + from + "] " + message, PURPLE);
        repaint();
    }

    /**
     * Action taken on left Soft button pressend : Send Message to Buddy
     * @param label Label of Left Soft Button
     */
    public void leftSoftButtonPressed(String label) {
        ItemList il;

        Buddy buddy = buddyList.get(buddyname);
        if (buddy == null) return;
        
        int buddyStatus = buddy.getStatus();
        Integer buddyGcid = buddy.getGcid();
        
        boolean available = true;
        if (buddyGcid != null && buddyGcid.equals( community.getGCID())) {
        	available = (buddyStatus!=Buddy.OFFLINE);
        } else {
        	available = (buddyStatus==Buddy.ONLINE_AVAILABLE);
        }

/*
        System.out.println("Send Chat! ----- BuddyInfo: ");
        System.out.println(" name: " + buddyname);
        System.out.println(" msg: " + textField.getText());
        System.out.println(" status: " + buddyStatus);
        System.out.println(" available: " + available);
        System.out.println(" gcid: " + buddyGcid);
*/
        
        // If buddy still available for chat, send message
        if (available) {
        	if(textField.getText().length()>0) {
	        	il = new ItemList();
		        il.setItem("cmd", "sendBuddyMessage");
		        il.setItem("name", buddyname);
		        il.setItem("msg", textField.getText());
		        community.executeCmd(il);
		        
		        chatBox.addEntry("[" + community.getUsername() + "] " + textField.getText(), GRAY);
		        textField.setText("");
		        repaint();
        	} else {
        		community.showError( "Please enter a text to send");
        	}
	    // If buddy unavailable, display warning dialog.
        } else {
        	community.showError( buddyname + " is no longer available for chat");
        }
    }

    /**
     * Return to previous screen if Right Soft Button pressed 
     * @param label Right Soft Button label
     */
    public void rightSoftButtonPressed(String label) {
        community.switchToView( Community.BACK);
    }
}