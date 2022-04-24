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

import java.util.Stack;

/**
 * This class represents a two-dimensional maze. A maze consists of walls and
 * paths. A maze has an entry and a exit. This class creates the setup of
 * walls and paths programmatically. It is guaranteed that each created maze
 * will have a solution, provided that both the width and the height of the
 * maze are odd.
 */
public class Maze {
    private int width;
    private int height;
    private Cell[] grid;
    private boolean changed;


    /**
     * Create a new instance of this class.
     * @param width The width of the maze in cells
     * @param height The height of the maze in cells
     * @see sample.commui.Cell
     */
    public Maze(int width, int height) {
        int i;

        this.width = width;
        this.height = height;

        grid = new Cell[width * height];

        for (i=0; i<grid.length; i++) {
            grid[i] = new Cell();
        }

        for (i=0; i<grid.length; i++) {
            grid[i].setNeighbors(grid, i, width, height);
        }

        reset();
    }

    /**
     * Create a new configuration of walls and paths for this instance.
     */
    public void reset() {
        int[] dirList;
        Stack stack;
        Cell cell;
        int dir;

        //System.out.println("Maze.reset()");

        stack = new Stack();
        cell = grid[1 + width];
        stack.push(cell);

        for (int i=0; i<grid.length; i++) {
            grid[i].setWall(true);
        }

        dirList = new int[4];

        while (!stack.empty()) {
            cell = (Cell)stack.peek();
            cell.setWall(false);

            dir = cell.getRandomUnclaimedNeighbor(dirList);

            if (dir < 0) stack.pop();
            else {
                cell = cell.getNeighbor(dir);
                cell.setWall(false);
                cell = cell.getNeighbor(dir);
                stack.push(cell);
            }
        }

        // Carve out maze entry
        cell = grid[width];
        cell.setWall(false);

        // Carve out maze exit
        cell = grid[grid.length - width - 1];
        cell.setWall(false);

        changed = true;
    }

    /**
     * Check if this instance has changed since the last call to this method.
     * @return <code>true</code> if it has, <code>false</code> otherwise.
     */
    public boolean isChanged() {
        boolean c = changed;
        changed = false;
        return c;
    }

    /**
     * Get the size of this instance in cells.
     * @return the size of this instance in cells.
     */
    public int getSize() {
        return grid.length;
    }

    /**
     * Get the width of this instance in cells.
     * @return the width of this instance in cells.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the height of this instance in cells.
     * @return the height of this instance in cells.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Check if a certain cell in this instance is a wall or not.
     * @return <code>true</code> if it has, <code>false</code> otherwise.
     */
    public boolean isWall(int index) {
        return grid[index].isWall();
    }

    /**
     * Get the tile code for a certain cell in this instance.
     * @return the tile code
     */
    public int getTileCode(int index) {
        return grid[index].getTileCode();
    }
}