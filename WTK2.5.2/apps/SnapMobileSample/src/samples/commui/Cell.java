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

/**
 * This class represents the smallest defined element in a maze.
 * Cells are square, and they represent either a piece of a path,
 * or a piece of a wall. Each cell has eight neighbors, two
 * horizontal, two vertical, and four diagonal. Neigbors for
 * edge or corner cells may be <code>null</code>.
 */
public class Cell {
    private boolean wall;
    private Cell[] neighborList;
    private int neighborCount;

    /** Northwest direction */
    public static final int NW =  0;

    /** North direction */
    public static final int N  =  1;

    /** Northeast direction */
    public static final int NE =  2;

    /** East direction */
    public static final int E  =  3;

    /** Southeast direction */
    public static final int SE =  4;

    /** South direction */
    public static final int S  =  5;

    /** Southwest direction */
    public static final int SW =  6;

    /** West direction */
    public static final int W  =  7;

    private static final int[] STEP_LIST = {
        -1, -1, 0, -1, 1, -1, 1, 0, 1, 1, 0, 1, -1, 1, -1, 0
    };


    /**
     * Create a new instance of this class.
     * New cells are by default a piece of a wall.
     */
    public Cell() {
        wall = true;

        neighborCount = 0;
        neighborList = new Cell[8];
    }

    /*
     * Check if this instance is a piece of a wall.
     * @return <code>true</code> if it is, <code>false</code> otherwise.
     */
    public boolean isWall() {
        return wall;
    }

    /*
     * Configure this instance as a piece of a wall,
     * or a piece of a path.
     * @param wall <code>true</code> for wall, <code>false</code> otherwise.
     */
    public void setWall(boolean wall) {
        this.wall = wall;
    }

    /*
     * Check if <code>cell</code> is claimed.
     * @param cell The cell to check.
     * @return <code>true</code> if it is, <code>false</code> otherwise.
     */
    private static boolean isClaimed(Cell cell) {
        return cell != null && cell.isClaimed();
    }


    /*
     * Check if this instance is claimed.
     * @return <code>true</code> if it is, <code>false</code> otherwise.
     */
    public boolean isClaimed() {
        int pathCount = 0;

        if (!wall || neighborCount != 8) return true;

        if (!neighborList[N].isWall()) pathCount++;
        if (!neighborList[E].isWall()) pathCount++;
        if (!neighborList[S].isWall()) pathCount++;
        if (!neighborList[W].isWall()) pathCount++;

        if (pathCount > 1) return true;

        return false;

    }


    /*
     * Get the neighbor count of this instance.
     * @return the neighbor count.
     */
    public int getNeighborCount() {
        return neighborCount;
    }


    /*
     * Get a specific neighbor of this instance. May return
     * <code>null</code> for invalid direction values, or if
     * no such neighbor exists.
     * @return the neighbor.
     */
    public Cell getNeighbor(int dir) {
        if (dir < NW || dir > W) return null;
        return neighborList[dir];
    }

    private boolean isValidIndex(int x, int y, int width, int height) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /*
     * Set neighbors for this instance.
     * @param grid The grid of all cells in the maze.
     * @param index The grid index of this instance.
     * @param width The width of the grid in cells.
     * @param height The height of the grid in cells.
     */
    public void setNeighbors(Cell[] grid, int index, int width, int height) {
        int i, x, y, xx, yy;

        x = index % width;
        y = index / width;

        for (i=0; i<STEP_LIST.length; i+=2) {
            xx = x + STEP_LIST[i + 0];
            yy = y + STEP_LIST[i + 1];

            if (isValidIndex(xx, yy, width, height)) {
                neighborList[i / 2] = grid[yy * width + xx];
                neighborCount++;
            }
        }
    }


    /*
     * Get a random, unclaimed neighbor for this instance.
     * @param unclaimedList An integer array that this method will write into.
     * @return the direction, relative to this instance, of a
     * random, unclaimed neighbor.
     */
    public int getRandomUnclaimedNeighbor(int[] unclaimedList) {
        int i = 0, n;

        if (!Cell.isClaimed(neighborList[N])
            && !Cell.isClaimed(neighborList[N].neighborList[N])) {
            unclaimedList[i++] = N;
        }


        if (!Cell.isClaimed(neighborList[E])
            && !Cell.isClaimed(neighborList[E].neighborList[E])) {
            unclaimedList[i++] = E;
        }


        if (!Cell.isClaimed(neighborList[S])
            && !Cell.isClaimed(neighborList[S].neighborList[S])) {
            unclaimedList[i++] = S;
        }


        if (!Cell.isClaimed(neighborList[W])
            && !Cell.isClaimed(neighborList[W].neighborList[W])) {
            unclaimedList[i++] = W;
        }

        if (i == 0) return -1;

        n = Util.getRandom(0, i);
        //System.out.println("Cell.getRandomUnclaimedNeighbor(), n: " + n);

        return unclaimedList[n];
    }


    /*
     * Get the correct tile index for this instance. Each cell is represented
     * graphically when the maze is drawn. The tile that should be used for
     * drawing depends on the state of the neighboring cells.
     * @return tile index
     */
    public int getTileCode() {
        int code = 0;

        if (neighborList[N] == null || !neighborList[N].isWall()) code |= 1;
        if (neighborList[E] == null || !neighborList[E].isWall()) code |= 2;
        if (neighborList[S] == null || !neighborList[S].isWall()) code |= 4;
        if (neighborList[W] == null || !neighborList[W].isWall()) code |= 8;

        return code;
    }
}