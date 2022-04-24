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

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * This class implements a user interface label class. This is a
 * passive object in that it does not respond to any events.
 */
public class Label extends Component {
    private String text;
    private Image icon;
    private boolean center;

    /** 
     * Create a new Label instance with the provided text. 
     *
     * @param text The text to display in the label.
     * @param center Whether to center the text in the label.
     */
    public Label(String text, boolean center) {
        this.text = text;
        this.center = center;
    }

    /** 
     * Create a new Label instance with the provided icon. 
     *
     * @param icon The icon to display in the label.
     */
    public Label(Image icon) {
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
     * Set the icon for this instance.
     *
     * @param icon The icon for this instance.
     */
    public void setIcon(Image icon) {
        this.icon = icon;
        text = null;
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
     * Paint this instance. 
     *
     * @param g The Graphics object to use for painting operations.
     */
    public void paint(Graphics g) {
        int off;

        super.paint(g);

        off = center ? 0 : textOffsetX;

        if (icon != null) {
            g.drawImage(
                icon,
                x + (width - icon.getWidth()) / 2,
                y + (height - icon.getHeight()) / 2,
                NW_ANCHOR
            );
        }

        if (text != null) {
            int color = (focus && focusFontColor != -1) ? focusFontColor : fontColor;
           	g.setColor( color);        	
            paintText(g, color, text, x + off, y + textOffsetY, center, true);
        }

        paintShadow(g);
    }
}
