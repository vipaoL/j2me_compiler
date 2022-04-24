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
import java.util.Random;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Image;

import samples.ui.Event;
import samples.ui.EventListener;
import samples.ui.View;

import com.nokia.sm.net.ItemList;

/** This class implements the maze racer game engine */
public class MazeRacer implements EventListener, Runnable {
    private static final long CONTACT_TIMEOUT = 25000;
    private static final long PING_INTERVAL = 8000;

    private static final int WIN  = 0;
    private static final int TIE  = 1;
    private static final int LOSE = 2;

    private static final int WAITING_FOR_NUMBER   = 0;
    private static final int WAIT_FOR_FOLLOW_ACK  = 1;
    private static final int WAIT_FOR_LEAD_PING   = 2;
    private static final int WAIT_FOR_FOLLOW_PING = 3;
    private static final int WAIT_FOR_LEVEL_SEED  = 4;
    private static final int LEVEL_STARTED        = 5;
    private static final int LEVEL_COMPLETED      = 6;

    private static final byte NUMBER        = (byte) 0;
    private static final byte FOLLOW_ACK    = (byte) 1;
    private static final byte LEAD_PING     = (byte) 2;
    private static final byte FOLLOW_PING   = (byte) 3;
    private static final byte LEVEL_SEED    = (byte) 4;
    private static final byte PLAYER_UPDATE = (byte) 5;
    private static final byte COMPLETION    = (byte) 6;
    private static final byte CONCEDE       = (byte) 7;
    private static final byte VICTORY       = (byte) 8;
    private static final byte HEARTBEAT     = (byte) 9;
    private static final byte EXIT_GAME     = (byte)10;

    private static final String[] STATE_STRING = {
        "WAITING_FOR_NUMBER", "WAIT_FOR_FOLLOW_ACK", "WAIT_FOR_LEAD_PING",
        "WAIT_FOR_FOLLOW_PING", "WAIT_FOR_LEVEL_SEED", "LEVEL_STARTED",
        "LEVEL_COMPLETED"
    };

    private static final String[] ID_STRING = {
        "NUMBER", "FOLLOW_ACK", "LEAD_PING",
        "FOLLOW_PING", "LEVEL_SEED", "PLAYER_UPDATE",
        "COMPLETION", "CONCEDE", "VICTORY", "HEARTBEAT", "EXIT_GAME"
    };

    private static Random rnd = new Random();

    private Community community;
    private MazeRacerView view;
    private int num;
    private int state;
    private int delay;
    private int seed;
    private long then;
    private long pauseTime;
    private int winCount;
    private boolean singleUser;
    private boolean isLead;
    private boolean done;
    private boolean playing;
    private boolean paused;
    private int dir;
    private Player[] playerList;
    private Maze maze;
    private long levelStart;
    private long totalTime;
    private int lastDelta;
    private long lastContact;
    private long lastPacketSent;
    private int level;
    private int lastPos;
    private int lastSentPos;
    private char[] levelString;
    private char[] timeString;
    private char[] winString;
    private byte[] updateBuf;
    private Vector gameDataQueue;
    private String opponent;

    /**
     * Create a new instance of this class.
     * @param community Community instance
     */
    public MazeRacer(Community community) {
        Canvas canvas;
        boolean smallMaze;
        int w, h;

        this.community = community;

        levelString = new char[] {'l', 'e', 'v', 'e', 'l', ':', '0', '1'};
        timeString  = new char[] {'0', '0', ':', '0', '0', '.', '0'};
        winString   = new char[] {'0', '/', '0'};
        lastContact = -1;
        level = 0;
        done = true;
        playing = false;
        opponent = "Opponent";

        updateBuf = new byte[5];
        updateBuf[0] = PLAYER_UPDATE;

        canvas = community.getCanvas();
        smallMaze = "true".equals(community.getMainApp().getProperty("Small-Maze"));

        h = smallMaze ? 5 : (canvas.getHeight() - 40) / MazeRacerView.CELL_SIZE - 2;
        w = canvas.getWidth() / MazeRacerView.CELL_SIZE - 2;

        if ((w & 1) == 0) w--;
        if ((h & 1) == 0) h--;

        maze = new Maze(w, h);
        gameDataQueue = new Vector();

        Image img = null;
        String[] fileNameList = {"/player_one.png", "/player_two.png"};

        playerList = new Player[2];

        for (int i=0; i<playerList.length; i++) {
            try {img = Image.createImage(getClass().getResourceAsStream(fileNameList[i]));}
            catch (IOException e) {e.printStackTrace();}

            playerList[i] = new Player(maze, img, 18);
        }

        view = new MazeRacerView(community, this, maze, playerList);
        view.setTicker("Preparing, please wait...");
    }

    private void setState(int state) {
        this.state = state;
        System.out.println("state = " + STATE_STRING[state]);
    }

    /**
     * Check if this instance is running a single-user game.
     * @return <code>true</code> if single-user, <code>false</code> otherwise.
     */
    public boolean isSingleUser() {
        return singleUser;
    }

    char[] getWins() {
        return winString;
    }
    
    public void setOpponent( String opponent)
    {
    	this.opponent = opponent;
    }
    
    public String getOpponent()
    {
    	return opponent;
    }

    /**
     * Reset this instance.
     * @param singleUser <code>true</code> to reset for single-user, <code>false</code otherwise.
     */
    public void reset(boolean singleUser) {
        this.singleUser = singleUser;

        if (singleUser) resetSinglePlayer();
        else resetMultiPlayer();

        for (int i=0; i<playerList.length; i++) {
            playerList[i].setPosition(maze.getWidth());
        }

        dir = 0;

        level++;
        levelString[levelString.length - 1] = (char)('0' + level % 10);
        levelString[levelString.length - 2] = (char)('0' + level / 10);

        lastContact = System.currentTimeMillis();

        view.repaint();
    }

    /**
     * Reset this instance for single-user play.
     */
    public void resetSinglePlayer() {
        maze.reset();
        setState(LEVEL_STARTED);
        view.setTicker("Go!");
        levelStart = System.currentTimeMillis();
    }

    /**
     * Reset this instance for multi-user play.
     */
    public void resetMultiPlayer() {
        setState(WAITING_FOR_NUMBER);

        num = getRandom(0, 100);
        //System.out.println("created random number: " + num);

        sendGamePacket(new byte[] {NUMBER, (byte)num});
    }

    private void sendGamePacket(byte[] buf) {
        if (singleUser) return;

        ItemList itemList = new ItemList();
        itemList.setItem("cmd", "sendGamePacket");
        itemList.setItem("data", buf);

        if (buf[0] == PLAYER_UPDATE || buf[0] == HEARTBEAT) {
            itemList.setItem(Community.NON_CRITICAL, "true");
        } else {
            System.out.println("send: " + ID_STRING[buf[0]]);
            community.cancelNonCriticalCmds();
        }

        community.executeCmd(itemList);
        lastPacketSent = System.currentTimeMillis();
    }

    private int getRandom(int low, int high) {
        return low + Math.abs(rnd.nextInt() % (high - low));
    }

    /**
     * Get the view component for this instance.
     * @return the view component for this instance.
     */
    public View getView() {
        return view;
    }

    /**
     * Handle an event.
     * @param e The event to handle.
     */
    public boolean handleEvent(Event e) {
        if (!view.isActive()) return false;
        return true;
    }

    /**
     * Handle a key-press event.
     * @param k The key code.
     * @param action The action code.
     */
    public void keyPressed(int key, int action) {
        if (state == LEVEL_STARTED && playerList[0].canMove(action)) dir = action;
    }

    /**
     * Handle a key-release event.
     * @param k The key code.
     * @param action The action code.
     */
    public void keyReleased(int key, int action) {
    }

    private void packInteger(byte[] buf, int offset, int value) {
        for (int i=0, shift=24; i<4; i++, shift-=8) {
            buf[offset + i] = (byte)((value >> shift) & 0xff);
        }
    }

    private int unpackInteger(byte[] buf, int offset) {
        int value = 0;

        for (int i=0; i<4; i++) {
            value = (value << 8) | (buf[offset + i] & 0xff);
        }

        return value;
    }

    /**
     * Terminates game.  Also sends out a "GAME OVER" message
     * to the other player and a "gameStop" command to the 
     * SNAP servers, if the game hasn't already been terminated.
     * 
     * @param sleep
     */
    void gameOver( boolean sleep) 
    {
        if (!singleUser && playing && community.isLoggedIn()) {
            sendGamePacket(new byte[] {EXIT_GAME});

            ItemList itemList = new ItemList();
            itemList.setItem("cmd", "gameStop");
            itemList.setItem("gcid", community.getGCID());
            community.executeCmd(itemList);
        }
        playing = false;

        if (sleep) {
            try {Thread.sleep(3000);}
            catch (InterruptedException e) {}
        }

        stop();
    }

    private boolean isSetupState() {
        return state == WAITING_FOR_NUMBER
            || state == WAIT_FOR_FOLLOW_ACK
            || state == WAIT_FOR_LEAD_PING
            || state == WAIT_FOR_FOLLOW_PING
            || state == WAIT_FOR_LEVEL_SEED;
    }

    public boolean isPlaying() { return playing; } 
    
    /**
     * Handle received game data.  Data will be either game setup and  
     * synchronization commands, or in-game updates such as current
     * player position and/or heartbeats if the player hasn't moved
     * in a while.
     * <p>
     * Setup/synchronization updates will result in the game changing 
     * setup state & issuing further setup commands.  Player position
     * updates will result in the game GUI being updated.
     * 
     * @param from The sender of this data.
     * @param data The game data.
     */
    public void gameDataReceived(String from, byte[] data) 
   	{
        int id, arg, pos, rdelta;
    		
        if (!playing) return;
        
        lastContact = System.currentTimeMillis();
        
        id = data[0];
        if (id != PLAYER_UPDATE) System.out.println("receive: " + ID_STRING[id]);
	
        if (id == EXIT_GAME) {
        	gameOver( false);
            community.showError(
            	opponent + " quit, game over.",
                Community.MAZEEXIT, Dialog.ALERT
                );

            return;
        }

        if (id == HEARTBEAT) {
            System.out.println("heartbeat received.");
            return;
        }

        if (state == WAITING_FOR_NUMBER && id == NUMBER) {
            if (num == data[1]) {
                reset(singleUser);
                return;
            } else {

	            isLead = num > data[1];
	            if (!isLead) {
	            	sendGamePacket(new byte[] {FOLLOW_ACK});
	            	setState(WAIT_FOR_LEAD_PING);
	            } else {
	            	setState(WAIT_FOR_FOLLOW_ACK);	            	
	            }
//	            setState(isLead ? WAIT_FOR_FOLLOW_ACK : WAIT_FOR_LEAD_PING);
	            return;
            }
        }

        if (state == WAIT_FOR_FOLLOW_ACK && id == FOLLOW_ACK) {
            sendGamePacket(new byte[] {LEAD_PING});
            setState(WAIT_FOR_FOLLOW_PING);
            then = System.currentTimeMillis();
            return;
        }

        if (state == WAIT_FOR_LEAD_PING && id == LEAD_PING) {
            sendGamePacket(new byte[] {FOLLOW_PING});
            setState(WAIT_FOR_LEVEL_SEED);
            return;
        }

        if (state == WAIT_FOR_FOLLOW_PING && id == FOLLOW_PING) {
            byte[] buf;

            delay = (int)(System.currentTimeMillis() - then) / 2;
            seed = rnd.nextInt();
            Util.setSeed(seed);

            maze.reset();
            view.repaint();

            buf = new byte[5];
            buf[0] = LEVEL_SEED;
            packInteger(buf, 1, seed);
            sendGamePacket(buf);

            try {Thread.sleep(delay);}
            catch (InterruptedException e) {}

            view.setTicker("Go!");
            setState(LEVEL_STARTED);
            levelStart = System.currentTimeMillis();
//            lastContact = -1;

            return;
        }

        if (state == WAIT_FOR_LEVEL_SEED && id == LEVEL_SEED) {
            delay = 0;
            seed = unpackInteger(data, 1);
            Util.setSeed(seed);

            maze.reset();
            view.repaint();

            view.setTicker("Go!");
            setState(LEVEL_STARTED);
            levelStart = System.currentTimeMillis();
//            lastContact = -1;

            return;
        }

        if ((state == LEVEL_STARTED || state == LEVEL_COMPLETED) && id == PLAYER_UPDATE) {
            pos = unpackInteger(data, 1);
            playerList[1].setPosition(pos);

            return;
        }

        if (state == LEVEL_STARTED && id == COMPLETION) {
            setState(LEVEL_COMPLETED);
            sendGamePacket(new byte[] {CONCEDE});
            winString[2]++;
            view.setTicker( opponent + " finished first.");
            reset(singleUser);

            return;
        }

        if (state == LEVEL_COMPLETED && id == CONCEDE) {
            winCount++;
            winString[0] = (char)('0' + winCount);

            if (winCount > 2) {
                view.setTicker("You won!");
                sendGamePacket(new byte[] {VICTORY});

                ItemList itemList;

                itemList = new ItemList();
                itemList.setItem("cmd", "gameStop");
                itemList.setItem("gcid", community.getGCID());
                itemList.setItem("category1", "TOTAL_TIME");
                itemList.setItem("value1", "" + new Integer((int)(totalTime / 1000)));
                itemList.setItem("category2", "WINS");
                itemList.setItem("value2", "1");

                community.executeCmd(itemList);

                playing = false;
                gameOver(true);
                community.showDialog(
                        "Game Over", "Game over, you won!",
                        null, Community.MAZEEXIT, Dialog.ALERT
                        );

            } else {
                view.setTicker("You finished first!");
                reset(singleUser);
            }

            return;
        }

        if (state == LEVEL_COMPLETED && id == COMPLETION) {
            rdelta = unpackInteger(data, 1);

            if (rdelta < lastDelta) {
                sendGamePacket(new byte[] {CONCEDE});
                winString[2]++;
                view.setTicker( opponent + " finished first.");
            } else {
                winCount++;
                winString[0] = (char)('0' + winCount);
                view.setTicker("You finished first!");
            }

            reset(singleUser);

            return;
        }

        if (id == VICTORY) {
            winCount = -1;
            view.setTicker( opponent + " won :(");

            ItemList itemList = new ItemList();
            itemList.setItem("cmd", "gameStop");
            itemList.setItem("gcid", community.getGCID());
            itemList.setItem("category1", "LOSSES");
            itemList.setItem("value1", "1");

            community.executeCmd(itemList);

            playing = false;
            gameOver( true);
            community.showDialog(
                    "Game Over", "Game over, " + opponent + " won!",
                    null, Community.MAZEEXIT, Dialog.ALERT
                    );	
            return;
        }

        System.out.println("received stray packet, id = " + id);
   	}
   	
    /**
     * Get level indicator as char array.
     * @return level indicator as char array.
     */
    public char[] getLevel() {
        return levelString;
    }

    /**
     * Get time indicator as char array.
     * @return time indicator as char array.
     */
    public char[] getTime() {
        return timeString;
    }

    /**
     * Called when the app is put into a paused state, such as
     * during an incoming phone call.
     */
    public void pause() { 
    	synchronized (this) { 
    		paused = true; 
    	}
    	pauseTime = System.currentTimeMillis();
    }
    
    /**
     * Called when app is woken up from a paused state, e.g. after
     * an incoming call.  If the app has been asleep too long, the
     * game will exit.
     */
    public void resume() { 
    	synchronized (this) { 
    		paused = false; 
    	}
    	if (playing && 
    		System.currentTimeMillis()-pauseTime >= CONTACT_TIMEOUT) 
    	{
            gameOver( false);
            community.showDialog(
                    "Game Over", "Sorry, your game was paused too long. " +
                    "Game Over.  Please try again!",
                    null, Community.MAZEEXIT, Dialog.ALERT
                    );	
    	} 
    }
    
    /**
     * Updates the current time and its display in the GUI.
     */
    private void updateTime() {
        int min, sec, tenth, millis;

        millis = (int)(System.currentTimeMillis() - levelStart);
        min = millis / (1000 * 60);

        millis = millis % (1000 * 60);
        sec = millis / 1000;

        millis = millis % 1000;
        tenth = millis / 100;

        timeString[timeString.length - 1] = (char)('0' + tenth);
        timeString[timeString.length - 3] = (char)('0' + sec % 10);
        timeString[timeString.length - 4] = (char)('0' + sec / 10);
        timeString[timeString.length - 6] = (char)('0' + min % 10);
        timeString[timeString.length - 7] = (char)('0' + min / 10);
    }

	/**
	 * Check to see if we should send an updated position to 
     * the other player -- only send if we've moved at least
     * 2 pixels in Y or Y to reduce the number of messages 
     * passed back and forth during gameplay.
     * 
	 * @param pos
	 */
    private void handlePosChanged( int pos)
    {
        int w = maze.getWidth();
        int oldX = lastSentPos % w;
        int oldY = lastSentPos / w;
        int x = pos % w;
        int y = pos / w;
        int absX = Math.abs( x-oldX);
        int absY = Math.abs( y-oldY);
        if (absX>1 || absY>1) {
            packInteger(updateBuf, 1, pos);
            sendGamePacket(updateBuf);
            lastSentPos = pos;
        }
    }

    
    /**
     * Terminate the thread driving this instance.
     */
    public void stop() {
        done = true;
    }

    /** 
     * Main game loop. Executed each time a new game is played.  
     * Sends out position updates and/or heartbeats in case the 
     * player hasn't moved within the heartbeat interval. Also
     * sends out notification when the user reaches the end of
     * the maze.
     */
    public void run() {
        int stop, pos;

        done = false;
        playing = true;
        stop = maze.getWidth() * maze.getHeight() - maze.getWidth() - 1;
        level = 1;
        winCount = 0;
        lastContact = -1;
        lastPacketSent = -1;

        timeString  = new char[] {'0', '0', ':', '0', '0', '.', '0'};
        levelString[levelString.length - 1] = (char)('0' + level % 10);
        levelString[levelString.length - 2] = (char)('0' + level / 10);
        winString[0] = '0';
        winString[2] = '0';

        if (!singleUser) view.setTicker("Preparing, please wait...");
        view.repaint();

        while (!done) {

        	
        	while (paused) {
        		try { Thread.sleep( 500); } catch (Exception ignore) {}
        		if (done || !playing) return;
        	}

            if (lastContact > 0 && !singleUser && playing ) {
            	if((System.currentTimeMillis()-lastContact) >= CONTACT_TIMEOUT) {
	                community.showDialog(
	                        "Error", "Lost contact with your opponent. Game over, " +
	                        "please try again!",
	                        null, Community.MAZEEXIT, Dialog.ALERT
	                        );
	                gameOver(false);
	                break;
            	} else if((System.currentTimeMillis()-lastContact) >= 5000) {
            		synchronized(this) {
            			if(state == WAIT_FOR_LEAD_PING) {
            				sendGamePacket(new byte[] {NUMBER, (byte)num});
            				sendGamePacket(new byte[] {FOLLOW_ACK});
            			} else if (state == WAIT_FOR_FOLLOW_ACK) {
            				sendGamePacket(new byte[] {NUMBER, (byte)num});
            			}
            		}
            	}
            }

            if (state == LEVEL_STARTED && playing) {
                updateTime();
                playerList[0].move(dir);

                pos = playerList[0].getPosition();
                if (lastPos != pos) handlePosChanged( pos);
                lastPos = pos;
                
               if (pos == stop) {
                    setState(LEVEL_COMPLETED);

                    lastDelta = (int)(System.currentTimeMillis() - levelStart);
                    totalTime += lastDelta;

                    byte[] buf = new byte[5];
                    buf[0] = COMPLETION;
                    packInteger(buf, 1, lastDelta);
                    sendGamePacket(buf);

                    if (singleUser) reset(singleUser);
                    else view.setTicker("Maze solved, syncing with opponent...");
                }
            }
            
            // Send a heartbeat ping if we haven't sent any other
            // game messages in a while
            if (lastPacketSent > 0 && !paused && playing &&
                (System.currentTimeMillis()-lastPacketSent)>PING_INTERVAL)
            {
            	sendGamePacket(new byte[] {HEARTBEAT});
            }

            view.repaint();

            try {Thread.sleep(100);}
            catch (InterruptedException e) {}
        }
    }
}