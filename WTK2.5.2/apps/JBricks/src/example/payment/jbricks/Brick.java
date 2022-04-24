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
 * This class represents an individual brick, with its
 * position, dimension, type and color. Bricks know
 * how to draw themselves, and how to handle collisions.
 */
public class Brick extends Sprite {
    public static final int WIDTH = Screen.width / 15;
    public static final int HEIGHT = Screen.height / 30;
    public static final int STEP = Screen.width / 25;
    public static final int GAP = Math.max(2, Screen.width / 55);
    public static final int ZOMBIE = 0;
    public static final int STANDARD = 1;
    public static final int FIXED = 2;
    public static final int SLIDE = 3;
    private int pos;
    private int type;
    private int score;
    private ThreeDColor color;
    private ThreeDColor brighter;
    private ThreeDColor darker;
    private ThreeDColor[] colorList =
        { ThreeDColor.black, ThreeDColor.blue, ThreeDColor.green, ThreeDColor.darkGreen };
    private BrickList owner;

    public Brick(BrickList owner, int x, int y, int pos, int type) {
        this.pos = pos;
        this.owner = owner;

        moveTo(x, y);

        if (type == ZOMBIE) {
            width = 0;
            height = 0;
        } else {
            width = WIDTH;
            height = HEIGHT;
        }

        this.type = type;
        color = colorList[type];
        brighter = color.brighter();
        darker = color.darker();

        if (type == STANDARD) {
            score = 1;
        }
    }

    public Brick(Brick brick) {
        pos = brick.pos;
        owner = brick.owner;
        moveTo(brick.x, brick.y);
        type = brick.type;
        width = brick.width;
        height = brick.height;
        color = brick.color;
        brighter = brick.brighter;
        darker = brick.darker;
        score = brick.score;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public void setColor(ThreeDColor color) {
        this.color = color;
        brighter = color.brighter();
        darker = color.darker();
    }

    public void clear() {
        type = ZOMBIE;
        width = 0;
        height = 0;
    }

    public int hit(int direction) {
        if (type == STANDARD) {
            clear();
        }

        if (type == SLIDE) {
            Brick neighbor = owner.getNeighbor(this, direction);

            if ((neighbor != null) && (neighbor.getType() == 0)) {
                owner.moveBrick(pos, neighbor.pos);
            }
        }

        return score;
    }

    public int getType() {
        return type;
    }

    public boolean isFixed() {
        return (type == ZOMBIE) || (type == FIXED);
    }

    public void paint(Graphics g) {
        if (type == ZOMBIE) {
            return;
        }

        g.setColor(color.getRGB());
        g.fillRect(x, y, width, height);

        g.setColor(brighter.getRGB());
        g.drawLine(x, y, x + width, y);

        if (Screen.width >= 150) {
            g.drawLine(x, y + 1, (x + width) - 1, y + 1);
        }

        g.drawLine(x, y, x, y + height);

        if (Screen.height >= 150) {
            g.drawLine(x + 1, y, x + 1, (y + height) - 1);
        }

        g.setColor(darker.getRGB());
        g.drawLine(x + width, y, x + width, y + height);

        if (Screen.height >= 150) {
            g.drawLine((x + width) - 1, y + 1, (x + width) - 1, y + height);
        }

        g.drawLine(x, y + height, x + width, y + height);

        if (Screen.width >= 150) {
            g.drawLine(x + 1, (y + height) - 1, x + width, (y + height) - 1);
        }
    }

    public void paintShadow(Graphics g) {
        if (type == ZOMBIE) {
            return;
        }

        g.setColor(ThreeDColor.black.getRGB());
        g.fillRect(x + shadow, y + shadow, width, height);
    }

    public void erase(Graphics g) {
        if (isFixed()) {
            return;
        }

        g.setColor(Screen.BACKGROUND);
        g.fillRect(x, y, width + shadow, height + shadow);
    }
}
