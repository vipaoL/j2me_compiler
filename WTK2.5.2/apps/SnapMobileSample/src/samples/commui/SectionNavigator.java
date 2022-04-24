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
 * Class to implement all Navigation components:left and right arrow
 *
 */
class SectionNavigator extends Component {
    private static final int ARROW_OFF = 1;
    private static Image leftArrow;
    private static Image rightArrow;

    private Image selected;
    private String[] sectionList;
    private int index;

    static {
        leftArrow = ResourceManager.getImage("/left_arrow.png");
        rightArrow = ResourceManager.getImage("/right_arrow.png");
    }

    public SectionNavigator(Image selected, String[] sectionList) {
        this.selected = selected;

        setSectionList(sectionList);

        //System.out.println("selected.getWidth() = " + selected.getWidth());
        //System.out.println("selected.getHeight() = " + selected.getHeight());

        height = selected.getHeight();
    }

    public void setSectionList(String[] sectionList) {
        this.sectionList = sectionList;
        width = (selected.getWidth() - 1) * sectionList.length + 2 * (leftArrow.getWidth() + ARROW_OFF);
        index = 0;
        repaint();
    }

     /**
     * Update the state of this instance in light of a key press action.
     *
     * @param action The type of action. 
     * @param key The key pressed.
     * @return true if the instance handled the event, and
     *         false if the instance ignored the event.
     */

    public boolean keyPressed(int action, int key) {
        //System.out.println("SectionNavigator.keyPressed()");

        if (action == Canvas.LEFT && index > 0) {
            index--;
            repaint();

            notifyListeners(new Event(
                this,
                Event.ITEM_SELECTED,
                sectionList[index]
            ));

            return true;
        }

        if (action == Canvas.RIGHT && index < sectionList.length - 1) {
            index++;
            repaint();

            notifyListeners(new Event(
                this,
                Event.ITEM_SELECTED,
                sectionList[index]
            ));

            return true;
        }

        return false;
    }

    public void setSelected(int index) {
        this.index = index;
        //System.out.println("index: " + index);
        repaint();
    }

    public int getSelected() {
        return index;
    }

    public void setDimension(int width, int height) {
        // we don't like to be resized
    }
    
    /**
     * Paint this instance
     * @param g Graphics
     */

    public void paint(Graphics g) {
        int i, xx, sw;

        if (sectionList.length < 2) return;

        xx = x;
        g.drawImage(leftArrow, xx, y + (getHeight() - leftArrow.getHeight()) / 2, NW_ANCHOR);

        xx += leftArrow.getWidth();
        for (i=0; i<sectionList.length; i++) {
            if (i == index) {
                g.drawImage(selected, xx, y, NW_ANCHOR);
            }

            g.setColor(i == index ? 0x00808080 : foreground);
            g.drawRect(xx, y, selected.getWidth() - 1, getHeight() - 1);

            sw = font.stringWidth(sectionList[i]);
//            g.setFont(font);
            g.setColor(i == index ? 0x00000000 : foreground);
            g.drawString(sectionList[i], xx + (selected.getWidth() - sw) / 2, y + 2, NW_ANCHOR);

            xx += selected.getWidth() - 1;
        }

        xx += ARROW_OFF;
        g.drawImage(rightArrow, xx, y + (getHeight() - leftArrow.getHeight()) / 2, NW_ANCHOR);
    }
}