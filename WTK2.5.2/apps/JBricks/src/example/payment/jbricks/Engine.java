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


/**
 * This class drives the games (hence the name engine ). It has all the level 
 * data, animates the objects, keeps track of state, and triggers
 * screen redraws.
 */
public class Engine implements Runnable {
    public static final int PATTERN_WIDTH = 11;
    
    // engine states
    public static final int TITLE = 0;
    public static final int PLAY = 1;
    public static final int OVER = 2;
    public static final int MENU = 3;
    public static final int FEEDBACK = 4;
    
    private static final int LAST_KEY_DELTA = 7000;
    
    // brick types
    private static final int STD = Brick.STANDARD;
    private static final int FIX = Brick.FIXED;
    private static final int SLI = Brick.SLIDE;
    private static final int ZOM = Brick.ZOMBIE;
    
    private static final int MAX_LEVELS = 6;
    private static final int MAX_LIVES = 6;
    
    private Ball ball;
    private Brick paddle;
    private BrickList bricks;
    private BrickList oldBricks;
    private Screen screen;
    private int score;
    private int hiScore;
    private int level;
    private int availableLevels;
    private int lives;
    private int availableLives;
    private int state;
    private int key;
    private boolean paused;
    private long lastKeyPress;
    private boolean levelStarted;
    private boolean done;
    private Menu menu;

    // Note that the width of each pattern has to be equal to the PATTERN_WIDTH
    // variable above
    private int[] title_pattern =
        {
            STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, FIX, FIX, FIX,
            STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, FIX, STD, STD, STD, STD, STD, STD, STD,
            STD, STD, STD, FIX, STD, STD, STD, STD, STD, STD, STD, STD, FIX, STD, FIX, STD, STD, STD,
            STD, STD, STD, STD, STD, STD, FIX, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD,
            STD, STD, STD, STD, STD
        };
    private int[][] pattern_list =
        {
            {
                STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD,
                STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD,
                STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD,
                STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD,
                STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD,
                STD, STD, STD,
            },
            {
                STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, ZOM, SLI, ZOM, ZOM, SLI, ZOM,
                ZOM, SLI, ZOM, ZOM, SLI, ZOM, ZOM, ZOM, ZOM, ZOM, ZOM, ZOM, ZOM, ZOM, ZOM, ZOM, SLI,
                ZOM, ZOM, SLI, ZOM, ZOM, SLI, ZOM, ZOM, SLI, ZOM, ZOM, ZOM, ZOM, ZOM, ZOM, ZOM, ZOM,
                ZOM, ZOM, ZOM, ZOM, ZOM, SLI, ZOM, ZOM, SLI, ZOM, ZOM, SLI, ZOM, ZOM, SLI, ZOM, ZOM,
                ZOM, ZOM, ZOM, ZOM, ZOM, ZOM, ZOM, ZOM, ZOM, SLI, ZOM, ZOM, SLI, ZOM, ZOM, SLI, ZOM,
                ZOM, SLI, ZOM,
            },
            {
                ZOM, ZOM, ZOM, ZOM, ZOM, ZOM, ZOM, ZOM, ZOM, ZOM, ZOM, ZOM, FIX, SLI, FIX, FIX, FIX,
                FIX, FIX, SLI, FIX, ZOM, ZOM, FIX, ZOM, ZOM, ZOM, ZOM, ZOM, ZOM, ZOM, FIX, ZOM, ZOM,
                FIX, ZOM, STD, STD, STD, STD, STD, ZOM, FIX, ZOM, ZOM, FIX, ZOM, ZOM, ZOM, ZOM, ZOM,
                ZOM, ZOM, FIX, ZOM, ZOM, FIX, FIX, FIX, FIX, FIX, FIX, FIX, FIX, FIX, ZOM, ZOM, ZOM,
                ZOM, ZOM, ZOM, ZOM, ZOM, ZOM, ZOM, ZOM, ZOM, STD, STD, STD, STD, STD, STD, STD, STD,
                STD, STD, STD,
            },
            {
                STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, FIX, FIX, FIX, FIX, FIX,
                FIX, FIX, FIX, FIX, STD, STD, FIX, STD, STD, STD, STD, STD, STD, STD, FIX, STD, STD,
                STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD,
                STD, STD, STD, STD, STD, FIX, STD, STD, STD, STD, STD, STD, STD, FIX, STD, STD, FIX,
                FIX, FIX, FIX, FIX, FIX, FIX, FIX, FIX, STD, STD, STD, STD, STD, STD, STD, STD, STD,
                STD, STD, STD
            },
            {
                FIX, FIX, FIX, FIX, FIX, ZOM, FIX, FIX, FIX, FIX, FIX, FIX, STD, STD, STD, STD, ZOM,
                STD, STD, STD, STD, FIX, FIX, STD, STD, SLI, SLI, ZOM, SLI, SLI, STD, STD, FIX, STD,
                STD, STD, STD, STD, ZOM, STD, STD, STD, STD, STD, STD, STD, STD, STD, STD, ZOM, STD,
                STD, STD, STD, STD, FIX, STD, STD, STD, STD, SLI, STD, STD, STD, STD, FIX, FIX, STD,
                STD, STD, STD, ZOM, STD, STD, STD, STD, FIX, FIX, FIX, FIX, FIX, FIX, ZOM, FIX, FIX,
                FIX, FIX, FIX
            },
            {
                SLI, SLI, SLI, SLI, SLI, SLI, SLI, SLI, SLI, SLI, SLI, SLI, ZOM, ZOM, ZOM, ZOM, ZOM,
                ZOM, ZOM, ZOM, ZOM, FIX, SLI, ZOM, ZOM, SLI, ZOM, ZOM, ZOM, SLI, ZOM, ZOM, FIX, SLI,
                ZOM, ZOM, ZOM, STD, STD, STD, ZOM, ZOM, ZOM, FIX, SLI, ZOM, ZOM, ZOM, STD, STD, STD,
                ZOM, ZOM, ZOM, FIX, SLI, ZOM, ZOM, SLI, ZOM, ZOM, ZOM, SLI, ZOM, ZOM, FIX, SLI, ZOM,
                ZOM, ZOM, ZOM, ZOM, ZOM, ZOM, ZOM, ZOM, FIX, FIX, FIX, FIX, FIX, FIX, FIX, FIX, FIX,
                FIX, FIX, FIX
            }
        };

    /**
     * Creates Engine object
     *
     * @param screen the screen used for rendering
     */
    public Engine(Screen screen) {
        int i;

        menu = new Menu();
        menu.setEngine(this);

        this.screen = screen;
        screen.setEngine(this);

        paddle = new Brick(null, (Screen.width / 2) - (Brick.WIDTH / 2), (Screen.height * 8) / 10,
                -1, 2);
        paddle.setColor(ThreeDColor.lightGray);
        paddle.width = Screen.width / 8;
        paddle.height = Brick.HEIGHT;

        ball = new Ball((Screen.width / 2) - (Brick.WIDTH / 4),
                ((Screen.height * 8) / 10) - (2 * Ball.RADIUS), 2 * Math.max(1, Screen.width / 80),
                2 * -Math.max(1, Screen.width / 80));

        availableLevels = 1;
        availableLives = 1;

        level = 0;
        state = TITLE;
        done = false;
        bricks = new BrickList(title_pattern, PATTERN_WIDTH, -1);
        lastKeyPress = System.currentTimeMillis();

        Thread runner = new Thread(this);
        runner.start();
    }

    /**
     * Resets the game
     */
    private void reset() {
        lives = availableLives;
        score = 0;
        startLevel();
    }

    /**
     * Restarts a level
     */
    private void restartLevel() {
        if (state == PLAY) {
            paused = true;
        }

        synchronized (this) {
            levelStarted = true;
        }

        paddle.moveTo((Screen.width / 2) - (Brick.WIDTH / 2), (Screen.height * 8) / 10);
        ball.moveTo((Screen.width / 2) - (Brick.WIDTH / 4),
            ((Screen.height * 8) / 10) - (2 * Ball.RADIUS));
        ball.setSteps(Math.max(1, Screen.width / 80), -Math.max(1, Screen.width / 80));

        screen.repaint();
        screen.serviceRepaints();
    }

    /**
     * Starts a new level
     */
    private void startLevel() {
        bricks = new BrickList(pattern_list[level], PATTERN_WIDTH, level);
        restartLevel();
    }

    /**
     * Starts next level
     */
    private void nextLevel() {
        level++;

        if ((level == availableLevels) || (level == pattern_list.length)) {
            level = 0;
        }

        startLevel();
    }

    /**
     * Called when a key is pressed
     *
     * @param keycode key code of the key that was pressed
     * @param gameAction game action associated with the given key code of the device
     */
    public void keyPressed(int keycode, int gameAction) {
        key = gameAction;
        lastKeyPress = System.currentTimeMillis();

        if ((state == OVER) || (state == TITLE)) {
            state = MENU;
            bricks = new BrickList(title_pattern, PATTERN_WIDTH, -1);
        } else if ((state == PLAY) &&
                ((key == Canvas.LEFT) || (key == Canvas.RIGHT) || (key == Canvas.FIRE))) {
            paused = false;
        } else if (state == FEEDBACK) {
            state = MENU;
        }
    }

    /**
     * Called when a key is released
     *
     * @param keycode key code of the key that was pressed
     * @param gameAction game action associated with the given key code of the device
     */    
    public void keyReleased(int keycode, int gameAction) {
        key = 0;
    }

    /**
     * Retrives state of the game
     *
     * @param engineState holder of the state
     */
    public void getState(EngineState engineState) {
        engineState.bricks = bricks;
        engineState.ball = ball;
        engineState.paddle = paddle;
        engineState.state = state;
        engineState.score = score;
        engineState.hiScore = hiScore;
        engineState.lives = lives;
        engineState.menu = menu;
    }

    /**
     * Starts the game
     *
     * @param level a level to be started
     */
    protected void startGame(int level) {
        this.level = level;
        state = PLAY;
        paused = false;
        reset();
    }

    /**
     * Sets time of a last pressed key
     *
     * @param time time when the key was pressed
     */
    public void setLastKeyPressed(long time) {
        lastKeyPress = time;
    }

    /**
     * Returns a flag indicating whether the game was started or not
     *
     * @return true if the game was started, false otherwise
     */
    public synchronized boolean levelStarted() {
        boolean x = levelStarted;
        levelStarted = false;

        return x;
    }

    /**
     * Stops the game
     */
    public void stop() {
        done = true;
    }

    /**
     * Sets a flag indicating whether the game was paused
     *
     *@param pause true if the game is paused, false otherwise
     */
    public void setPaused(boolean pause) {
        paused = pause;
    }

    /**
     * Executes the game
     */
    public void run() {
        boolean recentCollision;
        long then;
        int px;
        int pw;
        int delta;
        int paddleSpeed = 0;

        recentCollision = false;
        then = System.currentTimeMillis();

        while (!done) {
            if (((state == TITLE) || (state == OVER)) &&
                    ((System.currentTimeMillis() - lastKeyPress) > LAST_KEY_DELTA)) {
                state = MENU;
                lastKeyPress = System.currentTimeMillis();
                bricks = new BrickList(title_pattern, PATTERN_WIDTH, -1);
            }

            px = paddle.x;
            pw = paddle.width;

            if ((state == PLAY) && !paused) {
                if (key == Canvas.LEFT) {
                    paddleSpeed = Math.min(-1, -Brick.STEP);
                } else if (key == Canvas.RIGHT) {
                    paddleSpeed = Math.max(1, Brick.STEP);
                } else {
                    paddleSpeed = 0;
                }

                if (((paddleSpeed < 0) && (px > 0)) ||
                        ((paddleSpeed > 0) && ((px + pw) < Screen.width))) {
                    paddle.moveBy(paddleSpeed, 0);
                }

                ball.move();

                if ((ball.x <= 0) || (ball.x >= (Screen.width - ball.width - 1))) {
                    ball.bounceHorizontal();
                }

                if (ball.y <= 0) {
                    ball.bounceVertical();
                }

                if (ball.intersects(paddle)) {
                    if (!recentCollision) {
                        ball.bounce(paddle);
                    }

                    recentCollision = true;
                } else {
                    recentCollision = false;
                }

                score += bricks.checkForCollision(ball);

                if ((state == PLAY) && (score >= hiScore)) {
                    hiScore = score;
                }

                if (bricks.isClean()) {
                    nextLevel();
                }

                if (ball.y >= (paddle.y + paddle.height)) {
                    if (state == PLAY) {
                        lives--;
                    }

                    if (lives < 0) {
                        state = OVER;
                        level = 0;
                    }

                    restartLevel();
                }
            }

            screen.repaint();

            delta = (int)(System.currentTimeMillis() - then);

            if (delta < 30) {
                try {
                    Thread.sleep(30 - delta);
                } catch (InterruptedException e) {
                }
            }

            then = System.currentTimeMillis();
        }
    }

    /**
     * Sets engine state
     *
     * @param state a code of the engine state
     */
    protected void setState(int state) {
        this.state = state;
    }

    /**
     * Increases number of available game levels
     *
     * @param inc number of additional game levels
     */
    protected void increaseNumOfLevels(int inc) {
        availableLevels += inc;
    }

    /**
     * Increases number of available lives
     *
     * @param inc number of additional lives
     */
    protected void increaseNumOfLives(int inc) {
        availableLives += inc;
        lives += inc;
    }

    /**
     * Returns number of available game levels
     */
    protected int getAvailableLevels() {
        return availableLevels;
    }

    /**
     * Returns number of available lives
     */
    protected int getAvailableLives() {
        return availableLives + 1;
    }

    /**
     * Checks whether it is possible to buy additional game levels
     *
     * @param count number of requested additional game levels
     * @return true if it is possible to buy the levels, false otherwise
     */
    protected boolean canBuyLevels(int count) {
        return (availableLevels + count) < MAX_LEVELS;
    }

    /**
     * Checks whether it is possible to buy additional lives
     *
     * @param count number of requested additional lives
     * @return true if it is possible to buy the lives, false otherwise
     */
    protected boolean canBuyLives(int count) {
        return (availableLives + count) < MAX_LIVES;
    }

    /**
     * Shows menu
     */
    protected void showMenu() {
        state = MENU;
        oldBricks = bricks;
        bricks = new BrickList(title_pattern, PATTERN_WIDTH, -1);
        paused = true;
        screen.repaint();
    }

    /**
     * Resumes paused game
     */
    protected void resumeGame() {
        if (state == PLAY) {
            paused = true;
        }

        synchronized (this) {
            levelStarted = true;
        }

        if (oldBricks != null) {
            bricks = oldBricks;
        }

        screen.repaint();
        screen.serviceRepaints();
    }
}
