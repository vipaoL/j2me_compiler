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
 * This class implements the ball, with its radius,
 * and x and y step size. The ball knows how to draw
 * itself, and every once in a while it will do a
 * subtle, pseudo-random path modification to prevent
 * closed ball paths.
 */
public class Ball extends Sprite {
    public static final int RADIUS = Math.max(2, Screen.width / 55);
    private int dx;
    private int dy;
    private int xo;
    private int yo;
    private ThreeDColor color;
    private ThreeDColor brighter;
    private ThreeDColor darker;
    private int counter;
    private int offset;

    public Ball(int x, int y, int dx, int dy) {
        moveTo(x, y);
        setSteps(dx, dy);

        offset = 0;

        width = 2 * RADIUS;
        height = 2 * RADIUS;

        xo = yo = 0;

        color = ThreeDColor.red;
        brighter = color.brighter();
        darker = color.darker();
    }

    public void setSteps(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int getXStep() {
        return dx;
    }

    public int getYStep() {
        return dy;
    }

    public void move() {
        x = x + dx + xo;
        y = y + dy + yo;
        xo = yo = 0;
    }

    public void bounceHorizontal() {
        dx = -dx;

        if (Util.getRandomInt(0, 1000) < 70) {
            xo = (dx < 0) ? (-1) : 1;
        }
    }

    public void bounceVertical() {
        dy = -dy;

        if (Util.getRandomInt(0, 1000) < 70) {
            yo = (dy < 0) ? (-1) : 1;
        }
    }

    public void bounce(Sprite other) {
        int cx;
        int cy;

        cx = getCenterX();
        cy = getCenterY();

        if (dx < 0) {
            if ((cy >= other.y) && (cy < (other.y + other.height)) &&
                    (x < (other.x + other.width))) {
                dx = Math.abs(dx);
            }
        } else {
            if ((cy >= other.y) && (cy < (other.y + other.height)) && ((x + width) >= other.x)) {
                dx = -Math.abs(dx);
            }
        }

        if (dy < 0) {
            if ((cx >= other.x) && (cx < (other.x + other.width)) &&
                    (y < (other.y + other.height))) {
                dy = Math.abs(dy);
            }
        } else {
            if ((cx >= other.x) && (cx < (other.x + other.width)) && ((y + height) >= other.y)) {
                dy = -Math.abs(dy);
            }
        }

        if (Util.getRandomInt(0, 1000) < 70) {
            xo = (dx < 0) ? (-1) : 1;
        }

        if (Util.getRandomInt(0, 1000) < 70) {
            yo = (dy < 0) ? (-1) : 1;
        }
    }

    public void paintShadow(Graphics g) {
        g.setColor(ThreeDColor.black.getRGB());
        g.fillArc(x + shadow, y + shadow, width, height, 0, 360);
    }

    public void paint(Graphics g) {
        if (++counter == 6) {
            counter = 0;
            offset = 1 - offset;
        }

        g.setColor(color.getRGB());
        g.fillArc(x, y, width, height, 0, 360);

        g.setColor(brighter.getRGB());
        g.drawArc(x + 2, y + 2, width - 4, height - 4, 180 + offset, -90);

        g.setColor(darker.getRGB());
        g.drawArc(x, y, width, height, 0, -90);
    }
}
