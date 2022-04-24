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

import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * This class implements a user interface toggle class that can be in
 * either an on or off state.. An Event of type Event.ITEM_SELECTED or
 * Event.ITEM_DESELECTED is propagated as the Choice object state is
 * toggled by key press events.  
 */
public class Choice extends Component {
    protected Object label;
    protected boolean state;
    protected Image offImage;
    protected Image onImage;

    /** 
     * Create a new Choice instance. 
     *
     * @param offImage The Image to use for dislaying the off state.
     * @param onImage The Image to use for dislaying the on state.
     * @param label The Image or String to display as a label.
     */
    public Choice(Image offImage, Image onImage, Object label) {
        this.offImage = offImage;
        this.onImage = onImage;

        focusable = true;

        this.label = label;
        state = false;
    }

    /** 
     * Update the state of this instance in light of a key press action.
     *
     * @param action The type of action. The Choice class handles actions
     *               of type Canvas.FIRE and notifies registered event
     *               listeners of an Event of type Event.ITEM_SELECTED
     *               or Event.ITEM_DESELECTED as the state is toggled.
     * @param key The key pressed.
     * @return true if the instance handled the event, and 
     *         false if the instance ignored the event.  
     */
    public boolean keyPressed(int action, int key) {
        if (action == Canvas.FIRE || key == View.ENTER_BUTTON) {
            state = !state;

            repaint();

            notifyListeners( new Event(
                this,
                state ? Event.ITEM_SELECTED : Event.ITEM_DESELECTED,
                new Boolean(state)
            ));

            return true;
        }

        return false;
    }

    /** 
     * Set the state for this instance.
     *
     * @param state The state for this instance.
     */
    public void setState(boolean state) {
        this.state = state;
    }

    /** 
     * Get the state for this instance.
     *
     * @return The state for this instance.
     */
    public boolean getState() {
        return state;
    }

    /** 
     * Set the label for this instance. 
     *
     * @param label The label for this instance.
     */
    public void setLabel(Object label) {
        this.label = label;
    }

    /** 
     * Get the label for this instance. A label of type Image or type
     * String is acceptable.
     *
     * @return The label for this instance.  */
    public Object getLabel() {
        return label;
    }

    /** 
     * Paint this instance. 
     *
     * @param g The Graphics object to use for painting operations.
     */
    public void paint(Graphics g) {
        super.paint(g);

        if (label instanceof Image) {
            Image img = (Image)label;

            g.drawImage(
                img,
                x + 5,
                y + (height - img.getHeight()) / 2,
                NW_ANCHOR
            );

            g.drawImage(
                state ? onImage : offImage,
                x + getWidth() - 5 - onImage.getWidth(),
                y + (height - onImage.getHeight()) / 2,
                NW_ANCHOR
            );

        } else {
            int color = (focus && focusFontColor != -1) ? focusFontColor : fontColor;
            String str = label.toString();
            paintText( g, color, str, x + textOffsetX, y + textOffsetY, false, true);
            int stringWidth = font.charsWidth( str.toCharArray(), 0, str.length());
            g.drawImage(
                    state ? onImage : offImage,
                    x + stringWidth + textOffsetX + 5,
                    y + (height - onImage.getHeight()) / 2,
                    NW_ANCHOR
                );

        }
        paintBorder( g);
    }
}
