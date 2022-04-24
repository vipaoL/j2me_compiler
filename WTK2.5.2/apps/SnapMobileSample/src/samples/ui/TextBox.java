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
 * This class implements a text box that can hold and displays
 * multiple rows of text. Text is broken along word boundaries.
 */
public class TextBox extends ScrollableComponent  {
    /** The combined left and righ margin of a text box. */
    public static final int WIDTH_OFFSET = 10;
    /** The unbroken text to be displayed	*/
    private String text;
    /** The text broken into lines for display */
    private String[] cookedText;
    /** Lines of broken text */
    private int numLines;
    private int cursor;
    private ScrollView scroller;

    /**
     * Create a new TextBox instance.
     */
    public TextBox() {
        focusable = true;
        cookedText = new String[0];
    }

    /**
     * Set the ScrollView object for this instance.
     *
     * @param scroller The ScrollView object for this instance.
     */
    public void setScroller(ScrollView scroller) {
        this.scroller = scroller;
    }

    /**
     * Get the ScrollView object for this instance.
     *
     * @return The ScrollView object for this instance.
     */
    public ScrollView getScroller() {
        return scroller;
    }

    private void breakText() {

        if (width <= WIDTH_OFFSET) return;

        String string;
        Vector tmpList;
        char current;
        int girth;
        int space;
        int length;
        int n;

        girth   = width - WIDTH_OFFSET;
        tmpList = new Vector();
        string  = new String(text);
        string  = string.trim();
        string  = string.replace('\t', ' ');
        current = string.charAt(0);
        length  = string.length() - 1;
        space   = 0;
        n       = 0;

        while(n < length)
        {
            // Collect a new line of text
            while( font.substringWidth(string, 0, n + 1) <= girth )
            {
               // System.out.println("Considering: (" + string.substring(0, n + 1) + ")");

                current = string.charAt(n);

                if( current == ' ' )
                {
                    space = n;
                }

                n++;

                // Don't go beyond the end of the source string,
                // and honor any embedded newline character
                if( n > length || current == '\n' )
                {
                    // Don't roll back to the last space
                    space = 0;
                    break;
                }
            }

            // If possible, don't break a line in the middle of word
            if( space > 0 )
            {
                n = space;
            }

            //System.out.println("Added: (" + string.substring(0, n) + ")");
            tmpList.addElement(string.substring(0, n));

            string = string.substring(n);

            // Trim preceeding space only if line is not due to a newline
            if( current != '\n' )
            {
                string = string.trim();
            }

            length = string.length() - 1;
            space  = 0;
            n      = 0;
        }

        cookedText = new String[tmpList.size()];
        for (int i=0; i<cookedText.length; i++) {
            cookedText[i] = (String)tmpList.elementAt(i);
        }

        if (scroller != null) scroller.heightChanged(getTotalHeight());
    }

    /**
     * Set the dimensions of this instance.
     *
     * @param width The width of this instance.
     * @param height The height of this instance.
     */
    public void setDimension(int width, int height) {
        super.setDimension(width, height);
        numLines = height / font.getHeight();
        breakText();
        repaint();
    }

    /**
     * Get the total height of the full text view.
     *
     * @return The total height of the full text view.
     */
    public int getTotalHeight() {    	
//    	System.out.println("text lines: " + cookedText.length + 
//    			", font height: " + font.getHeight());
        return cookedText.length * font.getHeight();
    }

    /**
     * Set the text for this instance.
     *
     * @param text The text for this instance.
     */
    public void setText(String text) {
        //System.out.println("TextBox.setText(): " + text);
        this.text = text;
        breakText();
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
     * Set the font for this instance.
     *
     * @param font The font for this instance.
     */
    public void setFont(CustomFont font) {
        super.setFont(font);

        numLines = height / font.getHeight();
        breakText();
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
        return cursor + numLines < cookedText.length;
    }

    /**
     * Scroll up the text by one line.
     */
    public void scrollUp() {
        cursor--;
        repaint();
    }

    /**
     * Scroll down the text by one line.
     */
    public void scrollDown() {
        cursor++;
        repaint();
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
     * Get the current number of lines of text for this instance.
     *
     * @return The current number of lines of text for this instance.
     */
    public int size() {
        return cookedText.length;
    }

    /**
     * Get the height of the visible text window.
     *
     * @return The height of the visible text window.
     */
    public int getWindowHeight() {
        return numLines * font.getHeight();
    }

    /**
     * Paint this instance.
     *
     * @param g The Graphics object to use for painting operations.
     */
    public void paint(Graphics g) {
        String string;
        Integer color;
        int n, yy, off;

        off = WIDTH_OFFSET / 2;

        super.paint(g);

        n = Math.min(numLines, cookedText.length - cursor);
        yy = y + off;

        int textColor = (focus && focusFontColor != -1) ? focusFontColor : fontColor;
        for (int i=0; i<n; i++) {
            paintText(g, textColor, cookedText[cursor+i], x + off, yy, false, false);
            yy += font.getHeight();
        }

        paintBorder(g);
    }
}
