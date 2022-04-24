package samples.ui;

import java.util.Vector;
import javax.microedition.lcdui.*;

/**
 * This class implements a graphical table. A table contains
 * an optional header, and zero or more rows. The optional
 * header and each row are instances of the TableRow class.
 *
 * @see TableRow
 */
public class Table extends Component {
    private static final int LIGHT_GRAY = 0x00eeeeee;
    private static final int WHITE      = 0x00ffffff;

    private TableRow header;
    private Vector rowList;
    private int rowHeight;
    private int cursor;
    private int windowStart;
    private int windowSize;
    private boolean popup;

    private Table() {
        rowList = new Vector();
        cursor = 0;
        windowStart = 0;
        windowSize = 1;
    }

    /**
     * Create a new Table instance with the provided column headers.
     *
     * @param header A TableRow with column headers for the table.
     */
    public Table(TableRow header) {
        this();

        this.header = header;
        rowHeight = header.getHeight();
    }

    /**
     * Create a new Table instance with no header, and the specified row height.
     *
     * @param rowHeight The height of each table row.
     * @param popup Whether this table is used to implement a popup.
     */
    public Table(int rowHeight, boolean popup) {
        this();

        this.rowHeight = rowHeight;
        this.popup = popup;
    }

    /**
     * Set the element object in the provided row/column for this instance.
     *
     * @param elem The Object to insert in the specified column.
     * @param row The row to update.
     * @param column The column to update.
     */
    public void setElement(Object elem, int row, int column) {
        TableRow tableRow;

        tableRow = (TableRow)rowList.elementAt(row);
        tableRow.setElement(elem, column);
        repaint();
    }

    /**
     * Get the element object from the provided row/column for this instance.
     *
     * @param row The desifred row index.
     * @param column The desired column index.
     * @return The Object in the specified column.
     */
    public Object getElement(int row, int column) {
        TableRow tableRow;

        tableRow = (TableRow)rowList.elementAt(row);
        return tableRow.getElement(column);
    }

    /**
     * Specify how many rows of this table should be visible.
     *
     * @param windowSize The number of rows to be visible.
     */
    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
        repaint();
    }

    /** Return the number of visible rows in this table. */
    public int getWindowSize() {
        return windowSize;
    }

    /**
     * Specifiy the first visible row in this table .
     *
     * @param windowStart The first row to be visible.
     */
    public void setWindowStart(int windowStart) {
        this.windowStart = windowStart;
        cursor = windowStart;
        //System.out.println("Table.setWindowStart(), cursor = " + cursor);
        repaint();
    }

    /** Return the index of the first visible row in this table. */
    public int getWindowStart() {
        return windowStart;
    }

    /**
     * Get the current cursor position for this instance.
     *
     * @return The current cursor position for this instance.
     */
    public int getCursor() {
        return cursor;
    }

    /**
     * Add the provided row to the end of the rows in this instance.
     *
     * @param row The row to add to the end of the rows in this instance.
     */
    public void add(TableRow row) {
        row.setDimension(row.getWidth(), rowHeight);

        rowList.addElement(row);
        repaint();
    }

    /**
     * Remove the row at the provided index in this instance.
     *
     * @param index The row to remove in this instance.
     */
    public void remove(int index) {
        int size = rowList.size();

        rowList.removeElementAt(index);

        if (cursor == size - 1 && rowList.size() > 0) {
            cursor--;
        }

        repaint();
    }

    /**
     * Query whether the instance can scroll up further or not.
     *
     * @return Whether the instance can scroll up further.
     */
    public boolean canScrollUp() {
        return cursor > 0;
    }

    /**
     * Query whether the instance can scroll down further or not.
     *
     * @return Whether the instance can scroll down further.
     */
    public boolean canScrollDown() {
        return cursor < rowList.size() - 1;
    }

    /**
     * Update the state of this instance in light of a key press action.
     *
     * @param action The type of action. The Table class handles actions
     *               of type Canvas.FIRE and notifies registered event
     *               listeners of an Event of type Event.ITEM_SELECTED.
     *               The Table class handles actions of type Canvas.UP
     *               and Canvas.DOWN.
     * @param key The key pressed.
     * @return true if the instance handled the event, and
     *         false if the instance ignored the event.
     */
    public boolean keyPressed(int action, int key) {
        //System.out.println("RankingView.keyPressed(): cursor = " + cursor);

        if (action == Canvas.UP) {
            if (canScrollUp()) {
                cursor--;
                //System.out.println("adjusted cursor = " + cursor);

                if (cursor < windowStart) windowStart--;
                repaint();
                notifyListeners(new Event(this, Event.CURSOR_MOVED, new Integer(cursor)));
                return true;
            }

            if (popup) return true;
        }

        if (action == Canvas.DOWN) {
            if (canScrollDown()) {
                cursor++;
                //System.out.println("adjusted cursor = " + cursor);

                if (cursor >= windowStart + windowSize) windowStart++;
                repaint();
                notifyListeners(new Event(this, Event.CURSOR_MOVED, new Integer(cursor)));

                return true;
            }

            if (popup) return true;
        }
        
        // Note: this is so that when a table is the active Component,
        // left/right keypresses don't get passed off and consumed
        // by other neighbor widgets, or by the owning View.
        if (action == Canvas.LEFT || action == Canvas.RIGHT) return true;

        return false;
    }

    /**
     * Update the state of this instance in light of a key release action.
     *
     * @param action The type of action. The Table class handles actions
     *               of type Canvas.FIRE and notifies registered event
     *               listeners of an Event of type Event.ITEM_DESELECTED.
     *               The Table class handles actions of type Canvas.UP
     *               and Canvas.DOWN.
     * @param key The key released.
     * @return true if the instance handled the event, and
     *         false if the instance ignored the event.
     */
    public boolean keyReleased(int action, int key) {
        if (action == Canvas.FIRE) {
            notifyListeners(new Event(this, Event.ITEM_DESELECTED, new Integer(cursor)));
            return true;
        }

        return false;
    }

    /** Return the height of this instance in pixels. */
    public int getHeight() {
        int hh = 0;

        if (header != null) hh += rowHeight;
        hh += Math.min(rowList.size(), windowSize) * rowHeight;

        return hh;
    }

    /**
     * Set whether the instance has input focus or not.
     *
     * @param focus Whether the instance has input focus.
     */
    public void setFocus(boolean focus) {
        TableRow row;

        super.setFocus(focus);

        if (rowList.size() > 0) {
            row = (TableRow)rowList.elementAt(cursor);
            row.setFocus(focus);
        }
    }

    /**
     * Query whether the instance can have input focus or not.
     *
     * @return Whether the instance can have input focus.
     */
    public boolean isFocusable() {
        return size() > 0;
    }

    /**
     * Get the current number of rows for this instance.
     *
     * @return The current number of rows for this instance.
     */
    public int size() {
        return rowList.size();
    }

    /**
     * Remove all rows in this instance.
     */
    public void clear() {
        rowList.removeAllElements();
        repaint();
    }

    /**
     * Paint this instance.
     *
     * @param g The Graphics object to use for painting operations.
     */
    public void paint(Graphics g) {
        TableRow row;
        int i, yy;

        super.paint(g);

        yy = y;

        if (header != null) {
            header.setLocation(x, yy);
            header.paint(g);
            yy += rowHeight;
        }

        for (i=0; i<Math.min(rowList.size() - windowStart, windowSize); i++) {
            row = (TableRow)rowList.elementAt(windowStart + i);
            row.setLocation(x, yy);
            row.setBackground((!popup && (windowStart + i & 1) == 0) ? LIGHT_GRAY : WHITE);

            if (hasFocus() && cursor == windowStart + i) row.setFocus(true);
            row.paint(g);
            row.setFocus(false);

            yy += rowHeight;
        }

        paintBorder(g);
    }
}
