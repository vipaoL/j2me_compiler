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

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;


/**
 * This class handles and coordinates most of the drawing for this game. 
 * It knows how to draw the splash, menu and game over screen, as well as the
 * actual games screen. It also forwards 'cooked' key events to the Engine class.
 */
public class Screen extends Canvas {
    public static int width;
    public static int height;
    private static final int TL_ANCHOR = Graphics.TOP | Graphics.LEFT;
    private static final int TH_ANCHOR = Graphics.TOP | Graphics.HCENTER;
    public static Graphics GRAPHICS;
    public static final int BACKGROUND = ThreeDColor.gray.getRGB();
    private Font fontSmall;
    private Font fontMedium;
    private Font fontLarge;
    private Font font;
    private Image buf;
    private int lastState;
    private int lastScore;
    private int lastLives;
    private Engine engine;
    private EngineState state;
    private int fontHeight;
    private MIDlet midlet;

    /**
     * Creates Screen instance
     * 
     * @param midlet A MIDlet using this Screen
     */
    public Screen(MIDlet midlet) {
        width = getWidth();
        height = getHeight();

        fontSmall = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        fontMedium = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
        fontLarge = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_LARGE);

        if (height < 180) {
            font = fontSmall;
        } else if (height >= 180 && height < 280) {
            font = fontMedium;
        } else {
            font = fontLarge;
        }

        fontHeight = font.getHeight();

        buf = Image.createImage(width, height - (fontHeight + 5));
        GRAPHICS = buf.getGraphics();
        state = new EngineState();

        lastState = -1;
        lastScore = -1;
        lastLives = -1;

        this.midlet = midlet;
    }

    /**
     * Sets game business logic
     *
     * @param game engine
     */
    public void setEngine(Engine engine) {
        this.engine = engine;
        engine.getState(state);
        state.menu.setMIDlet(midlet);
    }

    /**
     * Called when a key is pressed
     *
     * @param keycode the key code of the key that was pressed
     */
    public void keyPressed(int keycode) {
        if (state.state == Engine.MENU) {
            engine.setLastKeyPressed(System.currentTimeMillis());
            state.menu.keyPressed(getGameAction(keycode));
        } else {
            engine.keyPressed(keycode, getGameAction(keycode));
        }
    }

    /**
     * Called when a key is released
     *
     * @param keycode the key code of the key that was released
     */
    public void keyReleased(int keycode) {
        if (state.state != Engine.MENU) {
            engine.keyReleased(keycode, getGameAction(keycode));
        }
    }

    /**
     * Paints background on the Canvas
     *
     * @param g the Graphics object to be used for rendering
     */
    private void paintBackground(Graphics g) {
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, width, height);
    }

    /**
     * Paints splash screen
     *
     * @param g the Graphics object to be used for rendering
     */
    private void paintTitle(Graphics g) {
        engine.getState(state);

        if (lastState != Engine.TITLE) {
            paintBackground(GRAPHICS);
            state.bricks.paintShadow(GRAPHICS);
            state.bricks.paint(GRAPHICS);
        }

        g.drawImage(buf, 0, 0, TL_ANCHOR);
        g.setColor(BACKGROUND);
        g.fillRect(0, height - (fontHeight + 5), width, fontHeight + 5);

        g.setFont(font);

        g.setColor(ThreeDColor.black.getRGB());
        g.drawString("Bricks", (width / 2) + 1, (height / 2) + 1, TH_ANCHOR);

        g.setColor(ThreeDColor.red.getRGB());
        g.drawString("Bricks", width / 2, height / 2, TH_ANCHOR);
    }

    /**
     * Paints game over info
     *
     * @param g the Graphics object to be used for rendering
     */
    private void paintOver(Graphics g) {
        paintBackground(g);

        g.setFont(font);

        g.setColor(ThreeDColor.black.getRGB());
        g.drawString("Game", (width / 2) + 1, (height / 2) - 10 + 1, TH_ANCHOR);
        g.drawString("Over", (width / 2) + 1, (height / 2) + 10 + 1, TH_ANCHOR);

        g.setColor(ThreeDColor.red.getRGB());
        g.drawString("Game", width / 2, (height / 2) - 10, TH_ANCHOR);
        g.drawString("Over", width / 2, (height / 2) + 10, TH_ANCHOR);
    }

    /**
     * Paints memu
     *
     * @param g the Graphics object to be used for rendering
     */
    private void paintMenu(Graphics g) {
        engine.getState(state);

        if (lastState != Engine.MENU) {
            paintBackground(GRAPHICS);
            state.bricks.paintShadow(GRAPHICS);
            state.bricks.paint(GRAPHICS);
        }

        g.drawImage(buf, 0, 0, TL_ANCHOR);
        g.setColor(BACKGROUND);
        g.fillRect(0, height - (fontHeight + 5), width, fontHeight + 5);

        g.setFont(font);

        g.setColor(ThreeDColor.black.getRGB());

        for (int i = 0; i < state.menu.getItems().length; i++) {
            g.drawString(state.menu.getItems()[i].getText(), (width / 2) + 1,
                (height / 2) - fontHeight / 2 + 1 + (fontHeight * i), TH_ANCHOR);
        }

        for (int i = 0; i < state.menu.getItems().length; i++) {
            if (state.menu.getItems()[i].isEnabled()) {
                g.setColor(ThreeDColor.purple.getRGB());
            } else {
                g.setColor(ThreeDColor.gray.getRGB());
            }

            g.drawString(state.menu.getItems()[i].getText(), width / 2,
                (height / 2) - fontHeight / 2 + (fontHeight * i), TH_ANCHOR);
        }

        g.setColor(ThreeDColor.pink.getRGB());

        g.drawString(state.menu.getItems()[state.menu.getSelected()].getText(), width / 2,
            (height / 2) - fontHeight / 2 + (fontHeight * state.menu.getSelected()), TH_ANCHOR);
    }
    
    /**
     * Paints payment transaction feedback (payment transaction status)
     *
     * @param g the Graphics object to be used for rendering
     */
    private void paintFeedback(Graphics g) {
        paintBackground(GRAPHICS);
        g.drawImage(buf, 0, 0, TL_ANCHOR);
        g.setColor(BACKGROUND);
        g.fillRect(0, height - (fontHeight + 5), width, fontHeight + 5);

        g.setFont(fontLarge);

        int textColor = ThreeDColor.green.getRGB();
        String[] info = new String[2];
        info[0] = "Thank you for buying";
        
        switch (((Main) midlet).getLastTransactionState()) {
            case Main.FEATURE_1_LIFE:
                info[1] = "additional life";
                break;
            case Main.FEATURE_3_LIVES:
                info[1] = "additional three lives";
                break;
            case Main.FEATURE_1_LEVEL:
                info[1] = "additional game level";
                break;
            case Main.FEATURE_3_LEVELS:
                info[1] = "additional three game levels";
                break;
            case Main.TRAN_REJECTED:
                info[0] = "Your payment transaction";
                info[1] = "was rejected!";
                textColor = ThreeDColor.orange.getRGB();
                break;
            case Main.TRAN_FAILED:
                info[0] = "Your payment transaction failed!";
                info[1] = "Please try again.";
                textColor = ThreeDColor.red.getRGB();
                break;
        }
        
        g.setColor(ThreeDColor.black.getRGB());        
        
        g.drawString(info[0], width / 2,
                (height / 3) + 31, TH_ANCHOR);
        g.drawString(info[1], width / 2,
                (height / 3) + 51, TH_ANCHOR);            
        
        g.setColor(textColor);
        
        g.drawString(info[0], width / 2,
                (height / 3) + 30, TH_ANCHOR);
        g.drawString(info[1], width / 2,
                (height / 3) + 50, TH_ANCHOR);
    }

    /**
     * Renders the Canvas
     *
     * @param g the Graphics object to be used for rendering the Canvas
     */
    public void paint(Graphics g) {
        boolean full_repaint = engine.levelStarted();

        engine.getState(state);
        g.setFont(font);

        if (state.state == Engine.TITLE) {
            paintTitle(g);
        } else if (state.state == Engine.MENU) {
            paintMenu(g);
        } else if (state.state == Engine.FEEDBACK) {
            paintFeedback(g);
        } else if (state.state == Engine.OVER) {
            paintOver(g);
            state.menu.resetSelected();
            state.menu.showResumeItem(false);
        } else {
            if (full_repaint) {
                paintBackground(GRAPHICS);

                state.bricks.paintShadow(GRAPHICS);
                state.bricks.paint(GRAPHICS);
            }

            g.drawImage(buf, 0, 0, TL_ANCHOR);

            if (state.state == Engine.PLAY) {
                if ((state.score != lastScore) || full_repaint) {
                    g.setColor(BACKGROUND);
                    g.fillRect(width / 3, height - (fontHeight + 5), (width * 2) / 3, fontHeight +
                        5);

                    g.setColor(ThreeDColor.black.getRGB());
                    g.drawString("S: " + state.score, ((width * 1) / 3) + 1,
                        height - (fontHeight + 3), TL_ANCHOR);
                    g.drawString("H: " + state.hiScore, ((width * 2) / 3) + 1,
                        height - (fontHeight + 3), TL_ANCHOR);
                    g.setColor(ThreeDColor.orange.getRGB());
                    g.drawString("S: " + state.score, (width * 1) / 3, height - (fontHeight + 5),
                        TL_ANCHOR);
                    g.drawString("H: " + state.hiScore, (width * 2) / 3, height - (fontHeight + 5),
                        TL_ANCHOR);
                }

                if ((state.lives != lastLives) || full_repaint) {
                    g.setColor(BACKGROUND);
                    g.fillRect(0, height - (fontHeight + 5), width / 3, fontHeight + 5);

                    for (int i = 0; i < state.lives; i++) {
                        g.setColor(ThreeDColor.black.getRGB());
                        g.fillArc(BrickList.XOFFSET + 1 + (3 * Ball.RADIUS * i),
                            height - (fontHeight - 1), Math.max(4, height / 50),
                            Math.max(4, height / 50), 0, 360);

                        g.setColor(ThreeDColor.red.getRGB());
                        g.fillArc(BrickList.XOFFSET + (3 * Ball.RADIUS * i), height - fontHeight,
                            Math.max(4, height / 50), Math.max(4, height / 50), 0, 360);
                    }
                }
            }

            state.ball.paintShadow(g);
            state.paddle.paintShadow(g);

            state.ball.paint(g);
            state.paddle.paint(g);
        }

        lastState = state.state;
        lastScore = state.score;
        lastLives = state.lives;
    }
}
