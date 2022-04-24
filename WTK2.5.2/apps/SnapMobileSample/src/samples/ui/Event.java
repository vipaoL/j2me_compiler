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

/**
 * This class implements a simple event message used to represent user
 * interface actions.
 */
public class Event {
    /** Event indicating a component was selected */
    public static final int ITEM_SELECTED   = 0;

    /** Event indicating a component was deselected */
    public static final int ITEM_DESELECTED = 1;

    /** Event indicating that the text in a user editable component changed */
    public static final int TEXT_CHANGED    = 2;

    /** Event indicating that the cursor position in a Table changed */
    public static final int CURSOR_MOVED    = 3;

    private Object source;
    private int type;
    private Object value;

    /**
     * Create a new Event instance.
     *
     * @param source The object generating the event.
     * @param type The type of the event.
     * @param value The object containing the data for the event.
     */
    public Event(Object source, int type, Object value) {
        this.source = source;
        this.type = type;
        this.value = value;
    }

    /** 
     * Retrieve the object that generated the event. 
     *
     * @return The object that generated the event.
     */
    public Object getSource() {
        return source;
    }

    /** 
     * Retrieve the type of the event. 
     *
     * @return The type of the event.
     */
    public int getType() {
        return type;
    }

    /** 
     * Retrieve the object containing the data for the event. 
     *
     * @return The object contaning the data for the event.
     */
    public Object getValue() {
        return value;
    }
}
