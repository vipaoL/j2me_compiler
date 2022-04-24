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

package samples.commui;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import samples.ui.CustomFont;
import samples.ui.TextBox;

/**
 * This class implements visualization for the maze racer game.
 */
public class MazeRacerView extends CommunityView {
    public static final int CELL_SIZE = 4;
    public static final int ANCHOR = Graphics.TOP | Graphics.LEFT;

    private static final String LEVEL_WINS = "level wins (you/";
    private static final String LEVEL_WINS_2 = "): ";

    private MazeRacer mazeRacer;
    private Maze maze;
    private CustomFont font;
    private Player[] playerList;
    private Image background;
    private Image[] imgList;
    private String ticker;
    private int hoff;
    private int voff;
    private int tickerX;
    private int tickerWidth;
    private int lww;

    /**
     * Create a new instance of this class.
     * @param community Community instance
     * @param mazeRacer Game engine
     * @param maze The actual maze
     * @param playerList A list of players
     */
    public MazeRacerView(Community community, MazeRacer mazeRacer, Maze maze, Player[] playerList) {
        super(community, null);

        this.mazeRacer = mazeRacer;
        this.maze = maze;
        this.playerList = playerList;

        font = new CustomFont("7pt-proportional");
        lww = font.stringWidth(LEVEL_WINS);
        showTitle = false;
        setRightSoftButton(Community.BACK);

        hoff = (getWidth() - maze.getWidth() * CELL_SIZE) / 2;
        voff = 4 + font.getHeight();

        background = Image.createImage(
            getWidth(),
            getHeight()
        );

        Image img = null;

        try {img = Image.createImage(getClass().getResourceAsStream("/wall_tiles.png"));}
        catch (IOException e) {e.printStackTrace();}

        tickerX = getWidth();
        imgList = new Image[img.getWidth() / CELL_SIZE];

        for (int i=0; i<imgList.length; i++) {
            imgList[i] = Image.createImage(img, CELL_SIZE * i, 0, CELL_SIZE, img.getHeight(), 0);
        }
    }

    public CustomFont getFont() {
        return font;
    }

    /**
     * Activate or deactivate this view.
     * @param active Activate if <code>true</code>, otherwise desctivate
     */
    public void setActive(boolean active) {
        super.setActive(active);

        if (active && !mazeRacer.isPlaying()) {
            Thread thread = new Thread(mazeRacer);
            thread.start();
        } else {
//        	mazeRacer.gameOver( false);
//            mazeRacer.stop();
        }
    }

    /**
     * Set the view ticker.
     * @param ticker The ticker message. May be <code>null</code>.
     */
    public void setTicker(String ticker) {
        this.ticker = ticker;
        //System.out.println("ticker: " + ticker);
        tickerX = getWidth();
        tickerWidth = ticker == null ? 0: font.stringWidth(ticker);
    }

    /**
     * Handle key presses.
     * @param key The key code of the pressed key.
     */
    public void keyPressed(int key) {
        super.keyPressed(key);

        mazeRacer.keyPressed(key, getGameAction(key));
    }

    /**
     * Handle key releases.
     * @param key The key code of the released key.
     */
    public void keyReleased(int key) {
        super.keyReleased(key);

        mazeRacer.keyReleased(key, getGameAction(key));
    }

    /**
     * Handle right soft button presses.
     * @param label The label of the soft button.
     */
    public void rightSoftButtonPressed(String label) {
    	community.showDialog( 
    			"Exit Game", "Are you sure you want to exit the game?",
    			null, Community.MAZEEXIT, Dialog.YES_NO
    			);
/*    	
        TextBox textBox = new TextBox();
        textBox.setText(  "Are you sure you want to exit the game?");
        textBox.setBackground(0x00ffffff);
        textBox.setForeground(0x00c0c0c0);
        textBox.setFocusable(false);
        textBox.setDimension(canvas.getWidth() - 10, 12 * textBox.getFont().getHeight() + TextBox.WIDTH_OFFSET);
        Dialog dialog = new Dialog(
                community,
                "Exit Game",
                null,
                Community.YES,
                Community.NO,
                textBox,
                textBox,
                Dialog.YES_NO,
                Community.MAZEEXIT
            );
        community.switchToView( dialog, true);
*/
    }

    private void paintBackground(Graphics g) {
        int i, n, w;

        g.setColor(0x00ffffff);
        g.fillRect(0, 0, getWidth(), getHeight());


        g.setColor(0x000000ff);

        n = maze.getSize();
        w = maze.getWidth();

        for (i=0; i<n; i++) {
            if (maze.isWall(i)) {
                g.drawImage(
                    imgList[maze.getTileCode(i)],
                    i % w * CELL_SIZE + hoff,
                    i / w * CELL_SIZE + voff,
                    ANCHOR
                );
            }
        }
    }

    /**
     * Paint this instance.
     * @param g The graphics context
     */
    public void paint(Graphics g) {
        Player player;
        char[] level, time, wins;
        int i, n, sw, yy;

        if (maze.isChanged()) {
            paintBackground(background.getGraphics());
        }

        g.drawImage(background, 0, 0, Graphics.TOP | Graphics.LEFT);

        if (ticker != null) {
            font.setColor(0xffe00020);
            font.drawString(g, ticker, tickerX, 3);
            tickerX -= 2;
            if (tickerX + tickerWidth < 0) tickerX = getWidth();
        }

        n = mazeRacer.isSingleUser() ? 1 : playerList.length;
        for (i=0; i<n; i++) {
            player = playerList[i];
            player.paint(g, maze.getWidth(), CELL_SIZE, hoff, voff);
        }

        level = mazeRacer.getLevel();
        time = mazeRacer.getTime();
        wins = mazeRacer.getWins();
        sw = font.charsWidth(level, 0, level.length);

        yy = voff + 3 + maze.getHeight() * CELL_SIZE;

        font.setColor(0xff0000ff);
        font.drawChars(
            g,
            time,
            0,
            time.length,
            hoff,
            yy
        );

        font.drawChars(
            g,
            level,
            0,
            level.length,
            getWidth() - hoff - sw,
            yy
        );

        yy += font.getHeight();

        String names = LEVEL_WINS + mazeRacer.getOpponent() + LEVEL_WINS_2;
        lww = font.stringWidth(names);
        if (!mazeRacer.isSingleUser()) {
            font.drawString(g, names, hoff, yy);
            font.drawChars(g, wins, 0, wins.length, hoff + lww, yy);
        }

        paintRightSoftButton(g);
        paintWaiting(g);
    }

}