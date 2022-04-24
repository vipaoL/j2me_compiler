package samples.ui;

import javax.microedition.lcdui.*;

/**
 * This class implements a vertically scrollable view
 * for scrollable components.
 *
 * @see ScrollableComponent
 */
public class ScrollView extends Component {
    private static final int TOP    = 0;
    private static final int BODY   = 1;
    private static final int BOTTOM = 2;

    private Image upArrow;
    private Image downArrow;
    private Image barTemplate;
    private Image[] bar;
    private ScrollableComponent scrollable;
    private int barY;

    /*
     * Create a new instance of this class.
     *
     * @param upArrow An image that indicates the view can scroll up.
     * @param downArrow An image that indicates the view can scroll down.
     * @param bar An image array that is used to create a scroll bar. The
     *            image at position 0 represents the top of the bar. At
     *            position 1, this method expects a horizontal slice of
     *            the body of the scroll bar that is exactly one pixel high.
     *            At position 2, this method expects the bottom of the scroll
     *            bar.
     * @param scrollable The scrollable component to be managed by this instance.
     */
    public ScrollView(Image upArrow, Image downArrow, Image[] bar, ScrollableComponent scrollable) {
        this.upArrow = upArrow;
        this.downArrow = downArrow;
        this.bar = bar;
        this.scrollable = scrollable;

        focusable = true;
        barTemplate = bar[BODY];
        scrollable.setScroller(this);
        scrollable.setFocusable(false);
    }

    /**
     * Set the View object associated with this instance.
     *
     * @param view The View object associated with this instance.
     */
    public void setView(View view) {
        super.setView(view);

        scrollable.setView(view);
    }

    /*
     * Scrollable components need to call this method when their height changes.
     *
     * @param hh The new height (in pixels) of the contained component.
     */
    public void heightChanged(int hh) {
        int barHeight = scrollable.getWindowHeight() * height / hh;

/*        
        System.out.println("barTemplate.getWidth() = " + barTemplate.getWidth());
        System.out.println("scrollable.getWindowHeight() = " + scrollable.getWindowHeight());
        System.out.println("height = " + height);
        System.out.println("hh = " + hh);
        System.out.println("barHeight = " + barHeight);
*/
        if (barHeight > height) barHeight = height;
        barHeight -= (bar[TOP].getHeight() + bar[BOTTOM].getHeight());
        //System.out.println("adjusted barHeight = " + barHeight);
        bar[BODY] = Image.createImage(barTemplate.getWidth(), barHeight);

        Graphics g = bar[BODY].getGraphics();
        g.drawImage(
            barTemplate,
            0,
            0,
            NW_ANCHOR
        );
    }

    /**
     * Set the dimensions of this instance.
     *
     * @param width The width of this instance.
     * @param height The height of this instance.
     */
    public void setDimension(int width, int height) {
        super.setDimension(width, height);
        scrollable.setDimension(width, height);
    }

    /**
     * Set the postion of this instance.
     *
     * @param x The X coordinate of the lower left corner of this instance.
     * @param y The Y coordinate of the lower left corner of this instance.
     */
    public void setLocation(int x, int y) {
        super.setLocation(x, y);
        scrollable.setLocation(x, y);
    }

    /**
     * Update the state of this instance in light of a key press action.
     *
     * @param action The type of action. The ScrollView class handles actions
     *               of type Canvas.UP and Canvas.DOWN, and scrolls the
     *               associate View object up or down appropriately.
     * @param key The key pressed.
     * @return true if the instance handled the event, and
     *         false if the instance ignored the event.
     */
    public boolean keyPressed(int action, int key) {
        //System.out.println("ScrollView.keyPressed(), key = " + key);

        if (action == Canvas.UP) {
            if (scrollable.canScrollUp()) {
                scrollable.scrollUp();
                barY = scrollable.getCursor() * height / scrollable.size();
                repaint();

                return true;
            }
        }

        if (action == Canvas.DOWN) {
            if (scrollable.canScrollDown()) {
                scrollable.scrollDown();
                barY = scrollable.getCursor() * height / scrollable.size();
                repaint();

                return true;
            }
        }

        return false;
    }

    /**
     * Paint this instance.
     *
     * @param g The Graphics object to use for painting operations.
     */
    public void paint(Graphics g) {
        int yy;

        scrollable.paint(g);
        
        g.setClip( 0, 0, 
        		scrollable.getView().getCanvas().getWidth(), 
        		scrollable.getView().getCanvas().getHeight()
        		);

        if (scrollable.canScrollUp()) {
            g.drawImage(
                upArrow,
                x + (width - upArrow.getWidth()) / 2,
                y - upArrow.getHeight() / 2 + 2,
                NW_ANCHOR
            );
        }

        if (scrollable.canScrollUp() || scrollable.canScrollDown()) {
            yy = y + barY;

            g.drawImage(
                bar[TOP],
                x + width - bar[TOP].getWidth() / 2,
                yy,
                NW_ANCHOR
            );

            yy += bar[TOP].getHeight();

            g.drawImage(
                bar[BODY],
                x + width - bar[BODY].getWidth() / 2,
                yy,
                NW_ANCHOR
            );

            yy += bar[BODY].getHeight();

            g.drawImage(
                bar[BOTTOM],
                x + width - bar[BOTTOM].getWidth() / 2,
                yy,
                NW_ANCHOR
            );
        }

        if (scrollable.canScrollDown()) {
            g.drawImage(
                downArrow,
                x + (width - downArrow.getWidth()) / 2,
                y + height - downArrow.getHeight() / 2 + 2,
                NW_ANCHOR
            );
        }
    }
}
