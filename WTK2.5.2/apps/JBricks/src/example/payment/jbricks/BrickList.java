/*
 *
 * Copyright (c) 2007, Sun Microsystems, Inc.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of Sun Microsystems nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package example.payment.jbricks;

import javax.microedition.lcdui.Graphics;


/**
 * This is a container class that some of the
 * initialization, drawing and collision detection
 * for the bricks in any particular level. It also
 * serves as a container that manages redraws
 */
public class BrickList {
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int WEST = 2;
    public static final int EAST = 3;
    public static final int XOFFSET;
    public static final int YOFFSET;

    static {
        int w = Engine.PATTERN_WIDTH * Brick.WIDTH;
        int g = (Engine.PATTERN_WIDTH - 1) * Brick.GAP;

        XOFFSET = ((Screen.width - (w + g)) / 2) - 1;
        YOFFSET = Screen.height / 8;
    }

    private Brick[] list;
    private int columns;
    private int[] recent_collision;
    Sprite inter;
    private int[] step = { 0, -1, 0, 1, -1, 0, 1, 0 };
    private int[] all_step = { 0, 0, 0, -1, 1, 0, 0, 1, 0, 1, -1, 0, -1, 0, 0, -1, 0, -1 };
    private ThreeDColor[] rainbow =
        {
            ThreeDColor.purple, ThreeDColor.purple, ThreeDColor.blue, ThreeDColor.blue,
            ThreeDColor.red, ThreeDColor.red, ThreeDColor.orange, ThreeDColor.orange,
        };

    public BrickList(int[] typeList, int patternWidth, int level) {
        int x = XOFFSET;
        int y = YOFFSET - (Brick.HEIGHT + Brick.GAP);
        int n = -1;

        list = new Brick[typeList.length];
        recent_collision = new int[list.length];
        inter = new Sprite();

        for (int i = 0; i < list.length; i++) {
            if ((i % patternWidth) == 0) {
                columns = (x - XOFFSET) / (Brick.WIDTH + Brick.GAP);
                x = XOFFSET;
                y += (Brick.HEIGHT + Brick.GAP);
                n++;
            }

            list[i] = new Brick(this, x, y, i, typeList[i]);

            if ((level == 0) && (list[i].getType() == Brick.STANDARD)) {
                list[i].setColor(rainbow[n]);
            }

            x += (Brick.WIDTH + Brick.GAP);
        }
    }

    public Brick getBrickAt(int n) {
        if ((n < 0) || (n >= list.length)) {
            return null;
        }

        return list[n];
    }

    public void moveBrick(int from, int to) {
        int to_x = list[to].x;
        int to_y = list[to].y;
        list[to] = new Brick(list[from]);
        list[to].moveTo(to_x, to_y);
        list[to].setPos(to);
        list[from].erase(Screen.GRAPHICS);
        list[from].clear();
        list[to].paintShadow(Screen.GRAPHICS);
        list[to].paint(Screen.GRAPHICS);
    }

    public int checkForCollision(Ball ball) {
        Brick brick;
        int xw;
        int yh;
        int oxw;
        int oyh;
        int n;
        int x;
        int y;
        int score;
        int dir;
        int width = Engine.PATTERN_WIDTH;
        int height = list.length / Engine.PATTERN_WIDTH;
        boolean intersects;

        score = dir = 0;

        x = (ball.getCenterX() - XOFFSET) / (Brick.WIDTH + Brick.GAP);

        if ((x < 0) || (x >= width)) {
            return 0;
        }

        y = (ball.getCenterY() - YOFFSET) / (Brick.HEIGHT + Brick.GAP);

        if ((y < 0) || (y >= height)) {
            return 0;
        }

        for (int i = 0; i < (all_step.length / 2); i++) {
            x += all_step[(2 * i) + 0];
            y += all_step[(2 * i) + 1];

            if ((x < 0) || (x >= width) || (y < 0) || (y >= height)) {
                continue;
            }

            n = (y * Engine.PATTERN_WIDTH) + x;
            brick = list[n];

            xw = ball.x + ball.width;
            yh = ball.y + ball.height;
            oxw = brick.x + brick.width;
            oyh = brick.y + brick.height;

            intersects = ((ball.x >= brick.x) && (ball.x < oxw) && (ball.y >= brick.y) &&
                (ball.y < oyh)) ||
                ((xw >= brick.x) && (xw < oxw) && (ball.y >= brick.y) && (ball.y < oyh)) ||
                ((ball.x >= brick.x) && (ball.x < oxw) && (yh >= brick.y) && (yh < oyh)) ||
                ((xw >= brick.x) && (xw < oxw) && (yh >= brick.y) && (yh < oyh));

            if (intersects) {
                if (recent_collision[n] == 0) {
                    ball.bounce(brick);
                }

                if (ball.getCenterX() < brick.x) {
                    dir = EAST;
                }

                if (ball.getCenterX() > (brick.x + Brick.WIDTH)) {
                    dir = WEST;
                }

                if (ball.getCenterY() < brick.y) {
                    dir = SOUTH;
                }

                if (ball.getCenterY() > (brick.y + Brick.HEIGHT)) {
                    dir = NORTH;
                }

                if (brick.getType() != Brick.SLIDE) {
                    brick.erase(Screen.GRAPHICS);
                }

                score += brick.hit(dir);

                recent_collision[n] = 2;

                // don't do more than one collision per frame
                break;
            } else if (recent_collision[n] > 0) {
                recent_collision[n]--;
            }
        }

        return score;
    }

    public boolean isClean() {
        for (int i = 0; i < list.length; i++) {
            if (list[i].getType() == Brick.STANDARD) {
                return false;
            }
        }

        return true;
    }

    public Brick getNeighbor(Brick brick, int direction) {
        int i;

        if (brick == null) {
            return null;
        }

        for (i = 0; i < list.length; i++) {
            if (list[i] == brick) {
                break;
            }
        }

        if (i == list.length) {
            return null;
        }

        int x = i % columns;
        int y = i / columns;
        int dx = step[2 * direction];
        int dy = step[(2 * direction) + 1];

        x += dx;
        y += dy;

        if ((x < 0) || (x >= columns) || (y < 0) || (y >= (list.length / columns))) {
            return null;
        }

        return list[(y * columns) + x];
    }

    public void paintShadow(Graphics g) {
        for (int i = 0; i < list.length; i++) {
            list[i].paintShadow(g);
        }
    }

    public void paint(Graphics g) {
        for (int i = 0; i < list.length; i++) {
            list[i].paint(g);
        }
    }
}
