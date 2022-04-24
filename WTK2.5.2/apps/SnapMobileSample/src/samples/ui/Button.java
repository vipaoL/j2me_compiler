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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * This class implements a user interface button class. An Event of
 * type Event.ITEM_SELECTED is propagated for handled key press
 * events, and an Event of type Event.ITEM_DESELECTED is propagated
 * for handled key release events.  
 */
public class Button extends Component {
    private static final int ON      = 0;
    private static final int OFF     = 1;
    private static final int PRESSED = 2;

    private String text;
    private Image icon;
    private Image[] imgList;
    private int[] colorList;
    private boolean isPressed;

    /** 
     * Create a new Button instance. 
     */
    Button() {
        focusable = true;
    }

    /** 
     * Create a new Button instance with the provided string. 
     *
     * @param text The text to display in the button.
     */
    public Button(String text) {
        this();
        this.text = text;
    }

    /** 
     * Create a new Button instance with the provided icon. 
     *
     * @param icon The image to display in the button.
     */
    public Button(Image icon) {
        this();
        this.icon = icon;
    }

    /** 
     * Set the text for this instance.
     *
     * @param text The text for this instance.
     */
    public void setText(String text) {
        this.text = text;
        icon = null;
        repaint();
    }

    /** 
     * Get the text for this instance.
     *
     * @return The text for this instance.
     */
    public String getText() {
        return text;
    }

    /**
     * Set the image and color data for this instance. The button can
     * be in 1 or 3 different states: ON, OFF, or PRESSED. The images
     * and colors at index 0 are used for the ON state, at index 1 are
     * used for the OFF state, abd at index 2 are used for the PRESSED
     * state.
     *
     * @param imgList The array of 3 images.
     * @param colorList The array of 3 colors.
     */
    public void setStateData(Image[] imgList, int[] colorList) {
        this.imgList = imgList;
        this.colorList = colorList;
    }

    /** 
     * Set the icon for this instance.
     *
     * @param icon The icon for this instance.
     */
    public void setIcon(Image icon) {
        this.icon = icon;
        icon = null;
        repaint();
    }

    /** 
     * Get the icon for this instance.
     *
     * @return The icon for this instance.
     */
    public Image getIcon() {
        return icon;
    }

    /** 
     * Update the state of this instance in light of a key press action.
     *
     * @param action The type of action. The Button class handles actions
     *               of type Canvas.FIRE and notifies registered event
     *               listeners of an Event of type Event.ITEM_SELECTED.
     * @param key The key pressed.
     * @return true if the instance handled the event, and 
     *         false if the instance ignored the event.  
     */
    public boolean keyPressed(int action, int key) {
        boolean saveShadows;

        if (action == Canvas.FIRE || key == View.ENTER_BUTTON) {
            isPressed = true;

            saveShadows = drawShadows;
            drawShadows = false;

            repaint();

            drawShadows = saveShadows;

            notifyListeners(new Event(this, Event.ITEM_SELECTED, text == null ? (Object)icon : (Object)text));

            return true;
        }

        return false;
    }

    /** 
     * Update the state of this instance in light of a key release action. 
     * 
     * @param action The type of action. The Button class handles actions
     *               of type Canvas.FIRE and notifies registered event
     *               listeners of an Event of type Event.ITEM_DESELECTED.
     * @param key The key released.
     * @return true if the instance handled the event, and 
     *         false if the instance ignored the event.
     */
    public boolean keyReleased(int action, int key) {
        if (action == Canvas.FIRE || key == View.ENTER_BUTTON) {
            isPressed = false;
            repaint();
            notifyListeners(new Event(this, Event.ITEM_DESELECTED, text == null ? (Object)icon : (Object)text));

            return true;
        }

        return false;
    }

    /** 
     * Paint this instance. 
     *
     * @param g The Graphics object to use for painting operations.
     */
    public void paint(Graphics g) {
        int index, color;

        super.paint(g);

        if (imgList == null) {
            color = (focus && focusBackground != -1) ? focusBackground : background;
            index = -1;
        } else {
            if (hasFocus()) {
                index = isPressed ? PRESSED : ON;
            } else {
                index = OFF;
            }

            g.drawImage( imgList[index], x, y, NW_ANCHOR);
            color = colorList[index];
        }

        if (icon != null) {
            g.drawImage(
                icon,
                x + (width - icon.getWidth()) / 2,
                y + (height - icon.getHeight()) / 2,
                NW_ANCHOR
            );
        }

        if (text != null) {
            paintText( g, color, text, x, y + textOffsetY, true, true);
        }

        if (drawShadows && index != PRESSED) {
            g.setColor( shadowColor);
            g.drawLine(x + 1, y + height, x + width - 1, y + height);
            g.drawLine(x + 2, y + height + 1, x + width, y + height + 1);
            g.drawLine(x + width - 1, y + height - 1, x + width - 1, y + height - 1);
            g.drawLine(x + width, y + height, x + width, y + height);
            g.drawLine(x + width, y + 1, x + width, y + height - 1);
            g.drawLine(x + width + 1, y + 2, x + width + 1, y + height);
        }
    }
}
