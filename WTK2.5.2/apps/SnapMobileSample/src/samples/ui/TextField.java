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


/**
 * This class implements an editable text field which can hold and 
 * display a single line of text.  Its cursor can be scrolled left/right 
 * through the text, and various input modes which control the set of 
 * characters that appear when keys are pressed.  An optional "password"
 * display mode masks out the text with asterisks.
 * <p>
 * Input modes: Alphanumeric, Numeric, Email Address, Username, ASCII
 * <p>
 * Display modes: Normal, Password
 */
public class TextField extends Component 
{
	// Key codes
	/** Key code for the handset "left" button */
	public static final int LEFT 					= -3;
	/** Key code for the handset "right" button */
	public static final int RIGHT 					= -4;
	/** Key code for the handset "delete" button (if handset has a full keyboard) */
    public static final int DELETE 					= -8;
	/** Key code for the handset "backspace" button (if handset has a full keyboard) */
    public static final int BACKSPACE 				= 8;
	/** Key code for the handset "back" button */
    public static final int BACK 					= -7;
	/** Key code for the handset "enter" button (if handset has a full keyboard) */
    public static final int ENTER 					= 10;
	/** Key code for the handset "space" button (if handset has a full keyboard) */
    public static final int SPACE 					= 32;
    
    /** Max. milliseconds between cycling keypresses.  Longer than this and
     * a new character is produced
     */ 
    public static final int REPEAT_DELTA			= 600;

    /** Milliseconds between cursor blinks */
    public static final int BLINK_RATE				= 500;

    /** Milliseconds after which, if a user has been holding down a key, 
     * character entered will be replaced with the number of the key pressed.
     */
    public static final int HOLD_FOR_NUMBER_DUR 	= 1000;

    /** Milliseconds after which the last character entered in password 
     * display mode will be replaced with an asterisk.
     */

    public static final int MASK_PASSWORD_CHAR_DUR 	= 1500;

    /** 
     * Alphanumeric entry mode.
     * These are the characters produced by cycling through presses
     * on various handset keys:
	 * 	   <ul>
	 *      <li><b>0</b> key:  space ' ', and 0</li>
	 *      <li><b>1</b> key:  . , ? ! 1 @ ' - _ ( ) : / * % # + < > =</li>
	 *      <li><b>2</b> key:  a b c 2</li>
	 *      <li><b>3</b> key:  d e f 3</li>
	 *      <li><b>4</b> key:  g h i 4</li>
	 *      <li><b>5</b> key:  j k l 5</li>
	 *      <li><b>6</b> key:  m n o 6</li>
	 *      <li><b>7</b> key:  p q r s 7</li>
	 *      <li><b>8</b> key:  t u v 8</li>
	 *      <li><b>9</b> key:  w x y z 9</li>
	 *     </ul>
	 */
    public static final int ENTRY_ALPHANUMERIC 		= 0;
  
    /**
     * Numeric mode. Numbers only (keys 0-9 produce numbers 0-9)
     */
    public static final int ENTRY_NUMERIC 			= 1;
    
    /**
     * Email Address mode. Same as "Alphanumeric" mode, 
     * with the following exceptions:
     * 	   <ul>
     *      <li><b>0</b> key:  only 0</li>
     *      <li><b>1</b> key:  . 1 @ - _ +</li>
     *     </ul>
     */
    public static final int ENTRY_EMAILADDR 		= 2;
    
    /**
    * Username mode.  Same as "Alphanumeric", with the 
    * following exceptions:
    * 	   <ul>
    *      <li><b>0</b> key:  only 0</li>
    *      <li><b>1</b> key:  1 _</li>
    *     </ul>
    */
    public static final int ENTRY_USERNAME 			= 5;

    /**
	 * Full ASCII (ASCII chars 32-126, inclusive).
	 * Same as "Alphanumeric" mode, with the following exceptions:
	 * 	   <ul>
	 *      <li><b>1</b> key:  . , ? ! 1 @ ' - _ ( ) : / * % # + < > = " $ & ; \ [ ] ^ ` | { } ~</li>
	 *     </ul>
     */
    public static final int ENTRY_ASCII 			= 6;
    
    /** Displays TextField contents unhidden */
    public static final int DISP_PLAINTEXT 			= 0;

    /** Displays TextField contents masked by cursors */
    public static final int DISP_PASSWORD 			= 1;

    private static final int DISP_PASSWORD_ENTRY 	= 2;
    
    // Key cycle tables
    private static final String TABLE 				= " .adgjmptw";
    private static final String TABLE_NUMERIC 		= "0123456789";
    private static final String[] REPEAT_TABLE 		= {
        " 0",
        ".,?!1@'-_():/*%#+<>=",
        "abc2",
        "def3",
        "ghi4",
        "jkl5",
        "mno6",
        "pqrs7",
        "tuv8",
        "wxyz9",
    };
    private static final String REPEAT_1KEY_EMAILADDR 	= ".1@-_+";
    private static final String REPEAT_1KEY_USERNAME 	= "1_";
    private static final String REPEAT_1KEY_ASCII 		= ".,?!1@'-_():/*%#+<>=\"$&;\\[]^`|{}~";

    private int maxSize;
    private char[] charArray;
    private int cursor;
    private int nChars;
    private int cursorX;
    private int startChar;
    private int lastKey;
    private boolean keyHeld;
    private boolean showCursor;
    private int repeatCount;
    private int stringWidth;
    private long lastKeyPress;
    private boolean upperCase;
    private boolean qwerty;
    private int entryMode = ENTRY_ALPHANUMERIC;
    private int dispMode = DISP_PLAINTEXT;


    /**
     * Create a new TextField instance with the provided maxiumum character count.
     *
     * @param maxSize The maximum numbers of characters to accept.
     */
    public TextField(int maxSize) {
        this.maxSize = maxSize;
        focusable = true;
        showCursor = true;
        qwerty = false;
        charArray = new char[maxSize+1];
        nChars = 0;
        cursor = 0;
        stringWidth = 0;
        startChar = 0;
    }

    private char toUpper(char c) {
        if (upperCase && c >= 'a' && c <= 'z') c = (char)('A' + (c - 'a'));
        return c;
    }

    
    /**
     * Set the text for this instance.
     *
     * @param text The text for this instance.
     */
    public void setText(String text) 
    {
    	nChars = cursor = Math.min(text.length(), maxSize);

        for (int i=0; i<cursor; i++) {
            charArray[i] = text.charAt(i);
        }

        stringWidth = font.charsWidth(charArray, 0, nChars);
        cursorX 	= font.charsWidth(charArray, 0, cursor);
        if (dispMode==DISP_PASSWORD_ENTRY) dispMode = DISP_PASSWORD;
    }

    /**
     * Get the text for this instance.
     *
     * @return The text for this instance.
     */
    public String getText() {
        return new String( charArray, 0, nChars);
    }

    /**
     * Gets the entry mode for this instance
     * 
     * @return The entry mode for this instance
     */
    public int getEntryMode()
    {
    	return entryMode;
    }

    /**
     * Sets the entry mode for this instance
     * 
     * @param mode The entry mode for this instance
     */
    public void setEntryMode( int mode)
    {
    	this.entryMode = mode;
    }
    
    /**
     * Gets the display mode for this instance
     * 
     * @return The display mode for this instance
     */
    public int getDispMode() {
		return dispMode;
	}

    /**
     * Sets the display mode for this instance
     * 
     * @param dispMode The display mode for this instance
     */
	public void setDispMode(int dispMode) {
		this.dispMode = dispMode;
	}
	
	/**
	 * Turns off "press-for-number" behavior if user was holding
	 * down a key.
	 * 
	 * @param action The Canvas action code of the key released
	 * @param key The keycode of the key released
	 */
    public boolean keyReleased(int action, int key) {
    	keyHeld = false;
        return true;
    }

    /**
     * Whenever the TextField gains focus, it starts an animation thread 
     * which waits for timeouts to perform the following actions:
     * <ol>
     * <li>Blinks the cursor</li>
     * <li>Performs press-for-number behavior if user holds down a key</li>
     * <li>Masks out a recently entered character, if in password display mode</li>
     * </ol>
     * When the TextField loses focus, this thread exits.
     */
    public void setFocus( boolean foc)
    {
    	if (focus==foc) return;
    	focus = foc;
   	
    	if (focus) {
    		
    		// TextField animation thread.  Keeps track of:
    		// 1) Cursor blinking
    		// 2) If key is held down, decides when to turn last 
    		//    entered character into a number.
    		Thread focusThread = new Thread() {
    			public void run() {
    				long startTime = System.currentTimeMillis();
    				long lastBlink = startTime;
    				while (focus) {
    					try { Thread.sleep( 100); } catch (Exception e) {}
    					long time = System.currentTimeMillis();
    					
    					// Check for blink update
    					if ((int)(time-lastBlink) > BLINK_RATE) {
    						lastBlink = time;
    						showCursor = !showCursor;
    						repaint();
    					}
    					// Check for hold-for-number update (flip character to number
    					// if key held down for long enough)
    					if (keyHeld && lastKeyPress>0 && cursor>0 &&
    						(int)(time-lastKeyPress) > HOLD_FOR_NUMBER_DUR &&
    						lastKey >= Canvas.KEY_NUM0 && lastKey <= Canvas.KEY_NUM9
    						)
    					{
    						charArray[cursor-1] = TABLE_NUMERIC.charAt( lastKey-Canvas.KEY_NUM0);
    				    	stringWidth = font.charsWidth( charArray, 0, nChars);
    				    	cursorX		= font.charsWidth( charArray, 0, cursor);
    						keyHeld = false;
    						lastKeyPress = time;
    						repaint();
    					}
    					// Check for masking unmasked (freshly entered) characters in PASSWORD mode
    					if ((int)(time-lastKeyPress) > MASK_PASSWORD_CHAR_DUR &&
    						dispMode == DISP_PASSWORD_ENTRY && cursor>0
    						)
        				{
    						dispMode = DISP_PASSWORD;
    						repaint();
        				}
    				}
    			}
    		};
    		focusThread.start();
    	} 
    }
    
	/**
     * Update the state of this instance in light of a key press action.
     *
     * @param action The type of action. The TextField class ignores
     *               the action parameter, but responds to a wide variety
     *               of key types corresponding to text entry events.
     * @param key The key released.
     * @return true if the instance handled the event, and
     *         false if the instance ignored the event.
     */
    public boolean keyPressed(int action, int key) 
    {
        boolean handled = false;
        int delta = 0;

       // Check if we're using a QWERTY keyboard intead of number pad
        if (key == SPACE || key == BACKSPACE || (key >= 'a' && key <= 'z')) qwerty = true;

        // Check for key-cycling, if last press happened recently enough, and 
        // we're in an entry mode that has key-cycling.
        delta = (int)(System.currentTimeMillis() - lastKeyPress);
        boolean repeat = 
        	entryMode != ENTRY_NUMERIC && 	// Don't repeat in NUMERIC mode
        	delta < REPEAT_DELTA &&			// Don't repeat if last press was too long ago
        	key == lastKey	&&				// Don't repeat if key pressed is different than last one
        	!qwerty &&						// Don't repeat if using QWERTY keyboard
        	!(key==Canvas.KEY_NUM0 && 		// Don't repeat on 0 key in USERNAME or 
        	  (entryMode==ENTRY_USERNAME ||	//  EMAILADDR entry modes
        	   entryMode==ENTRY_EMAILADDR));	
        
       	if (dispMode==DISP_PASSWORD_ENTRY) dispMode=DISP_PASSWORD;
        
        // Left / Right
        if (key == Canvas.LEFT || key == LEFT || key == Canvas.RIGHT || key == RIGHT) {
	        if (key == Canvas.LEFT || key == LEFT) {
	        	if (cursor>0) cursor--;
	        	 if (cursor<startChar) startChar = cursor;
	        } else if (key == Canvas.RIGHT || key == RIGHT) {
	        	if (cursor<nChars) cursor++;
	        	
	        }
	        // Update start char and cursor pos
	       	stringWidth = font.charsWidth( charArray, startChar, nChars-startChar);  
	       	cursorX = font.charsWidth( charArray, startChar, cursor-startChar);  
	       	while (cursorX > getWidth()-textOffsetX-2) {
	       		startChar++;
	       		cursorX = font.charsWidth(charArray,startChar,cursor-startChar);
	       	}
	        repaint();
	       	handled = true;   
        }
 
        // Pound
        else if (key == Canvas.KEY_POUND) {
            upperCase = !upperCase;
            handled = true;
            repaint();
        }

        // Delete / Backspace
        else if (key == DELETE || key == BACKSPACE) {
        	if (cursor > 0) deleteChar( cursor-1);
            handled = true;
            repaint();	        
        }

        // Fire / Enter 
        else if (key == Canvas.FIRE || key == ENTER) {
            notifyListeners(new Event(this, Event.TEXT_CHANGED, new String(charArray, 0, cursor)));
            handled = true;
        }

        // All other keys - either cycle through current key's character options,
        // or place a new character in the text field.
        else {
        	handled = enterCharacter( repeat, key);
        }
        
        lastKey = key;
        keyHeld = true;
        lastKeyPress = System.currentTimeMillis();

        return handled;
    }
    
    /**
     * Handles entry of a character from the keyboard or keypad.
     * The character entered depends on the entry mode, and 
     * keypad cycling behavior (if the time interval since the
     * last keypress was recent enough, then "repeat" will be
     * true and the last entered character will cycle once. 
     * Otherwise, a new character will be inserted at the
     * cursor position.)
     * 
     * @param repeat <code>true</code> if last key entered should
     *  cycle
     * @param key code of key entered
     * @return <code>true</code> if keypressed handled
     */
    private boolean enterCharacter( boolean repeat, int key)
    {
    	String tableEntry = "";
        char c = ' ';
        boolean handled = false;
    	
    	// REPEAT:
        // Use repeat tables for cycling through keys (all modes except NUMERIC)
        if (repeat && key >= Canvas.KEY_NUM0 && key <= Canvas.KEY_NUM9) {
        	
        	// Use different "1" key repeat tables, depending on entry mode
        	if (key == Canvas.KEY_NUM1) {
            	switch (entryMode) {
            	case ENTRY_ALPHANUMERIC:tableEntry = REPEAT_TABLE[1]; 		break;
            	case ENTRY_EMAILADDR:	tableEntry = REPEAT_1KEY_EMAILADDR; break;
            	case ENTRY_USERNAME: 	tableEntry = REPEAT_1KEY_USERNAME; 	break;
            	case ENTRY_ASCII:		tableEntry = REPEAT_1KEY_ASCII; 	break;
            	}
            // For ALPHANUMERIC, EMAILADDR, and DOMAIN modes, keys other than "1" key all
            // share the same repeat tables.
        	} else {
        		tableEntry = REPEAT_TABLE[ key - Canvas.KEY_NUM0];
        	}
            c = tableEntry.charAt(++repeatCount % tableEntry.length());
            setChar( c, cursor-1);
            handled = true;
            repaint();

        // NEW keypress:
        // Pick first entry in current mode's repeat tables, for given key pressed
        } else {
            repeatCount = 0;
            if (cursor <= maxSize && ( key == SPACE || (key >= 'a' && key <= 'z') || 
            	(key >= Canvas.KEY_NUM0 && key <= Canvas.KEY_NUM9))) 
            {
            	// For "1" key, use first char in repeat table for current entry mode
              	if (key == Canvas.KEY_NUM1) {
	            	switch (entryMode) {
	            	case ENTRY_ALPHANUMERIC:c = REPEAT_TABLE[1].charAt(0); 		break;
	            	case ENTRY_NUMERIC: 	c = TABLE_NUMERIC.charAt(1); 		break;
	            	case ENTRY_EMAILADDR:	c = REPEAT_1KEY_EMAILADDR.charAt(0);break;
	            	case ENTRY_USERNAME: 	c = REPEAT_1KEY_USERNAME.charAt(0); break;
	            	case ENTRY_ASCII:		c = REPEAT_1KEY_ASCII.charAt(0);	break;
	            	}
	            // For all other keys: pick chars either from numeric or alphanumeric 
	            // first-char tables
              	} else {
		           	// NUMERIC mode - keys display their digit.  
              		// USERNAME and EMAILADDR modes - zero key only displays '0', never 'space' characer.
                	if (entryMode == ENTRY_NUMERIC || 
                	    (entryMode==ENTRY_USERNAME && key==Canvas.KEY_NUM0) ||
                		(entryMode==ENTRY_EMAILADDR && key==Canvas.KEY_NUM0)
                		)
                		c = qwerty ? (char)key : TABLE_NUMERIC.charAt(key - Canvas.KEY_NUM0);
                	// For all other modes use ALPHANUMERIC's beginning character
                	else 
                		c = qwerty ? (char)key : TABLE.charAt(key - Canvas.KEY_NUM0);
              	}
              	insertChar( c, cursor);
                handled = true;
                repaint();
            }
        }
        
        return handled;
    }
    
    /**
     * Inserts a character at a given location in the string
     * 
     * @param c The character to insert
     * @param loc The index where it should be inserted.
     */
    private void insertChar( char c, int loc)
    {
    	if (nChars >= maxSize) return;
    	if (loc<0 || loc>=maxSize) return;
      	for (int i=nChars; i>loc; i--) charArray[i] = charArray[i-1];
      	nChars++;
    	cursor++;
    	setChar( c, loc);
    }
        
    /**
     * Sets a character at a given location in the string
     * 
     * @param c The character to set
     * @param loc The index of the character to be overwritten with 'c'
     */
    private void setChar( char c, int loc)
    {
    	if (loc<0 || loc>=maxSize) return;
    	c = toUpper(c);
      	charArray[ loc] = c;
    	if (dispMode==DISP_PASSWORD) dispMode=DISP_PASSWORD_ENTRY;
    	stringWidth = font.charsWidth( charArray, startChar, nChars-startChar);
       	cursorX = font.charsWidth( charArray, startChar, cursor-startChar);  
       	while (cursorX > getWidth()-textOffsetX-2) {
       		startChar++;
       		cursorX = font.charsWidth(charArray,startChar,cursor-startChar);
       	}
    }

    /**
     * Removes a character at a given location in the string.  The
     * string will be compacted to close the gap.
     * @param loc The index where it should be removed.
     */
    private void deleteChar( int loc)
    {
    	if (loc<0 || loc>=maxSize) return;
        for (int i=loc; i<nChars-1; i++) charArray[i] = charArray[i+1];
        cursor--;
        nChars--;
        if (startChar>0) startChar--;
    	stringWidth = font.charsWidth( charArray, startChar, nChars-startChar);
       	cursorX = font.charsWidth( charArray, startChar, cursor-startChar);  
    	if (dispMode==DISP_PASSWORD_ENTRY) dispMode=DISP_PASSWORD;
    }

    /**
     * Paint this instance.
     *
     * @param g The Graphics object to use for painting operations.
     */
    public void paint(Graphics g) {
        int yoff;

        super.paint(g);

        yoff = (height - font.getHeight()) / 2 + textOffsetY;
//        g.setFont(font);

        int color = (focus && focusFontColor != -1) ? focusFontColor : fontColor;
       	g.setColor( color);
       	char[] chars = charArray;

       	// Blank out most or all of the password with asterisks if display 
       	// mode is type PASSWORD. 
       	if ((dispMode==DISP_PASSWORD || dispMode==DISP_PASSWORD_ENTRY) && nChars>0) {
       		chars = new char[nChars+1];
       		for (int i=0; i<nChars; i++) chars[i] = '*';
       		// Leave most recently entered character unmasked if user has just entered it.       		
       		if (dispMode==DISP_PASSWORD_ENTRY && focus && cursor>0) 
       			chars[cursor-1] = charArray[cursor-1];
	       	chars[nChars] = 0;
            stringWidth = font.charsWidth( chars, startChar, nChars-startChar);       		
            cursorX 	= font.charsWidth( chars, startChar, cursor-startChar);       		
       	}

       	g.setClip( x, y, getWidth()-1, getHeight());	// clip tight for text
//       	g.drawChars( chars, startChar, nChars-startChar, x + textOffsetX, y + yoff, NW_ANCHOR);
       	font.drawChars( g, chars, startChar, nChars-startChar, x + textOffsetX, y + yoff);

       	// Draw cursor if text field has focus, and cursor is in "blink on"
        if (focus && showCursor) {
            g.drawLine(
            		x + cursorX + textOffsetX, 
            		y + 2, 
            		x + cursorX + textOffsetX, 
            		y + height - 3
            		);
        }

        g.setClip( x-1, y-1, width+3, height+3);	// clip wide for border/shadow
        paintBorder(g);
    }
}
