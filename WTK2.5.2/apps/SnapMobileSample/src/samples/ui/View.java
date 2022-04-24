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
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * This class implements the concept of a view. See the
 * documentation for the <code>ViewCanvas</code> class for more details.
 *
 * @see ViewCanvas
 */
public class View {
    /* Key code for left soft button. */
    public static final int LEFT_SOFT_BUTTON  = -6;

    /* Key code for right soft button. */
    public static final int RIGHT_SOFT_BUTTON = -7;

    /* Key code for right enter/fireoft button. */
    public static final int ENTER_BUTTON      = -5;

    /* RGB value for default title color. */
    public static int TITLE_COLOR = 0x00787d84;

    private static final int ANY   = 0;
    private static final int NORTH = 1;
    private static final int SOUTH = 2;
    private static final int EAST  = 3;
    private static final int WEST  = 4;

    private static final int MARGIN  = 5;

    protected int debugKey = 0;

    private static Image blip;
    private static int blipX;
    protected static boolean waiting;
    
    protected static String test_font = "Font1";

    //protected static Font SOFT_BUTTON_FONT = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
    //protected static Font TITLE_FONT = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);

    protected static CustomFont SOFT_BUTTON_FONT = new CustomFont(test_font);
    protected static CustomFont TITLE_FONT = new CustomFont(test_font);
    
    protected Canvas canvas;
    protected Vector children;
    protected Component focus;
    protected String name;
    protected String leftSoft;
    protected String rightSoft;
    protected Image bgImg;
    protected int background;
    protected int highlight;
    protected int titleX;
    protected boolean showTitle;
    protected boolean active;
    private int blipStart;
    private int blipStop;
    protected int softkeyColor = TITLE_COLOR;

    /** Perform initialization for this class. */
    public static void initialize() {
        try {
            blip = Image.createImage("/progress_blip.png");
        } catch (Exception e) {}
        blipX = MARGIN;
    }

    /**
     * Create a new instance of this class.
     *
     * @param canvas The canvas that this view is associated with.
     * @param name The name of this view.
     */
    public View( Canvas canvas, String name) {
        this.name = name;
        this.canvas = canvas;
        children = new Vector();
        background = 0x00ffffff;
        highlight  = 0x00ff0000;
        showTitle  = true;
        titleX 	= 0;
        blipStart = 0;
        /*if (blip!=null) blipStop = canvas.getWidth()-blip.getWidth();
        else blipStop = canvas.getWidth(); */ //May need this for IN-game reg */
        blipStop = 20;
    }

    /**
     * Sets the Canvas for this instance
     *
     * @param canvas The Canvas for this instance
     */
    public void setCanvas( Canvas canvas)
    {
        this.canvas = canvas;
    }

    /**
     * Gets the Canvas for this instance
     *
     * @return The Canvas for this instance
     */
    public Canvas getCanvas()
    {
        return canvas;
    }

    /**
     * Gets the flag which determines whether the title of the View
     * is displayed.
     *
     * @return The flag determining whether to show the title of this View
     */
    public boolean isShowTitle()
    {
        return showTitle;
    }

    /**
     * Sets the flag which determines whether the title of the View
     * is displayed.
     *
     * @param show The flag determining whether to show the title of this View
     */
    public void setShowTitle( boolean show)
    {
        this.showTitle = show;
    }
    
    /**
     * Sets the Font used for drawing the soft button labels on this View
     *
     * @param font The Font used for drawing the soft button labels
     */
    public void setSoftButtonFont(CustomFont font)
    {
        if (font != null) {
            SOFT_BUTTON_FONT = font;
        }
    }

    /**
     * Sets the Font used for drawing the title of this View
     *
     * @param font The Font used for drawing the title of this View
     */
    public void setTitleFont(CustomFont font)
    {
        if (font != null) {
            TITLE_FONT = font;
        }
    }

    /**
     * Set the name of this instance.
     *
     * @param name The new name of the instance.
     */
    public void setName(String name) {
        this.name = name;
        repaint();
    }


    /**
     * Gets the name of this instance.
     *
     * @return name The name of this instance
     */
    public String getName() {
        return name;
    }


    /**
     * Gets the name of this instance.
     *
     * @return name The name of this instance
     */
    public boolean hasName( String name) {
        if (this.name == null || name == null ) return false;
        return name.equals(this.name);
    }


    /**
     * Sets the color used to draw soft key labels for this instance.
     *
     * @param color The color used to draw soft key labels
     */
    public void setSoftkeyColor( int color)
    {
        this.softkeyColor = color;
    }

    /**
     * Gets the color used to draw soft key labels for this instance.
     *
     * @return The color used to draw soft key labels
     */
    public int getSoftkeyColor()
    {
        return softkeyColor;
    }

    /**
     * This method is called by the managing canvas just before
     * a view becomes active, and just after it becomes inactive.
     *
     * @param active Whether not this instance is active
     */
    public void setActive(boolean active) {
        //System.out.println("View.setActive(): " + this.active);
        this.active = active;

        if (!active) setWaiting(false);
    }

    /**
     * Return <code>true</code> if this instance is currently active,
     * <code>false</code> otherwise.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Set the idle state of this instance. If the argument is
     * <code>true</code>, then an animated progess bar is displayed
     * at the bottom of this view. If the argument is <code>false
     * </code>, then the progress bar is turned off.
     *
     * @param w Whether or not this instance is idle.
     */
    public void setWaiting(boolean w)
    {
        if (waiting == w) return;
        waiting = w;
        if (waiting) {
            Thread thread = new Thread() {
                public void run() {
                    int xd = 3;
                   blipX = blipStart; // (blipStop-blipStart-blip.getWidth())/2;

                    while (waiting) {
                        blipX += xd;

                        if (blipX < blipStart || blipX + blip.getWidth() > blipStop) {
                            xd = -xd;
                            blipX += 2 * xd;
                        }

                        repaint();

                        try {Thread.sleep(100);}
                        catch (InterruptedException e) {}
                    }
                }
            };
            thread.start();
        }

        repaint();
        //System.out.println("View.setWaiting(): " + waiting);
    }

    /** Return whether or not this instance is idle. */
    public boolean isWaiting() {
        return waiting;
    }

    /**
     * Add a component to this instance.
     *
     * @param comp The component to add.
     */
    public void add(Component comp) {
        //System.out.println("View.add(): " + comp);
        comp.setView(this);

        if (focus == null && comp.isFocusable()) {
            focus = comp;
            focus.setFocus(true);
        }

        if (!contains(comp)) {
            children.addElement(comp);
        }

        repaint();
    }

    /**
     * Remove a component from this instance.
     *
     * @param comp The component to remove.
     */
    public void remove(Component comp) {
        //System.out.println("View.remove(), this = " + this + ", comp =  " + comp);
        comp.setView(null);

        if (focus == comp) {
            focus = findClosestNeighbor(focus, ANY);
            focus.setFocus(true);
            //System.out.println("new focus: " + focus);
        }

        //System.out.println("children before remove: " + children);
        children.removeElement(comp);
        //System.out.println("children after remove: " + children);

        repaint();
    }

    /**
     * Return whether or not this instance contains
     * a specific component.
     *
     * @param comp The component to check for.
     */
    public boolean contains(Component comp) {
        return children.contains(comp);
    }

    /**
     * Set focus to a specific component.
     *
     * @param comp The component to focus on.
     */
    public void setFocus(Component comp) {
        //System.out.println("View.setFocus(): " + comp);
        if (comp.isFocusable() && children.contains(comp)) {
            focus = comp;
            focus.setFocus(true);
        }
    }

    /** Return the component that currently has focus. */
    public Component getFocus() {
        return focus;
    }

    /**
     * Set the label for the left soft button. Setting the label
     * to <code>null</code> will disable the soft button.
     *
     * @param leftSoft The label for the button.
     */
    public void setLeftSoftButton( String leftSoft) {
        this.leftSoft = leftSoft;

        blipStart = leftSoft == null ? 0 : SOFT_BUTTON_FONT.stringWidth(leftSoft) + 2 * MARGIN;
        blipX = blipStart;

        repaint();
    }

    /** Return the label for the left soft button. */
    public String getLeftSoftButton() {
        return leftSoft;
    }

    /**
     * Set the label for the right soft button. Setting the label
     * to <code>null</code> will disable the soft button.
     *
     * @param rightSoft The label for the button.
     */
    public void setRightSoftButton( String rightSoft) {
        this.rightSoft = rightSoft;

        blipStop = rightSoft == null ? getWidth() : getWidth() - SOFT_BUTTON_FONT.stringWidth(rightSoft) - 2 * MARGIN;
        repaint();
    }

    /** Return the label for the right soft button. */
    public String getRightSoftButton() {
        return rightSoft;
    }

    /**
     * Set the highlight color for the focus component.
     *
     * @param highlight The RGB color value to use as a highlight.
     */
    public void setHighlight(int highlight) {
        this.highlight = highlight;
    }

    /** Get the highlight color for the focus component. */
    public int getHighlight() {
        return highlight;
    }

    /**
     * Set the background color for this instance.
     *
     * @param background The RGB color value to use as a background.
     */
    public void setBackground(int background) {
        this.background = background;
    }

    /** Get the background color for this instance. */
    public int getBackground() {
        return background;
    }

    /**
     * Set the background image for this instance.
     *
     * @param bgImg The image to use as a background.
     */
    public void setBackgoundImage(Image bgImg) {
        this.bgImg = bgImg;
    }

    /** Get the background image for this instance. */
    public Image getBackgroundImage() {
        return bgImg;
    }

    // Adapted from atoms.alife.co.uk/sqrt, where it says:
    // "This code has been placed in the public domain."
    private static int sqrt(int x) {
        int v;
        int t = 1 << 30;
        int r = 0;
        int s;

        for (int i=0; i<16; i++) {
            s = t + r;
            r >>= 1;

            if (s <= x) {
                x -= s;
                r |= t;
            }

            t >>= 2;
        }

        return r;
    }

    private int getDistance(int a, int b, int oa, int ob, int distance) {
        int aa, bb, dd;

        if (oa < a) return -1;

        aa = Math.abs(a - oa);
        bb = Math.abs(b - ob);

        if (bb > aa) return -1;

        dd = sqrt(aa * aa + bb * bb);
        if (dd >= distance) return -1;

        return dd;
    }

    /**
     * Returns the first focusable component in the View (listed vertically,
     * from top of bottom).
     *
     * @return The first focusable component in the view
     */
    protected Component getFirstFocusable() {
        Component comp;

        //System.out.println("Container.getFirstFocusable()");

        for (int i=0; i<children.size(); i++) {
            comp = (Component)children.elementAt(i);
            if (comp.isFocusable()) return comp;
        }

        return null;
    }

    /**
     * Returns the last focusable component in the View (listed vertically,
     * from top of bottom).
     *
     * @return The last focusable component in the view
     */
    protected Component getLastFocusable() {
        Component comp;

        //System.out.println("Container.getLastFocusable()");

        for (int i=children.size()-1; i>=0; i--) {
            comp = (Component)children.elementAt(i);
            if (comp.isFocusable()) return comp;
        }

        return null;
    }

    /**
     * Given a component in the View, returns the next focusable component
     * (listed vertically, from top of bottom).  If there isn't a "next"
     * one, then wraps the focus back around to the top and returns the
     * first focusable component in the view.
     *
     * @return The next focusable component in the view
     */
    protected Component getNextFocusable( Component cur) {
        Component comp, next;

        //System.out.println("Container.getLastFocusable()");

        next = getFirstFocusable();
        for (int i=children.size()-1; i>=0; i--) {
            comp = (Component)children.elementAt(i);
            if (comp == cur) return next;
            if (comp.isFocusable()) next = comp;
        }

        return getLastFocusable();
    }

    /**
     * Given a component in the View, returns the previous focusable
     * component (listed vertically, from top of bottom).  If there
     * isn't a "previous" one, then wraps the focus around to the
     * bottom and returns the last focusable component in the view.
     *
     * @return The previous focusable component in the view
     */
    private Component getPrevFocusable( Component cur) {
        Component comp, prev;

        prev = getLastFocusable();
        for (int i=0; i<children.size(); i++) {
            comp = (Component)children.elementAt(i);
            if (comp == cur) return prev;
            if (comp.isFocusable()) prev = comp;
        }

        return getFirstFocusable();
    }

    private Component findClosestNeighbor(Component comp, int dir) {
        Component neighbor, c;
        int distance, i, dd, xx, yy;
        if (comp == null) {
            if (dir == NORTH || dir == WEST) return getFirstFocusable();
            else return getLastFocusable();
        }

        distance = Integer.MAX_VALUE;
        neighbor = null;

        for (i=0; i<children.size(); i++) {
            c = (Component)children.elementAt(i);
            if (c == comp || !c.isFocusable()) continue;

            switch (dir) {
                case ANY:
                    xx = Math.abs(comp.getX() - c.getX());
                    yy = Math.abs(comp.getY() - c.getY());
                    dd = sqrt(xx * xx + yy * yy);
                    break;

                case NORTH: dd = getDistance(c.getY(), c.getX(), comp.getY(), comp.getX(), distance); break;
                case SOUTH: dd = getDistance(comp.getY(), comp.getX(), c.getY(), c.getX(), distance); break;
                case WEST:  dd = getDistance(c.getX(), c.getY(), comp.getX(), comp.getY(), distance); break;
                case EAST:  dd = getDistance(comp.getX(), comp.getY(), c.getX(), c.getY(), distance); break;
                default: throw new IllegalArgumentException("illegal direction: " + dir);
            }

            if (dd >= 0 && dd < distance) {
                distance = dd;
                neighbor = c;
            }
        }

        return neighbor == null ? comp : neighbor;
    }

    /**
     * Handle key presses. If a subclass overrides this method,
     * it should always make a call to the superclass implementation
     * to avoid unexpected behavior.
     *
     * @param key The key code for the key that was pressd.
     */
    public void keyPressed(int key) {
        Component nfocus;
        boolean handled;
        int action = 0;

        // In case DELETE isn't supported as a valid keycode to
        // be passed to getGameAction on a particular handset.
        try {
            action = getGameAction( key);
        } catch (Exception e) {}

        handled = false;
        nfocus = focus;

        if (focus != null) handled = focus.keyPressed( action, key);

        if (!handled) {
            debugKey = action;
            if (action == Canvas.UP || action == Canvas.LEFT  ) {
                debugKey = 22;
                nfocus = getPrevFocusable( focus);
            }
            if (action == Canvas.DOWN || action == Canvas.RIGHT) {
                debugKey = 33;
                nfocus = getNextFocusable( focus);
            }

            if (nfocus != focus) {
                focus.setFocus(false);
                focus = nfocus;
                focus.setFocus(true);
                handleFocusChanged();      
                repaint();
            }

            if (key == LEFT_SOFT_BUTTON && leftSoft != null) {
                leftSoftButtonPressed( leftSoft);
            }

            if (key == RIGHT_SOFT_BUTTON && rightSoft != null) {
                rightSoftButtonPressed( rightSoft);
            }
        }
        return;
     }
    
    /**
     * Called when child widget focus changes, in case Views would 
     * like to add special behavior.  View's implementation does
     * nothing, subclasses must override.
     *
     */
    public void handleFocusChanged() {}
    

    /**
     * Handle key releases. If a subclass overrides this method,
     * it should always make a call to the superclass implementation
     * to avoid unexpected behavior.
     *
     * @param key The key code for the key that was pressd.
     */
    public  void keyReleased(int key) {
        if (focus != null) focus.keyReleased(getGameAction(key), key);
    }

    /**
     * Callback method for left soft button presses. The base implementation
     * of this method does nothing. Subclasses need to override it to achieve
     * useful behavior.
     */
    public void leftSoftButtonPressed(String label)
    {
    }

    /**
     * Callback method for right soft button presses. The base implementation
     * of this method does nothing. Subclasses need to override it to achieve
     * useful behavior.
     */
    public void rightSoftButtonPressed(String label)
    {
    }

    /** Get the witdth of this instance. */
    public int getWidth() {
        return canvas.getWidth();
    }

    /** Get the height of this instance. */
    public int getHeight() {
        return canvas.getHeight();
    }

    /** Repaint this instance. */
    public void repaint() {
        canvas.repaint();
    }

    /**
     * Get the game action associated with a specific key code.
     * @see Canvas#getGameAction
     */
    public int getGameAction(int key) {
        return canvas.getGameAction(key);
    }

    private String getDirectionAsString(int dir)
    {
        if (dir == ANY  ) return "ANY";
        if (dir == NORTH) return "NORTH";
        if (dir == SOUTH) return "SOUTH";
        if (dir == WEST ) return "WEST";
        if (dir == EAST ) return "EAST";

        throw new IllegalArgumentException("illegal direction: " + dir);
    }

    protected void paintLeftSoftButton(Graphics g) {
        if (leftSoft != null) {
            g.setColor(TITLE_COLOR);
/*
//          g.setFont(SOFT_BUTTON_FONT);
        	g.drawString(
                leftSoft,
                MARGIN,
                getHeight() - SOFT_BUTTON_FONT.getHeight(),
                Component.NW_ANCHOR
            );
*/            
        	SOFT_BUTTON_FONT.drawString(
        			g,
                    leftSoft,
                    MARGIN,
                    getHeight() - SOFT_BUTTON_FONT.getHeight()
                );
        }
    }

    protected void paintRightSoftButton(Graphics g) {
        int sw;

        if (rightSoft != null) {
            g.setColor(TITLE_COLOR);
/*
//          g.setFont(SOFT_BUTTON_FONT);
            g.drawString(
                rightSoft,
                getWidth() - sw - MARGIN,
                getHeight() - SOFT_BUTTON_FONT.getHeight(),
                Component.NW_ANCHOR
            );
*/
            sw = SOFT_BUTTON_FONT.stringWidth( rightSoft);
            SOFT_BUTTON_FONT.drawString(
            		g,
                    rightSoft,
                    getWidth() - sw - MARGIN,
                    getHeight() - SOFT_BUTTON_FONT.getHeight()
                );            
        }
    }

    protected void paintWaiting(Graphics g) {
        int yy;

        if (waiting) {
            yy = getHeight() - 10;

            g.setColor(TITLE_COLOR);
            g.drawLine(blipStart, yy, blipStop, yy);
            g.drawImage(blip, blipX, yy - blip.getHeight() / 2, Component.NW_ANCHOR);
        }
    }

    /**
     * Paint this instance.
     *
     * @param g The Graphics object to use for painting operations.
     */
    public void paint(Graphics g)
    {
        Component comp;

        g.setClip( 0, 0, getWidth(), getHeight());
        g.setColor(background);
        g.fillRect(0, 0, getWidth(), getHeight());

        if (bgImg != null) {
            g.drawImage(

                bgImg,
                0 + (getWidth() - bgImg.getWidth()) / 2,
                0 + (getHeight() - bgImg.getHeight()) / 2,
                Component.NW_ANCHOR
            );
        }

        if (showTitle && name != null) {
//            g.setFont(TITLE_FONT);
            g.setColor(TITLE_COLOR);
/*            
            g.drawString(
                name,
                5,
                0,
                Component.NW_ANCHOR
            );
*/            
            TITLE_FONT.drawString(
            		g,
                    name,
                    titleX,
                    0
                );
        }

        // Paint each component, except the in-focus component,
        // which is drawn later.
        for (int i=0; i<children.size(); i++) {
            comp = (Component)children.elementAt(i);
            if (comp == focus) continue;
            comp.paint( g);
        }

        g.setClip( 0, 0, getWidth(), getHeight());
        paintLeftSoftButton(g);
        paintRightSoftButton(g);
        paintWaiting(g);

        // Paint the selected component last, potentially on top of
        // the other components (e.g. a selected List widget may be
        // expanded into its full drop-down menu state, which
        // will overlap other components.)
        if (focus != null) {
            focus.paint(g);
        }
    }
}
