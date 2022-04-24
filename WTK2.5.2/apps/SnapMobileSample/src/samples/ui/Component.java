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
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * This class implements an abstract user interface class that
 * provides basic functions to control layout (position, width,
 * height), display (foreground/background color, font, shadows,
 * border, painting), and control (event dispatching and focus
 * control).
 */
public abstract class Component {
	
    /** Standard shadow color. */
//    public static final int SHADOW = 0x00606060;

    /** Standard layout anchor settings. */
    public static final int NW_ANCHOR = Graphics.TOP | Graphics.LEFT;

    /** Color to use for background painting. If -1, background will 
     * not be drawn. 
     */
    protected int background = -1;

    /** Color to use for background painting when in focus. 
     *  If -1, defaults to <code>background</code>.
     */
    protected int focusBackground = -1;
    
    /** Color to use for foreground painting. */
    protected int foreground;

    /** Color to use for foreground painting when in focus. */
    protected int focusForeground = -1;

    /** Color to use for font painting. */
    protected int fontColor;

    /** Color to use for font painting when in focus. */
    protected int focusFontColor = -1;

    /** Color to use for shadow */
    protected int shadowColor = 0x00606060;
    
    /** X coordinate of component position. */
    protected int x = -1;

    /** Y coordinate of component position. */
    protected int y = -1;

    /** Width of component. */
    protected int width = 0;

    /** Height of component. */
    protected int height = 0;

    /** Font for painting text. */
    protected CustomFont font;
//    protected  Font cfont;
    
    protected String test_font = "Font1";//"/7pt-proportional.png";//
   
    
    /** Number of pixels that text is indented from the left edge of
     *  the widget, when it is rendered.
     */
    protected int textOffsetX = 3;
    protected int textOffsetY = 2;

    /**
     * Array of images for background painting. Index 1 is used when
     * the object has focus, and index 0 is used when the object does
     * not have focus. */
    private Image[] imgList;
    private int[] colorList;

    /** Vector of registered event listeners. */
    protected Vector listeners;

    /** Boolean indicating whether to draw borders or not. */
    protected boolean drawBorders;

    /** Boolean indicating whether to draw shadows or not. */
    protected boolean drawShadows;

    /** Boolean indicating whether the component can accept input focus. */
    protected boolean focusable;

    /** Boolean indicating whether the component has input focus or not. */
    protected boolean focus;

    /** The View object that controls the component. */
    protected View view;

    /**
     * Create a new Component instance.
     */
    public Component() {
        drawBorders = true;
        listeners = new Vector();
        //font = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        font = new CustomFont(test_font);
    	
    }

    /**
     * Set the postion of this instance.
     *
     * @param x The X coordinate of the lower left corner of this instance.
     * @param y The Y coordinate of the lower left corner of this instance.
     */
    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;

        repaint();
    }

    /**
     * Get the X coordinate of this instance.
     *
     * @return The X coordinate of the lower left corner of this instance.
     */
    public int getX() {
        return x;
    }

    /**
     * Get the Y coordinate of this instance.
     *
     * @return The Y coordinate of the lower left corner of this instance.
     */
    public int getY() {
        return y;
    }

    /**
     * Set whether the instance has input focus or not.
     *
     * @param focus Whether the instance has input focus.
     */
    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    /**
     * Query whether the instance has input focus or not.
     *
     * @return Whether the instance has input focus.
     */
    public boolean hasFocus() {
        return focus;
    }

    /**
     * Set the dimensions of this instance.
     *
     * @param width The width of this instance.
     * @param height The height of this instance.
     */
    public void setDimension(int width, int height) {
        this.width = width;
        this.height = height;

        repaint();
    }

    /**
     * Query whether the instance wants to be highlighted when it has focus.
     *
     * @return Whether the instance is highlighted.
     */
    public boolean wantsHighlight() {
        return true;
    }

    /**
     * Get the width of this instance.
     *
     * @return The width of this instance.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the height of this instance.
     *
     * @return The height of this instance.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Set the View object that controls this instance.
     *
     * @param view The View object that controls this instance.
     */
    public void setView(View view) {
        this.view = view;
    }

    /**
     * Get the View object that controls this instance.
     *
     * @return The View object that controls this instance.
     */
    public View getView() {
        return view;
    }

    /**
     * Set the font for this instance.
     *
     * @param font The font for this instance.
     */
    public void setFont(CustomFont font) {
        this.font = font;
    }

    /**
     * Get the font for this instance.
     *
     * @return The font for this instance.
     */
    public CustomFont getFont() {
        return font;
    }

    /**
     * Set the font color for this instance.
     *
     * @param fontColor The font color for this instance.
     */
    public void setFocusFontColor(int fontColor) {
        this.focusFontColor = fontColor;
    }

    /**
     * Get the font color for this instance.
     *
     * @return The font color for this instance.
     */
    public int getFocusFontColor() {
        return focusFontColor;
    }

    /**
     * Set the background color for this instance.
     *
     * @param background The background color for this instance.
     */
    public void setFocusBackground(int background) {
        this.focusBackground = background;
    }

    /**
     * Get the background color for this instance.
     *
     * @return The background color for this instance.
     */
    public int getFocusBackground() {
        return focusBackground;
    }

    /**
     * Set the foreground color for this instance.
     *
     * @param foreground The foreground color for this instance.
     */
    public void setFocusForeground(int foreground) {
        this.focusForeground = foreground;
    }

    /**
     * Get the foreground color for this instance
     *
     * @return The foreground color for this instance.
     */
    public int getFocusForeground() {
        return focusForeground;
    }

    /**
     * Set the font color for this instance.
     *
     * @param fontColor The font color for this instance.
     */
    public void setFontColor(int fontColor) {
        this.fontColor = fontColor;
    }

    /**
     * Get the font color for this instance.
     *
     * @return The font color for this instance.
     */
    public int getFontColor() {
        return fontColor;
    }

    
    
    public int getShadowColor() {
		return shadowColor;
	}

	public void setShadowColor(int shadowColor) {
		this.shadowColor = shadowColor;
	}

	/**
     * Set the background color for this instance.
     *
     * @param background The background color for this instance.
     */
    public void setBackground(int background) {
        this.background = background;
    }

    /**
     * Get the background color for this instance.
     *
     * @return The background color for this instance.
     */
    public int getBackground() {
        return background;
    }

    /**
     * Set the foreground color for this instance.
     *
     * @param foreground The foreground color for this instance.
     */
    public void setForeground(int foreground) {
        this.foreground = foreground;
    }

    
    /**
     * Get the foreground color for this instance
     *
     * @return The foreground color for this instance.
     */
    public int getForeground() {
        return foreground;
    }

    
    /**
     * Get the X offset in pixels from the left edge of the widget
     * to begin drawing text
     *  
     * @return X offset in pixels from the left edge of the widget
     */
    public int getTextOffsetX() {
		return textOffsetX;
	}

    /**
     * Set the X offset in pixels from the left edge of the widget
     * to begin drawing text
     *  
     * @param textOffset X offset in pixels from the left edge of the widget
     */
	public void setTextMarginX(int textOffset) {
		this.textOffsetX = textOffset;
	}

    /**
     * Get the Y offset in pixels (downwards, from a vertically centered 
     * position within the widget) to begin drawing text.
     *  
     * @return Y offset in pixels
     */
     public int getTextOffsetY() {
		return textOffsetY;
	}

     /**
      * Set the Y offset in pixels (downwards, from a vertically centered 
      * position within the widget) to begin drawing text.
      *  
      * @param textOffset Y offset in pixels
      */
	public void setTextMarginY(int textOffset) {
		this.textOffsetY = textOffset;
	}

	/**
     * Set the backgroung image array of this instance. The image at
     * index 1 is used when the instance has focus, and the image
     * index 0 is used when the object does not have focus.
     *
     * @param bgImg The backgroung image array of this instance.
     */
    public void setBackgroundImage(Image[] bgImg) {
        this.imgList = bgImg;
    }

    /**
     * Get the backgroung image array of this instance.
     *
     * @return The backgroung image array of this instance.
     */
    public Image[] getBackgroundImage() {
        return imgList;
    }

    /**
     * Set whether the instance draws borders or not.
     */
    public void setDrawBorders(boolean drawBorders) {
        this.drawBorders = drawBorders;
    }

    /**
     * Query whether the instance draws borders or not.
     */
    public boolean isDrawBorders() {
        return drawBorders;
    }

    /**
     * Set whether the instance draws shadows or not.
     */
    public void setDrawShadows(boolean drawShadows) {
        this.drawShadows = drawShadows;
    }

    /**
     * Query whether the instance draws shadows or not.
     */
    public boolean isDrawShadows() {
        return drawShadows;
    }

    /**
     * Set whether the instance can have input focus or not.
     *
     * @param focusable Whether the instance can have input focus.
     */
    public void setFocusable(boolean focusable) {
        this.focusable = focusable;
    }

    /**
     * Query whether the instance can have input focus or not.
     *
     * @return Whether the instance can have input focus.
     */
    public boolean isFocusable() {
        return focusable;
    }

    /**
     * Repaint this instance.
     */
    public void repaint() {
        if (view != null) {
            view.repaint();
            //view.serviceRepaints();
        }
    }

    /**
     * Add the provided event listener to this instance. This method has no
     * effect if <code>l</code> is already a registered event listener.
     *
     * @param l The EventListener object to register with this instance.
     */
    public void addEventListener(EventListener l) {
        if (!listeners.contains(l)) listeners.addElement(l);
    }

    /**
     * Remove the provided event listener to this instance.
     *
     * @param l The EventListener object to deregister from this instance.
     */
    public void removeEventListener(EventListener l) {
        listeners.removeElement(l);
    }

    /**
     * Notify all event listeners registered with this instance of
     * the provided event.
     *
     * @param e The Event object to propagate to all event listeners.
     */
    protected void notifyListeners(Event e) {
        EventListener el;

        for (int i=0; i<listeners.size(); i++) {
            el = (EventListener)listeners.elementAt(i);
            el.handleEvent(e);
        }
    }

    /**
     * Update the state of this instance in light of a key press action.
     * The default Component implementation is for a passive component,
     * that is, a component that does not handle any key events.
     *
     * @param action The type of action.
     * @param key The key released.
     * @return true if the instance handled the event, and
     *         false if the instance ignored the event.
     */
    public boolean keyPressed(int action, int key) {
        return false;
    }

    /**
     * Update the state of this instance in light of a key release action.
     * The default Component implementation is for a passive component,
     * that is, a component that does not handle any key events.
     *
     * @param action The type of action.
     * @param key The key released.
     * @return true if the instance handled the event, and
     *         false if the instance ignored the event.
     */
    public boolean keyReleased(int action, int key) {
        return false;
    }

    /**
     * Repaint the border, if any, for this instance.
     *
     * @param g The Graphics object to use for painting operations.
     */
    protected void paintBorder(Graphics g) {
        if (!drawBorders) return;

        int color = (focus && focusForeground != -1) ? focusForeground : foreground;
       	g.setColor( color);        	
        g.drawRect(x, y, width - 1, getHeight() - 1);

        paintShadow(g);
    }

    /**
     * Repaint the shadow, if any, for this instance.
     *
     * @param g The Graphics object to use for painting operations.
     */
    protected void paintShadow(Graphics g) {
        int hh;

        if (!drawShadows) return;

        hh = getHeight();

        g.setColor( shadowColor);
        g.drawLine(x + 1, y + hh, x + width, y + hh);
        g.drawLine(x + 2, y + hh + 1, x + width + 1, y + hh + 1);
        g.drawLine(x + width, y + 1, x + width, y + hh);
        g.drawLine(x + width + 1, y + 2, x + width + 1, y + hh + 1);
    }

    /**
     * Paint the bottom and right edge borders, if any, for this instance.
     *
     * @param g The Graphics object to use for painting operations.
     */
    protected void paintLowBorder(Graphics g) {
        paintLowBorder(g, x, y, width, height);
    }

    /**
     * Paint the bottom and right edge borders, if any, for this instance.
     *
     * @param g The Graphics object to use for painting operations.
     * @param xx The X coordinate of the lower left corner reference point.
     * @param yy The Y coordinate of the lower left corner reference point.
     * @param ww The width of the bottom edge.
     * @param hh The height of the right edge.
     */
    protected void paintLowBorder(Graphics g, int xx, int yy, int ww, int hh) {
        if (!drawBorders) return;

        int color = (focus && focusForeground != -1) ? focusForeground : foreground;
       	g.setColor( color);        	
        g.drawLine(xx, yy, xx + ww, yy);
        g.drawLine(xx, yy, xx, yy + hh);

        g.setColor( shadowColor);
        g.drawLine(xx + 1, yy + hh, xx + ww, yy + hh);
        g.drawLine(xx + ww, yy, xx + ww, yy + hh);
    }

    /**
     * Paint the provided text within this instance.
     *
     * @param g The Graphics object to use for painting operations.
     * @param text The string to display.
     * @param xx The X coordinate of the lower left corner reference point.
     * @param yy The Y coordinate of the lower left corner reference point.
     * @param hcenter Whether the text should be horizontally centered
     *        within the component.
     * @param vcenter Whether the text should be vertically centered
     *        within the component.
     */
    protected void paintText(Graphics g, int color, String text, int xx, int yy, boolean hcenter, boolean vcenter) {
        paintText(g, color, text, xx, yy, width, height, hcenter, vcenter);
    }

    /**
     * Paint the provided text within this instance.
     *
     * @param g The Graphics object to use for painting operations.
     * @param text The string to display.
     * @param xx The X coordinate of the lower left corner reference point.
     * @param yy The Y coordinate of the lower left corner reference point.
     * @param ww The width of the bottom edge.
     * @param hh The height of the right edge.
     * @param hcenter Whether the text should be horizontally centered
     *        within the component.
     * @param vcenter Whether the text should be vertically centered
     *        within the component.
     */
    protected void paintText(Graphics g, int color, String text, int xx, int yy, int ww, int hh, boolean hcenter, boolean vcenter) {
            	
 //   	g.setFont(font);

        if (hcenter) xx += (ww - font.stringWidth(text)) / 2;
        if (vcenter) yy += (hh - font.getHeight()) / 2;

        /*if (drawShadows) {
            g.setColor(SHADOW);
            g.drawString(text, xx + 1, yy + 1, NW_ANCHOR);
        }*/

        g.setColor( color);
        //g.drawString( text, xx, yy, NW_ANCHOR);
        font.drawString( g, text, xx, yy);// NW_ANCHOR);
        
    }

    /**
     * Set a  key/value pair for this instance.
     *
     * @param name String to serve as a attribute key.
     * @param value String to serve as a attribute value.
     */
    public void setAttribute(String name, String value) {
    }

    public void paint( Graphics g, int offX, int offY) 
    { 
    	x += offX;
    	y += offY;
    	paint( g); 
    	x -= offX;
    	y -= offY;
    }

    /**
     * Paint this instance.
     *
     * @param g The Graphics object to use for painting operations.
     */
    public void paint(Graphics g) {
        int index;

        //System.out.println("Component.paint(): " + this);
        g.setClip( x-1, y-1, width+3, height+3);

        int color = (focus && focusBackground != -1) ? focusBackground : background;
        if (color != -1) {
	        g.setColor( color);
	        g.fillRect(x, y, width, height);
        }
        
        index = focus && imgList != null && imgList.length > 1 ? 1 : 0;

        if (imgList != null && imgList[index] != null) {
            g.drawImage(
                imgList[index],
                x + (width - imgList[index].getWidth()) / 2,
                y + (height - imgList[index].getHeight()) / 2,
                NW_ANCHOR
            );
        }
    }
}
