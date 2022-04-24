package samples.ui;

import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * This class implements user interface chat message display class.
 */
public class ChatBox extends Component {
    private Vector messageList;
    private Vector colorList;
    private Vector cookedMessageList;
    private Vector cookedColorList;
    private Vector tmpList;
    private int cursor;
    private int history;
    private int numLines;
    private int widthOffset;

    /**
     * Create a new ChatBox instance.
     */
    ChatBox() {
        widthOffset = 2;
        focusable = true;
        messageList = new Vector();
        colorList = new Vector();
        cookedMessageList = new Vector();
        cookedColorList = new Vector();
        tmpList = new Vector();
    }

    /**
     * Create a new ChatBox instance with the provided history buffer size.
     *
     * @param history The history buffer size.
     */
    public ChatBox(int history) {
        this();
        this.history = history;
    }

    /**
     * Set the width offset (left/right margin) of this instance.
     *
     * @param widthOffset The width offset (left/right margin) of this instance.
     */
    public void setWidthOffset(int widthOffset) {
        this.widthOffset = widthOffset;
        breakLines();
    }

    /**
     * Get the width offset (left/right margin) of this instance.
     *
     * @return The width offset (left/right margin) of this instance.
     */
    public int getWidthOffset() {
        return widthOffset;
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
        breakLines();
    }

    /**
     * Get the width of this instance.
     *
     * @return The width of this instance.
     */
    public int getWidth() {
        return super.getWidth();
    }

    /**
     * Set the font for this instance.
     *
     * @param font The font for this instance.
     */
    public void setFont(CustomFont font) {
       super.setFont(font);

        numLines = height / font.getHeight();
        breakLines();
    }

    private void breakLine(String message, Integer color) {
        String string;
        int n;

        tmpList.removeAllElements();
        string = new String(message);
        string = string.trim();

        for (;;) {
            if (font.stringWidth(string) <= width - widthOffset) {
                tmpList.addElement(string);
                break;
            }

            n = string.length() - 1;
            while (n >= 0) {
                if (font.substringWidth(string, 0, n) <= width - widthOffset && string.charAt(n) == ' ') break;
                n--;
            }

            if (n > 0) {
                tmpList.addElement(string.substring(0, n));
                string = string.substring(n + 1);
            } else {
                // string has no spaces
                // to break along
                n = 0;
                while (font.substringWidth(string, 0, n) < width - widthOffset) n++;

                tmpList.addElement(string.substring(0, n));
                string = string.substring(n + 1);
            }
        }

        for (int i=0; i<tmpList.size(); i++) {
            cookedMessageList.addElement(tmpList.elementAt(i));
            cookedColorList.addElement(color);
        }
    }

    private void breakLines() {
        String[] list;

        cookedMessageList.removeAllElements();
        cookedColorList.removeAllElements();

        for (int i=0; i<messageList.size(); i++) {
            breakLine(
                (String)messageList.elementAt(i),
                (Integer)colorList.elementAt(i)
            );
        }
    }

    /**
     * Append a text message to the existing message history.
     *
     * @param message The text message to display.
     * @param color The text color for the message.
     */
    public void addEntry(String message, int color) {
        Integer integer;

        integer = new Integer(color);

        messageList.addElement(message);
        colorList.addElement(integer);

        if (messageList.size() >= history) {
            messageList.removeElementAt(0);
            colorList.removeElementAt(0);
        }

        breakLine(message, integer);
        cursor = Math.max(0, cookedMessageList.size() - numLines);
    }

    /**
     * Update the state of this instance in light of a key press action.
     *
     * @param action The type of action. The ChatBox class handles actions
     *               of type Canvas.UP and Canvas.DOWN, and scrolls the
     *               chat message window up or down appropriately.
     * @param key The key pressed.
     * @return true if the instance handled the event, and
     *         false if the instance ignored the event.
     */
    public boolean keyPressed(int action, int key) {
        if (action == Canvas.UP) {
            if (cursor == 0) return false;

            cursor--;
            repaint();

            return true;
        }

        if (action == Canvas.DOWN) {
            if (cursor + numLines >= cookedMessageList.size()) return false;

            cursor++;
            repaint();

            return true;
        }

        return false;
    }

    /**
     * Update the state of this instance in light of a key release action.
     *
     * @param action The type of action. The CharBox class does not handle
     *               any key release actions.
     * @param key The key released.
     * @return false, because the ChatBox class ignores the event.
     */
    public boolean keyReleased(int action, int key) {
        return false;
    }

    /**
     * Paint this instance.
     *
     * @param g The Graphics object to use for painting operations.
     */
    public void paint(Graphics g) {
        String string;
        Integer color;
        int n, yy;

        super.paint(g);

        n = Math.min(numLines, cookedMessageList.size() - cursor);
        yy = y;

        for (int i=cursor; i<cursor+n; i++) {
            string = (String)cookedMessageList.elementAt(i);
            color = (Integer)cookedColorList.elementAt(i);

            paintText(g, color.intValue(), string, x + widthOffset / 2, yy + 1, false, false);

            yy += font.getHeight();
        }

        paintBorder(g);
    }
}
