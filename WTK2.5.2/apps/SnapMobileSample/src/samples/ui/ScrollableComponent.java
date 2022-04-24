package samples.ui;

import javax.microedition.lcdui.*;

/**
 * This class is a subclass of the base Component class. Classes
 * that want to be displayed in a ScrollView need to extend this class.
 *
 * @see Component
 * @see ScrollView
 */
public abstract class ScrollableComponent extends Component {

    /**
     * Query whether the instance can scroll up further or not.
     *
     * @return Whether the instance can scroll up further.
     */
    public abstract boolean canScrollUp();

    /**
     * Query whether the instance can scroll down further or not.
     *
     * @return Whether the instance can scroll down further.
     */
    public abstract boolean canScrollDown();

    /**
     * Set the ScrollView object for this instance.
     *
     * @param scroller The ScrollView object for this instance.
     */
    public abstract void setScroller(ScrollView scroller);

    /**
     * Scroll up by one increment.
     */
    public abstract void scrollUp();

    /**
     * Scroll down by one increment.
     */
    public abstract void scrollDown();

    /**
     * Get the total height of the full underlying view.
     *
     * @return The total height of the full underlying view.
     */
    public abstract int getTotalHeight();

    /**
     * Get the height of the visible window.
     *
     * @return The height of the visible window.
     */
    public abstract int getWindowHeight();

    /**
     * Get the current cursor position for this instance.
     *
     * @return The current cursor position for this instance.
     */
    public abstract int getCursor();

    /**
     * Get the current number of lines (cursor positions) for this instance.
     *
     * @return The current number of lines (cursor positions) for this instance.
     */
    public abstract int size();
}
