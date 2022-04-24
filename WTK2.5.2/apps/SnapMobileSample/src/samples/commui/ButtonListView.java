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
import samples.ui.*;

/**
 * Class to define the layout of the Button List 
 */
public class ButtonListView extends CommunityView implements EventListener {
    public static Image[] IMAGE_LIST;
    public static int[] COLOR_LIST;
    public static CustomFont BUTTON_FONT;
   // public static Font BUTTON_FONT;
    

    private static final int VERTICAL_GAP = 10;

    private ButtonListListener listener;
    private Button[] buttonList;
    private static String test_font="Font1";

    /**
     * iitialize buttons with fonts and images from resources
     */
    public static void initialize() {
        //BUTTON_FONT = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE);
    	BUTTON_FONT = new CustomFont(test_font);

        IMAGE_LIST = new Image[] {
            ResourceManager.getImage("/button_on.png"),
            ResourceManager.getImage("/button_off.png"),
            ResourceManager.getImage("/button_pressed.png")
        };

        COLOR_LIST = new int[] {0x001e64b4, 0x00787d84, 0x00ffffff};
    }

    /**
     * Sets up widget appearance and layout.
     * @param community Main <code>Community</code> instance
     * @param name Name of view (used for navigation)
     * @param listener 
     * @param buddyList
     * @param buttonNameList
     * @param leftSoft
     * @param rightSoft
     */
    public ButtonListView(Community community, String name, ButtonListListener listener,
        BuddyList buddyList, String[] buttonNameList, String leftSoft, String rightSoft)
    {
        super(community, name);

        int i, y, height;

        this.listener = listener;

        setBackgoundImage(Community.FADING_BACKGROUND);
        setLeftSoftButton( leftSoft);
        setRightSoftButton( rightSoft);

        if (buddyList != null && community.isLoggedIn()) {
            buddyList.setLocation(getWidth() - buddyList.getWidth() - 2, 0);
            add(buddyList);
        }

        i = buttonNameList.length;
        height = i * IMAGE_LIST[0].getHeight() + (i - 1) * VERTICAL_GAP;
        y = getHeight() / 2 - height / 2;

        buttonList = new Button[buttonNameList.length];
        for (i=0; i<buttonList.length; i++) {
            buttonList[i] = new Button(buttonNameList[i]);
            buttonList[i].setFont(BUTTON_FONT);
            buttonList[i].setStateData(IMAGE_LIST, COLOR_LIST);
            buttonList[i].addEventListener(this);
            buttonList[i].setDimension(
                IMAGE_LIST[0].getWidth(),
                IMAGE_LIST[0].getHeight()
            );
            buttonList[i].setLocation((getWidth() - buttonList[i].getWidth()) / 2, y);

            add(buttonList[i]);

            y += IMAGE_LIST[0].getHeight() + VERTICAL_GAP;
        }
    }

    /** 
     * Handles "SELECTED" events for pushbuttons:
     * 
     */
    public boolean handleEvent(Event e) {
        Button button = (Button)e.getSource();
        //System.out.println("ButtonListView.handleEvent(), button = " + button.getText());

        if (e.getType() == Event.ITEM_DESELECTED) {
            listener.buttonPressed(getName(), button.getText());
            return true;
        }
        return false;
    }

    public void leftSoftButtonPressed(String label) {
        if (label.equals(Community.SELECT)) {
            keyPressed(ENTER_BUTTON);
            keyReleased(ENTER_BUTTON);
        }
    }

    public void rightSoftButtonPressed(String label) {
    	community.switchToView( label);
    }
}