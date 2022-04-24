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

/**
 * This class implements an invisible PageBreak component. This is a GUI
 * component which alters the layout of widgets drawn after it when it is
 * activated, but has no visual representation itself.
 */
public class PageBreak extends Component {
	private String name;
	private Component titleComp;
	private int yOffset;
	private boolean isActive = false;
	
    /** 
     * Create a new PageBreak instance with the provided name, 
     * title component (optional) and yOffset from the top of 
     * the screen from which to begin drawing the first widget 
     * after the PageBreak. 
     *
     * @param name The name of this PageBreak.
     * @param title Whether to center the text in the label.
     */
    public PageBreak( String name, Component title, int y, int topMargin) {
        this.name = name;
        this.titleComp = title;
        this.yOffset = -y + topMargin;
        this.y = y;
    }

    /** 
     * Get the text for this instance.
     *
     * @return The text for this instance.
     */
    public String getName() {
        return name;
    }


    /** 
     * Get the titleComponent for this instance, if there is one.
     * Otherwise returns <code>null<code>.
     *
     * @return The icon for this instance.
     */
    public Component getTitleComponent() {
        return titleComp;
    }

    /**
     * Get the yOffset for this instance.
     * 
     * @return The Y-axis offset for this instance as an int.
     */
    public int getYOffset() {
    	return yOffset;
    }
    
    /**
     * Returns the <code>isActive</code> flag for this instance.
     * 
     * @return a boolean indicating whether this instance is active or not.
     */
    public boolean isActive() {
    	return isActive;
    }
    
    /**
     * Sets the <code>isActive</code> flag for this instance.
     *
     */
    public void setActive( boolean active) {
    	this.isActive = active;
    }
    
    /** 
     * Paint this instance.  This either paints the associated 
     * <code>title</code> component, if there is one and if the 
     * PageBreak is active, or else paints nothing. 
     *
     * @param g The Graphics object to use for painting operations.
     */
    public void paint(Graphics g) 
    {
    	if (isActive && titleComp != null) {
    		titleComp.paint( g);
    	}
    }
}
