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


/**
 * This class provides support for pseudo-3D coloring.
 * Each of the defined colors can used as a base color,
 * and the brighter() and darker() methods can be used
 * to retrieve the appropriately highlighted or shadowed
 * version of that color. The getRGB() method is used to
 * retrieve the actual RGB value of the color, the returned
 * value can be passed to Graphics.setColor().
 */
public class ThreeDColor {
    public static ThreeDColor lightGray = new ThreeDColor(0xc0c0c0);
    public static ThreeDColor lightPurple = new ThreeDColor(0xb000ff);
    public static ThreeDColor black = new ThreeDColor(0x000000);
    public static ThreeDColor gray = new ThreeDColor(0x808080);
    public static ThreeDColor blue = new ThreeDColor(0x0000ff);
    public static ThreeDColor red = new ThreeDColor(0xff0000);
    public static ThreeDColor yellow = new ThreeDColor(0xffff00);
    public static ThreeDColor white = new ThreeDColor(0xffffff);
    public static ThreeDColor orange = new ThreeDColor(0xffc800);
    public static ThreeDColor green = new ThreeDColor(0x00ff00);
    public static ThreeDColor purple = new ThreeDColor(0x8000c0);
    public static ThreeDColor darkCyan = new ThreeDColor(0x00b2b2);
    public static ThreeDColor pink = new ThreeDColor(0xffafaf);
    public static ThreeDColor darkGray = new ThreeDColor(0x404040);
    public static ThreeDColor darkGreen = new ThreeDColor(0x00b200);
    public static ThreeDColor darkOrange = new ThreeDColor(0xb28c00);
    public static ThreeDColor darkPurple = new ThreeDColor(0x500080);
    public static ThreeDColor darkRed = new ThreeDColor(0xb20000);
    private int rgb;

    private ThreeDColor(int rgb) {
        this.rgb = rgb;
    }

    public ThreeDColor brighter() {
        if (this == lightGray) {
            return white;
        }

        if (this == gray) {
            return lightGray;
        }

        if (this == darkGreen) {
            return green;
        }

        if (this == blue) {
            return darkCyan;
        }

        if (this == red) {
            return pink;
        }

        if (this == yellow) {
            return white;
        }

        if (this == orange) {
            return yellow;
        }

        if (this == green) {
            return yellow;
        }

        if (this == purple) {
            return lightPurple;
        }

        return white;
    }

    public ThreeDColor darker() {
        if (this == lightGray) {
            return gray;
        }

        if (this == gray) {
            return darkGray;
        }

        if (this == darkGreen) {
            return darkGray;
        }

        if (this == blue) {
            return darkGray;
        }

        if (this == red) {
            return darkRed;
        }

        if (this == yellow) {
            return orange;
        }

        if (this == orange) {
            return darkOrange;
        }

        if (this == green) {
            return darkGreen;
        }

        if (this == purple) {
            return darkPurple;
        }

        return darkGray;
    }

    public int getRGB() {
        return rgb;
    }
}
