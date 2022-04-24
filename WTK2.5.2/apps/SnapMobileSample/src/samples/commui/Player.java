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

import javax.microedition.lcdui.*;
import samples.ui.*;


/**
 * This class represents a player in the sample game. A player has a position
 * within a maze, and a graphical representation.
 */
public class Player {
    private int position;
    private int frameIndex;
    private Image[] imgList;
    private Maze maze;


    /*
     * Create a new instance of this class.
     * @param maze The maze this instance is contained in
     * @param img A tile map containing the graphical representation of this instance
     * @param tileWidth The width of each individual tile
     */
    public Player(Maze maze, Image img, int tileWidth) {
        this.maze = maze;

        imgList = new Image[img.getWidth() / tileWidth];

        for (int i=0; i<imgList.length; i++) {
            imgList[i] = Image.createImage(img, tileWidth * i, 0, tileWidth, img.getHeight(), 0);
        }

        frameIndex = 0;
    }

    /**
     * Set the position of this instance.
     * @param The new position of this instance
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Get the position of this instance.
     * @return The position of this instance
     */
    public int getPosition() {
        return position;
    }

    /**
     * Check whether this instance can move in the given direction.
     * @return <code>true</code> if the instance can move, <code>false</code> otherwise.
     */
    public boolean canMove(int dir) {
        int x, y, w, h;

        w = maze.getWidth();
        h = maze.getHeight();

        x = position % w;
        y = position / w;

        if (dir == Canvas.RIGHT && x + 1 < w && !maze.isWall(y * w + x + 1)) {
            return true;
        }

        if (dir == Canvas.LEFT && x - 1 >= 0 && !maze.isWall(y * w + x - 1)) {
            return true;
        }

        if (dir == Canvas.DOWN && y + 1 < h && !maze.isWall((y + 1) * w + x)) {
            return true;
        }

        if (dir == Canvas.UP && y - 1 >= 0 && !maze.isWall((y - 1) * w + x)) {
            return true;
        }

        return false;
    }


    /**
     * Move the instance one step in the given direction. This method has no effect if
     * the instance cannot move in the requested direction.
     * @param direction The direction to move to.
     */
    public void move(int dir) {
        int x, y, w;

        if (!canMove(dir)) return;

        w = maze.getWidth();

        x = position % w;
        y = position / w;

        if (dir == Canvas.RIGHT) x++;
        if (dir == Canvas.LEFT ) x--;
        if (dir == Canvas.DOWN ) y++;
        if (dir == Canvas.UP   ) y--;

        position = y * w + x;
    }


    /**
     * Paint this instance.
     * @param g The graphics context
     * @param gridWidth The width of the grid
     * @param cellSize The size of each individual cell
     * @param hoff The horizontal offset
     * @param voff The vertical offset
     */
    public void paint(Graphics g, int gridWidth, int cellSize, int hoff, int voff) {
        Image img = imgList[frameIndex++];

        g.drawImage(
            img,
            position % gridWidth * cellSize + hoff - img.getWidth() / 2 + 2,
            position / gridWidth * cellSize + voff - img.getHeight() / 2 + 2,
            Component.NW_ANCHOR
        );

        if (frameIndex >= imgList.length) frameIndex = 0;
    }
}