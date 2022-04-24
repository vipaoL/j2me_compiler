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

import java.util.Vector;
import javax.microedition.lcdui.*;
import com.nokia.sm.net.ItemList;
import com.nokia.sm.net.SnapEventListener;

import samples.ui.*;

/**
 * This class implements all Rankings related UI components and layout.
 *
 */
public class RankingView extends CommunityView implements AsyncCommandListener, EventListener {
    private static CustomFont TABLE_FONT;
//	private static Font TABLE_FONT;
    private static int[] SEPARATOR_LIST;

    private static Image slimOff;
    private static Image slimOn;

    private Component header;
    private Table table;
    
    private static String test_font = "Font1";

	/**
	 * Intiliaze UI layout 
	 *
	 */
    public static void initialize() {
//        TABLE_FONT = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
    	TABLE_FONT = new CustomFont(test_font);
        SEPARATOR_LIST = new int[] {0, 30, 125};

        slimOff = ResourceManager.getImage("/slim_button_off.png");
        slimOn = ResourceManager.getImage("/slim_button_on.png");
    }
    
	/**
	 * Setup widget appearance and layout.
	 * 
	 * @param community Community main instance
	 * @param name Name of Screen
	 * @param buddyList List of Buddies
	 * @param header
	 * @param nameList
	 */

    public RankingView(Community community, String name, BuddyList buddyList, Component header, String[] nameList) {
        super(community, name);

        this.header = header;
        header.addEventListener(this);

        int[] sepList;
        TableRow row;

        setLeftSoftButton( "    ");
        setRightSoftButton( Community.BACK);
        setBackgoundImage( Community.FADING_BACKGROUND);

        buddyList.setLocation(getWidth() - buddyList.getWidth() - 2, 0);
        add(buddyList);

        // right-align the header to the table
        header.setLocation(((getWidth() + slimOff.getWidth()) / 2) - header.getWidth(), 23);
        header.addEventListener(this);

        //System.out.println("header.getX() = " + header.getX());
        //System.out.println("header.getY() = " + header.getY());
        //System.out.println("header.getWidth() = " + header.getWidth());
        //System.out.println("header.getHeight() = " + header.getHeight());

        add(header);

        row = new TableRow(nameList, SEPARATOR_LIST);
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
        table.setLocation((getWidth() - table.getWidth()) / 2, header.getY() + header.getHeight() - 1);

        add(table);
    }
    
    /**
 	 * This method is called when a view becomes active,or inactive. If active,
 	 * set item to PlayerStatistics or topRankings.
 	 * 
 	 * @param active  Whether not this instance is active
 	 */
    public void setActive(boolean active) {
        if (active) {
            setWaiting(true);

            ItemList itemList = new ItemList();

            if (getName().equals(Community.MY_RANKINGS)) {
                itemList.setItem("cmd", "playerStatistics");
            } else {
                // assume this is a STATS ranking screen
                itemList.setItem("cmd", "topRankings");
                itemList.setItem("stat", getName());
                itemList.setItem("count", new Integer(table.getWindowSize() * 5));
            }

            itemList.setItem("gcid", community.getGCID());
            itemList.setItem("listener", this);

            community.executeCmd(itemList);
        }

        super.setActive(active);
    }

    /**
 	 * Handle results of SNAP server requests. If error, show error message. If
 	 * successful, user is logged in.
 	 * 
 	 * @param cmd  Name of command attempted
 	 * @param errorMessage Text of error message, if there is an error (null if not)
 	 * @param results Results of command, if successful
 	 */
    public boolean commandCompleted(String cmd, String errorMessage, int errorSeverity, ItemList results) {
        int i, rank, score;
        String idName, id;
        TableRow row;
        Vector list;
        ItemList il;

        //System.out.println("RankingView.commandCompleted(): " + cmd);

        setWaiting(false);

        //-------------------------------
        // Command success
        if (errorMessage == null) {
        	
            if (cmd.equals("playerStatistics")) {
                list = results.getList("statsList");
                idName = "stat";
            } else {
                list = results.getList("rankingList");
                idName = "userName";
            }

            //System.out.println("list.size(): " + list.size());
            table.clear();

            // If no data, display a placeholder message
            if (list.size() == 0) {
                row = new TableRow(new Object[] {"", "No data yet!", ""}, SEPARATOR_LIST);
                row.setFont(TABLE_FONT);
                row.setBackgroundImage(new Image[] {null, slimOn});
                row.setDimension(slimOn.getWidth(), slimOn.getHeight());
                table.add(row);

            // Otherwise fill rankings table
            } else {
	            for (i=0; i<list.size(); i++) {
	                il = (ItemList)list.elementAt(i);
	                id = il.getString(idName);
	                rank = il.getInteger("rank");
	                score = il.getInteger("value");
	
	                row = new TableRow(new Object[] {"" + rank, id, "" + score}, SEPARATOR_LIST);
	                row.setFont(TABLE_FONT);
	                row.setBackgroundImage(new Image[] {null, slimOn});
	                row.setDimension(slimOn.getWidth(), slimOn.getHeight());
	
	                table.add(row);
	            }
            }
            if (header instanceof SectionNavigator) {
                SectionNavigator sn = (SectionNavigator)header;
                String[] sectionList;

                sectionList = new String[list.size() / table.getWindowSize() + 1];
                //System.out.println("sectionList.length: " + sectionList.length);
                for (i=0; i<sectionList.length; i++) {
                    sectionList[i] = "" + (i + 1);
                }

                sn.setSectionList(sectionList);
                // right-align the header to the table
                sn.setLocation(((getWidth() + slimOff.getWidth()) / 2) - header.getWidth(), 23);
            }

            setFocus(table);
            repaint();
            return true;
        }
        return false;
    }
    
    /**
 	 * For each key pressed while this View is active, implements special
 	 * display and event processing behavior for Ranking:
 	 * 
 	 * @param key Key Pressed
 	 */
 
    public void keyPressed(int key) {
        int action = getGameAction(key);

        //System.out.println("RankingView.keyPressed(): action = " + action);

        if (action == Canvas.LEFT && header instanceof SectionNavigator) {
            header.keyPressed(action, key);
        }

        if (action == Canvas.RIGHT && header instanceof SectionNavigator) {
            header.keyPressed(action, key);
        }

        super.keyPressed(key);

        if ((action == Canvas.UP || action == Canvas.DOWN)
            && header instanceof SectionNavigator)
        {
            SectionNavigator sn = (SectionNavigator)header;
            //System.out.println("table.getCursor(): " + table.getCursor());
            //System.out.println("table.getWindowSize(): " + table.getWindowSize());
            sn.setSelected(table.getCursor() / table.getWindowSize());
        }
    }

 	/** EventListener callback */
    public boolean handleEvent(Event e) {
        if (e.getSource() == header && header instanceof SectionNavigator) {
            //System.out.println("section navigator: " + e.getValue());

            SectionNavigator sn = (SectionNavigator)header;
            int index = Integer.parseInt((String)e.getValue());
            table.setWindowStart(--index * table.getWindowSize());
            return true;
        }

        else if (e.getSource() == table) {
            //System.out.println("ranking table: " + e.getValue());
        }
        return false;
    }

    public void leftSoftButtonPressed(String label) {
        table.keyPressed(Canvas.FIRE, -1);
    }

    public void rightSoftButtonPressed(String label) {
        community.switchToView( Community.BACK);
    }
}