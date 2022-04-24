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
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import com.nokia.sm.net.ItemList;
import samples.ui.*;


/**
 * Implements the list of Buddies and their presence and
 * chat status.
 */
public class BuddyList extends Component {
    private static final int TILE_WIDTH = 8;

    private int pendingChatCount;
    private int onlineBuddyCount;
    private Vector list;
    private Image buddy;
    private Image bubble;
    private Image away;
    private Image[] count;

    /**
     * Create a new BuddyList instance. Lists buddies with their presence
     *  status represnted in UI: overlay chat_bubble image.
     *
     */
    public BuddyList() {
        int xx, i;

        list = new Vector();

        buddy = ResourceManager.getImage("/presence_on.png");
        bubble = ResourceManager.getImage("/chat_bubble.png");
        away = ResourceManager.getImage("/presence_icon_on_unavail_2.png");

        Image img = ResourceManager.getImage("/numbers.png");
        count = new Image[9];

        xx = 0;
        for (i=0; i<count.length; i++) {
            count[i] = Image.createImage(
                img,
                xx,
                0,
                i == count.length - 1
                    ? 2 * TILE_WIDTH
                    : TILE_WIDTH,
                img.getHeight(),
                0
            );

            xx += TILE_WIDTH;
        }

        width = buddy.getWidth() + bubble.getWidth() - 2;
        height = buddy.getHeight() + 3;
    }

    public void setDimension(int width, int height) {
        // we don't like to be resized
    }

    /**
     * Update Event to all listeners
     *
     */
    private void updateListeners() {
        notifyListeners(new Event(this, Event.ITEM_DESELECTED, null));
    }

    /**
     * Add Buddy
     * @param buddy Buddy object
     */
    public synchronized void add(Buddy buddy)
    {
    	// Don't add the same buddy twice.
    	if ( get(buddy.getName()) != null) return;
    	
        list.addElement(buddy);
        if (buddy.isAvailable()) onlineBuddyCount++;

        repaint();
        updateListeners();
    }

    public synchronized void set(Vector newList) {

    	list.removeAllElements();
        if (newList == null) return;

        onlineBuddyCount = 0;
        pendingChatCount = 0;

        for (int i=0; i<newList.size(); i++) {
            ItemList il = (ItemList)newList.elementAt(i);

            // Only add buddy if they are not already in
            // our list, and the buddy invitation has been
            // accepted already.
            String name = il.getString("name");
            if ("both".equals(il.getString("status"))) 
            {
                add( new Buddy( name, Buddy.OFFLINE));
            }
        }

        repaint();
        updateListeners();
    }

    public Buddy get(int index) {
        return (Buddy)list.elementAt(index);
    }

    public int size() {
        return list.size();
    }

    public synchronized Buddy get(String name) {
        for (int i=0; i<list.size(); i++) {
            Buddy buddy = (Buddy)list.elementAt(i);

            if (name.equals(buddy.getName())) {
                return buddy;
            }
        }

        return null;
    }

    /**
     * Remove buddy and update buddy count and messages
     * @param name Buddy name
     */
    public synchronized void remove(String name) {
        Buddy buddy = get(name);

        //System.out.println("BuddyList.removeBuddy(): " + name);

        if (buddy != null) {
            list.removeElement(buddy);
            if (buddy.getMessage() != null) pendingChatCount--;
            repaint();
            updateListeners();
        }
    }

    /**
     * update buddy presence for chat and game modes
     * @param name Buddy name
     * @param available availability status
     */
    public synchronized void updatePresence(String name, int presence, Integer gcid) {
        Buddy buddy = get(name);

//        System.out.println("BuddyList.updatePresence(), name = " + name + ", available = " + presence);

        if (buddy != null) {
            boolean buddyAvailable = buddy.isAvailable();
            buddy.setStatus( presence);
            buddy.setGcid( gcid);
            if (!buddyAvailable && (presence != Buddy.OFFLINE)) onlineBuddyCount++;
            if ( buddyAvailable && (presence == Buddy.OFFLINE)) onlineBuddyCount--;
            repaint();
            updateListeners();
        }
    }

    /**
     * add chat to the buddy chat queue
     * @param name buddy name
     * @param msg chat message
     */
    public synchronized void addChat(String name, String msg) {
        Buddy buddy = get(name);

        //System.out.println("BuddyList.addChat(), name = " + name + ", msg = " + msg);

        if (buddy != null) {
            String s = buddy.getMessage();

            //System.out.println("s = " + s);

            if (s == null) pendingChatCount++;
            buddy.setMessage(msg);
            repaint();
            updateListeners();
        }
    }

    /**
     * get message from buddy
     * @param name Buddy name
     * @return message String
     */
    public synchronized String getChat(String name) {
        Buddy buddy = get(name);

        //System.out.println("BuddyList.getChat(), name = ");

        if (buddy != null) {
            String s = buddy.getMessage();
            if (s != null)  pendingChatCount--;
            buddy.setMessage(null);
            repaint();
            updateListeners();

            return s;
        }

        return null;
    }

    /**
     * Paint this instance.
     *
     * @param g The Graphics object to use for painting operations.
     */
    public void paint(Graphics g) {
        int cappedCount;

        g.drawImage(buddy, x, y + 3, NW_ANCHOR);

        if (pendingChatCount > 0) {
            g.drawImage(bubble, x + buddy.getWidth() - 2, y + 1, NW_ANCHOR);
        }
        int onlineBuddyCount = 0;
        for (int i=0; i<list.size(); i++) {
            Buddy bud = (Buddy)list.elementAt(i);
            if (bud.getStatus() != Buddy.OFFLINE) onlineBuddyCount++;
        }
        cappedCount = Math.min(8, onlineBuddyCount-1);
        if (cappedCount >= 0)
            g.drawImage(count[cappedCount], x + buddy.getWidth() - 3, y + buddy.getHeight() - count[0].getHeight() + 3, NW_ANCHOR);
    }

}