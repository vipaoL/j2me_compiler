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
import samples.ui.CustomFont;

/**
 * This class implements all Buddy related UI components and layouts.
 * It lists buddies, their presence states and pop-up for 'chat', 'challenge'
 *  or 'remove' buddies.
 *
 */
public class BuddyView extends CommunityView implements EventListener {
    private static CustomFont TABLE_FONT;
   // private static Font TABLE_FONT;
    private static int[] SEPARATOR_LIST;
    private static String[] POPUP_NAME_LIST;
    private static String test_font = "Font1";

    private static Image slimOff;
    private static Image slimOn;
    private static Image popupOn;
    private static Image buddyChat;
    private static Image buddyOn;
    private static Image buddyOff;
    private static Image buddyOn_Unavail;

    private Button button;
    private Table table;
    private Table popupAvail;
    private Table popupUnavail;
    private Table popupChat;
    
    private BuddyList buddyList;

   /**
    * UI Initiliazer method for presence and challenge modes.
    */
    public static void initialize() {
        //TABLE_FONT = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
    	TABLE_FONT = new CustomFont(test_font);
        SEPARATOR_LIST = new int[] {0, 25};
        POPUP_NAME_LIST = new String[] {"chat messages", "challenge", "remove"};

        slimOff = ResourceManager.getImage("/slim_button_off.png");
        slimOn = ResourceManager.getImage("/slim_button_on.png");
        popupOn = ResourceManager.getImage("/popup_on.png");
        buddyChat = ResourceManager.getImage("/presence_chat.png");
        buddyOn = ResourceManager.getImage("/presence_on.png");
        buddyOff = ResourceManager.getImage("/presence_off.png");
        buddyOn_Unavail = ResourceManager.getImage("/presence_icon_on_unavail_2.png");
    }

    /**
     * Gets presence icon associated with the buddy. 
     * @param buddy Buddy object
     * @return Image icon of the presence of the buddy
     */
    private Image getPresenceIcon(Buddy buddy) { 
        if (buddy.getStatus() == Buddy.OFFLINE) 
        	return buddyOff;
        else if (buddy.getStatus() == Buddy.ONLINE_UNAVAILABLE) 
       		return buddyOn_Unavail;
        else if (buddy.getMessage() == null) 
        	return buddyOn;
        else 
        	return buddyChat;
    }

    /**
     * BuddyView creates BuddyList and reperesnts in a Table format.All the UI
     * layout for buddylist view is done here. 
     * @param community Main Community object
     * @param name Name of this View
     * @param buddyList Buddy List
     */
    public BuddyView(Community community, String name, BuddyList buddyList) {
        super(community, name);

        TableRow row;

        setLeftSoftButton(Community.SELECT);
        setRightSoftButton(Community.BACK);
        setBackgoundImage(Community.FADING_BACKGROUND);

        this.buddyList = buddyList;
        buddyList.setLocation(getWidth() - buddyList.getWidth() - 2, 0);
        buddyList.addEventListener(this);
        add(buddyList);
        
        row = new TableRow(new Object[] {"", "User Name"}, SEPARATOR_LIST);
        row.setFont(TABLE_FONT);
        row.setDrawSeparators(true);
        row.setBackgroundImage(new Image[] {slimOff});
        row.setForeground(0x00c0c0c0);
        row.setDimension(slimOff.getWidth(), slimOff.getHeight());

        table = new Table(row);
        table.addEventListener(this);
        table.setDrawBorders(false);
        table.setDrawShadows(false);
        table.setWindowSize(7);
        table.setDimension(row.getWidth(), 0);
        table.setLocation((getWidth() - table.getWidth()) / 2, 23);

        add(table);
        
        button = new Button("add friend");
        button.setBackgroundImage(new Image[] {slimOff, slimOn});
        button.setDrawShadows(false);
        button.setLocation(table.getX(), table.getY() + table.getHeight());
        button.setDimension(slimOff.getWidth(), slimOff.getHeight());
        button.addEventListener(this);

        add(button);

        TableRow row1 = createPopupRow( POPUP_NAME_LIST[0]);
        TableRow row2 = createPopupRow( POPUP_NAME_LIST[1]);
        TableRow row3 = createPopupRow( POPUP_NAME_LIST[2]);

        // Popup shown if buddy is available for chat and/or game challenge
        popupAvail = createPopupTable( 3);
        popupAvail.add( row1);
        popupAvail.add( row2);
        popupAvail.add( row3);
        
        // Popup shown if buddy is unavailable for chat or game challenge
        popupUnavail = createPopupTable( 1);
        popupUnavail.add( row3);

	    // Popup shown if buddy is available only for chat
        popupChat = createPopupTable( 2);
        popupChat.add( row1);
        popupChat.add( row3);

        populateTable();
    }

    public void setActive(boolean active) {
    	super.setActive( active);
    	table.setFocus(false);
    	this.setFocus( button);
    }

    
    private Table createPopupTable( int windowSize)
    {
        Table popup = new Table(popupOn.getHeight(), true);
        popup.addEventListener(this);
        popup.setDimension(popupOn.getWidth(), popupOn.getHeight());
        popup.setWindowSize(windowSize);
        return popup;
    }
    
    private TableRow createPopupRow( String title)
    {
    	TableRow row = new TableRow(new Object[] {title}, new int[] {0});
        row.setFont(TABLE_FONT);
        row.setBackgroundImage(new Image[] {null, popupOn});
        row.setDimension(popupOn.getWidth(), popupOn.getHeight());
        return row;
    }
   /**
    * This method populates the BuddyView Table with presence icon & name 
    *
    */
    private void populateTable() {
        TableRow row;
        Buddy buddy;

        table.clear();

        for (int i=0; i<buddyList.size(); i++) {
            buddy = buddyList.get(i);

            row = new TableRow(new Object[] {getPresenceIcon(buddy), buddy.getName()}, SEPARATOR_LIST);
            row.setFont(TABLE_FONT);
            row.setBackgroundImage(new Image[] {null, slimOn});
            row.setDimension(slimOn.getWidth(), slimOn.getHeight());

            table.add(row);
        }

        button.setLocation(table.getX(), table.getY() + table.getHeight());
    }

    /**
     * Popup when a buddy is clicked.
     *
     */
    private void showPopup() {
        int popupIndx;
        Buddy buddy;
        Table popup = null;

        if (table.size() <= 0) return;

        buddy = buddyList.get( table.getCursor());
        switch (buddy.getStatus()) {
        case Buddy.OFFLINE: 
        case Buddy.ONLINE_UNAVAILABLE:
        	popup = popupUnavail; 
        	break;
        case Buddy.ONLINE_AVAILABLE:
        	// In same game -- can challenge and chat
        	if (community.getGCID().equals(buddy.getGcid())) {
        		popup = popupAvail;
        	// In different game -- can only chat
        	} else { 
        		popup = popupChat;
        	}
        	break;
        }
        
        if (!contains(popup) && table.size() > 0) {
            popupIndx = table.getCursor() - table.getWindowStart() + 1;
            int popupY = table.getY() + popupIndx * slimOn.getHeight() + 4;
            int screenMax = getCanvas().getHeight() - 20;
            int overshoot = (popupY+popup.getHeight()) - screenMax;
            if (overshoot > 0) popupY -= overshoot;
            popup.setLocation( 50, popupY);
            add(popup);
            setFocus(popup);
            setRightSoftButton("cancel");
        }
    }

	/** EventListener callback */
    public boolean handleEvent(Event e) {
        if (e.getType() == Event.ITEM_DESELECTED) {
            if (e.getSource() == buddyList) populateTable();

            if (e.getSource() == table) showPopup();

            if (e.getSource() == button) {
            	
                TextField textField = new TextField(15);
                textField.setText("");
                textField.setEntryMode( TextField.ENTRY_USERNAME);
                textField.setBackground(0x00ffffff);
                textField.setForeground(0x00c0c0c0);
                textField.setFont(LoginView.TEXTFIELD_FONT);
                textField.setFont(TABLE_FONT);
                textField.setDimension(getWidth(), 20);

                community.showDialog( 
                		"Add Friend",  
                		textField, 
                		null,
                		null,
                		Dialog.DATA_ENTRY
                		);
            }

            if (e.getSource() == popupAvail || 
            	e.getSource() == popupUnavail || 
            	e.getSource() == popupChat ) 
            {
                Table popup = (Table)e.getSource();
                String cmd = (String)(popup.getElement(popup.getCursor(), 0));
                String buddyName = (String)table.getElement(table.getCursor(), 1);
                
                popup.setFocus(false);
                remove( popup);
                setRightSoftButton(Community.BACK);

                if (cmd.equals("chat messages")) {
                    Chat chatView = (Chat)community.getView(Community.CHAT);
                    chatView.setBuddyname( buddyName);
                    community.switchToView(chatView, true);

            	} else if (cmd.equals("remove")) {
                    community.showDialog(
                    		"Remove Friend", "Are you sure you want to remove '" + buddyName + "'?", 
                    		buddyName, null, Dialog.YES_NO
                    		);

            	} else if (cmd.equals("challenge")) {
                    community.handleChallenge(buddyName, community.getRandomGameRoomName(), 1);
                }
            }
        }
        return true;
    }

    /**
     * If left soft button is pressed Showpop()
     * @param label Label of the left soft button
     */
    public void leftSoftButtonPressed(String label) {
    	Component comp = getFocus();
		if (comp == table)	{
			showPopup();
		} else {
            comp.keyPressed(Canvas.FIRE, ENTER_BUTTON);
            comp.keyReleased(Canvas.FIRE, ENTER_BUTTON);
		}
    }

   /**
    * Action to take if  right soft button is pressed
    * @param label Label of the right soft button
    */
    public void rightSoftButtonPressed(String label) {
        if (label.equals(Community.BACK)) {
            community.switchToView(Community.BACK);
        }
        else if (label.equals(Community.CANCEL)) {
            table.setFocus(false);
            remove(popupAvail);
            remove(popupUnavail);
            remove(popupChat);
            setRightSoftButton(Community.BACK);
        }
    }
}