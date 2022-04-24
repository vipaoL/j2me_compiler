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
 * This class implements a popup menu.  When popped "up", displays entire 
 * menu of options and user may scroll up and down to select a new option.
 * When popped "down", shows only a single line w/ the current selected 
 * value.  Pops "up" and "down" on select/fire events.   
 */
public class List extends Component
{
    private Object[] choices;
    private int selection = 0;
    private boolean isPoppedUp;
    protected Image onImage = null;
    protected Image offImage = null;
    protected int popupH = -1;
    protected int popupW = -1;
    protected int popupX = -1;
    protected int popupY = -1;

    /** 
     * Create a new Choice instance. 
     *
     * @param onImage The Image to use for dislaying the on state.
     * @param offImage The Image to use for dislaying the off state.
     * @param choices An object array of choices to be displayed.
     */
    public List( Image onImage, Image offImage, Object[] choices) 
    {
    	super();
    	this.onImage = onImage;
    	this.offImage = offImage;
        this.choices = choices; 
       	this.selection = 0;
        this.focusable = true;
        this.isPoppedUp = false;
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
    	
    	// If not popped up, then FIRE/ENTER events pop up the menu
    	if (!isPoppedUp) {
    		if (action == Canvas.FIRE || key == View.ENTER_BUTTON) 
    		{	
    			isPoppedUp = true;
    			repaint();
    			return true;
    		} else {
    			return false;
    		}
    	}
    	
    	// If popped up: scroll up/down menu, or select item (and pop menu back down)
        if (action == Canvas.UP) {
        	if (selection > 0) {
        		selection--;
        		repaint();
        	}
    		return true;
        } else if (action == Canvas.DOWN) {
        	if (selection < choices.length-1) {
        		selection++;
        		repaint();
        	}
    		return true;
            	
        } else if (action == Canvas.FIRE || key == View.ENTER_BUTTON) {
            notifyListeners( 
            	new Event( this, Event.ITEM_SELECTED, new Integer(selection))
            	);
        	isPoppedUp = false;
            repaint();
            return true;
        }

        return false;
    }

    /** 
     * Set the state for this instance.
     *
     * @param sel The state for this instance.
     */
    public void setSelection(int sel) {
    	if (sel >=0 && sel < choices.length)
    		this.selection = sel;
    }

    /** 
     * Get the state for this instance.
     *
     * @return The state for this instance.
     */
    public int getSelection() {
        return selection;
    }

    /**
     * 
     * @return the selected item in the list (member of array passed into List constructor)
     */
    public Object getSelectedComponent() {
    	if (choices == null || selection<0 || selection>=choices.length) return null;
    	return choices[ selection];
    }

    /**
     * Calculate placement for the popup menu, trying to place its upper-left corner 
     * at the List widget's x,y location, but moving it up/left as necessary to keep
     * it from extending off the bottom/right edges of the screen. 
     *
     */
    protected void calcPopupPlacement()
    {
    	if ( x==-1 || y==-1 || width==0 || height==0 || choices==null) return;
		popupH = 0;
		popupW = width;
		for (int i=0; i<choices.length; i++) {
			int h = height;
			int w = width;
			if (choices[i] instanceof Image) {
				h = ((Image)choices[i]).getHeight();
				w = ((Image)choices[i]).getWidth();
				if (w > popupW) popupW = w;
			}
			popupH += h;
		}    	
   		// Try to place popup where our non-popped-up widget sits
		popupX = x;
		popupY = y;
		// Move popup up if necessary, to make sure it doesn't go 
		// off bottom of screen.
		if (y+popupH>view.getHeight()-2) 
			popupY = view.getHeight()-2-popupH;
		// Move popup left if necessary, to make sure it doesn't go
		// off right half of screen.
		if (x+popupW>view.getWidth()) 
			popupX = view.getWidth()-popupW;
    }
		
    /** 
     * Paint this instance, either in its popped-up state (with full menu of options visible)
     * or popped "down", displaying only the current selection.
     *
     * @param g The Graphics object to use for painting operations.
     */
    public void paint(Graphics g) {
    	
    	// If not popped up, then draw as a single-line text box, showing the current
    	// selection.  Also, draw "popup" graphic on right-hand side of list.
    	if (!isPoppedUp) {
    		super.paint(g);
    		Image img = null;
    		if (choices[ selection] instanceof Image) {
        		img = (Image)(choices[ selection]);
                g.drawImage(img, width - img.getWidth(), y,NW_ANCHOR);
    		} else {
	            int color = (focus && focusFontColor != -1) ? focusFontColor : fontColor;
	            String str = choices[ selection].toString();
	            paintText( g, color, str, x+textOffsetX, y+textOffsetY, false, true);
    		}
    		img = focus ? onImage : offImage;
            g.drawImage(img, x + width - img.getWidth(), y, NW_ANCHOR);
	        paintBorder( g);
                
        // Otherwise, draw full menu showing all options.  The currently selected
	    // option is drawn highlighted.
    	} else {
    		
    		if (popupW == -1 || popupH == -1 || popupX == -1 || popupY == -1) 
    			calcPopupPlacement();

            int color = (focus && focusBackground != -1) ? focusBackground : background;
            if (color != -1) {
    	        g.setColor( color);        	
    	        g.fillRect(x, y, width, height);
            }

            int x = popupX;
            int y = popupY;
            int w = 0;
            int h = 0;
            for (int i=0; i<choices.length; i++) {
    			if (choices[i] instanceof Image) {
    				h = ((Image)choices[i]).getHeight();
    				w = ((Image)choices[i]).getWidth();
    			} else {
        			h = height;
        			w = width;    				
    			}
    			if (i==selection) {
    				color = (focusForeground != -1) ? focusForeground : foreground;
    			} else {
    				color = background;
    			}
                if (color != -1) {
        	        g.setColor( color);        	
        	        g.fillRect(x, y, w, h);
                }
                if (choices[i] instanceof Image) {
                    g.drawImage( (Image)choices[i], x, y, NW_ANCHOR);
                } else {
        			if (i==selection) {
        				color = (focus && focusFontColor != -1) ? focusFontColor : fontColor;
        			} else {
        				color = fontColor;
        			}
        			paintText( g, color, choices[i].toString(), x + textOffsetX, y + textOffsetY, false, true);
                }
                y+=h;
            }
            if (drawBorders) {
	            color = (focus && focusForeground != -1) ? focusForeground : foreground;
	           	g.setColor( color);        	
	            g.drawRect(popupX, popupY, popupW-1, popupH-1);
            }
    	}    		
    }
}
