package samples.ui;

import javax.microedition.lcdui.*;

/**
 * This class implements a row in a graphical table. A row
 * has one or more columns.
 */
public class TableRow extends Component {
    private Object[] elemList;
    private int[] separatorList;
    private boolean drawSeparators;

    /**
     * Create a new TableRow instance with the provided elements and separators.
     *
     * @param elemList An array of elements to populate the table row.
     * @param separatorList An array of graphical column positions.
     */
    public TableRow(Object[] elemList, int[] separatorList) {
        this.elemList = elemList;
        this.separatorList = separatorList;

        
        
        drawBorders = false;
        drawShadows = false;
        drawSeparators = false;
    }

    /**
     * Set whether the instance draws column separators or not.
     *
     * @param drawSeparators Whether the instance draws separators or not.
     */
    public void setDrawSeparators(boolean drawSeparators) {
        this.drawSeparators = drawSeparators;
    }

    /**
     * Query whether the instance draws column separators or not.
     *
     * @return Whether the instance draws separators or not.
     */
    public boolean getDrawSeparators() {
        return drawSeparators;
    }

    /**
     * Set the element object in the provided column for this instance.
     *
     * @param elem The Object to insert in the specified column.
     * @param column The column to update.
     */
    public void setElement(Object elem, int column) {
        elemList[column] = elem;
        repaint();
    }

    /**
     * Get the element object from the provided column for this instance.
     *
     * @param column The desired column index.
     * @return The Object in the specified column.
     */
    public Object getElement(int column) {
        return elemList[column];
    }

    /**
     * Paint this instance.
     *
     * @param g The Graphics object to use for painting operations.
     */
    public void paint(Graphics g) {
        Object elem;
        int i;

        super.paint(g);

        for (i=0; i<elemList.length; i++) {
            if (drawSeparators && i > 0) {
                g.setColor(foreground);
                g.drawLine(x + separatorList[i], y + 2, x + separatorList[i], y + height - 3);
            }

            elem = elemList[i];

            if (elem instanceof Image) {
                g.drawImage(
                    (Image)elem,
                    x + separatorList[i] + 5,
                    y,
                    NW_ANCHOR
                );
            } else {
//                g.setFont(font);
                g.setColor(fontColor);

                paintText(g, fontColor, elem.toString(), x + separatorList[i] + 5, y, false, true);
            }
        }
    }
}
