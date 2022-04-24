/******************************************************************************/
/**
 *        @file        Life3D.java
 *        @brief        Implements a 3D version of Conway's "game of life".
 *
 *        Copyright (C) 2004 Superscape plc
 *
 *        This file is intended for use as a code example, and
 *        may be used, modified, or distributed in source or
 *        object code form, without restriction, as long as
 *        this copyright notice is preserved.
 *
 *        The code and information is provided "as-is" without
 *        warranty of any kind, either expressed or implied.
 */

/******************************************************************************/
package com.superscape.m3g.wtksamples.life3d;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.*;
import javax.microedition.m3g.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;


/**
 *         Life3D Midlet - implements a 3D version of Conway's "game of life".
 *
 *        The "game of life" is an interesting system discovered by John Conway in 1968.
 *        This is a generalization of that system to 3D.
 *
 *        The game grid is divided into cubical cells, each of which can be alive or dead.
 *        A live cell is indicated by a cube at that position; a dead cell is vacant.
 *
 *        At each generation, the number of living neighbours for each cell is counted, and
 *        the state of the cell in the next generation is determined by that count. Each has
 *        26 possible neighbors (including ones that are diagonal in one or more directions),
 *        and if a live cell has fewer than 4 live neighbors, it dies of loneliness. If it has
 *        more than 5 live neighbors, it dies of overcrowding. A vacant cell will have a new
 *        cell created inside it if it has exactly 4 live neighbors.
 *
 *        This apparently simple rule, when applied to all the cells simultaneously, leads
 *        to surprisingly complex behavior. Certain patterns of cells are stable, some die
 *        out, some flip between two or more configurations, and some actually progress through
 *        the grid in an ordered manner (these are called "gliders").
 *
 *        Although the grid starts in a random state, we have added the most interesting patterns
 *        we have found to a pattern library, which can be accessed from the keyboard.
 *
 *        Keys:
 *                0: Pause generation
 *                1: Full speed (1 generation per frame)
 *                2: Increase generation speed
 *                3: Decrease generation speed
 *                4: Load previous pattern in library
 *                5: Load next pattern in library
 *                *: Load random pattern
 *
 *        If a pattern dies out completely, then the game restarts with a random pattern again.
 */
public class Life3D extends MIDlet implements CommandListener {
    // Survival and birth parameters for individual cells.
    private static final int minSurvive = 4;
    private static final int maxSurvive = 5;
    private static final int minBirth = 5;
    private static final int maxBirth = 5;

    // The cube size must be a power of 2 for the optimized wrapping
    // code to work properly, so to ensure this, we specify the exponent
    // here. 2 to the 3 is 8, so the cube is 8x8x8.
    private static final int CUBESIZE_BITS = 3;

    // The number of cells along each edge of the cube
    private static final int CUBESIZE = (1 << CUBESIZE_BITS);

    // The total number of cells in the cube
    private static final int NUMCELLS = (CUBESIZE * CUBESIZE * CUBESIZE);

    // Various masks and values, used in the optimized update routine
    private static final int STEPX = (CUBESIZE * CUBESIZE);
    private static final int STEPY = (CUBESIZE);
    private static final int STEPZ = 1;
    private static final int MASKX = STEPX * (CUBESIZE - 1);
    private static final int MASKY = STEPY * (CUBESIZE - 1);
    private static final int MASKZ = STEPZ * (CUBESIZE - 1);
    private static final int SHIFTX = CUBESIZE_BITS * 2;
    private static final int SHIFTY = CUBESIZE_BITS;
    private static final int SHIFTZ = 0;

    // Variables.

    // Reference to the MIDlet's display
    private Display myDisplay = null;

    // Reference to the current canvas.
    private CallbackCanvas myCanvas = null;

    // Reference to a refresh timer, which is used to schedule screen updates
    private Timer myRefreshTimer = new Timer();

    // Reference to a TimerTask-derived object that calls back to the MIDlet
    private TimerTask myRefreshTask = null;

    // A command which is used to exit from the MIDlet.
    private Command exitCommand = new Command("Exit", Command.ITEM, 1);

    // Reference to the world which contains the 3D cells.
    private World myWorld = null;

    // The root group, which is the one we rotate slowly.
    private Group rootGroup = null;

    // References to the 3D objects that represent the cells,
    // one for each cell in the grid.
    private Mesh[] cells;

    // Array of neighbor counts for calculating the next state,
    // one for each cell in the grid.
    private byte[] nextState;

    // Array of cell states (1=alive, 0=dead), one for each cell
    // in the grid. This is not held as a boolean, so that we can
    // then use this value directly to update the number of live
    // neighbors.
    private byte[] currentState;

    // Reference to a random number generator.
    private Random rand = null;

    // Current viewing angle. This rotates slowly.
    private float angle = 0.0f;

    // Delay (number of frames) between one generation and the next.
    private int delay = 1;

    // Delay until next generation. This counts down from "delay" to 0,
    // and a new generation is calculated when it hits 0.
    private int delayCount = 0;

    // Count of number of live cells.
    private int population = 0;

    // Number of generations calculated since last reload.
    private int generations = 0;

    // Current pattern number from library.
    private int pattern = -1;

    // Current pattern name from library.
    private String patternName = "Random";

    // Pattern library - names and state strings for a number of interesting
    // patterns - feel free to add your own!
    private String[][] patternLibrary =
        {
            { "L block          ", "433434443533534543", },
            { "Skew L block     ", "333334343344433444", },
            { "Opposed L block  ", "254263264353354363", },
            

            { "Beehive          ", "353344364355453444464455", },
            { "Skew beehive     ", "323333412421423431433442521531", },
            { "Broken beehive   ", "434443454535546555645646343344", },
            { "Skew beehive plus", "254264345354356364366375445456466", },
            { "Beehive var 75   ", "515516524525605606634635715716724725", },
            { "Ascend+descend   ", "034044133143145243246256344345354355", },
            { "Battered ring    ", "235244245325336354355425434446455535545", },
            { "Ring             ", "243253334335346343356353364365434435446456464465", },
            

            { "Cage             ", "334345455454424435433554534545543", },
            { "Skew cage        ", "413422424433513514532533623", },
            

            { "Ball             ", "326335337346425427445447526535537546", },
            { "Ball 1 cap       ", "326335337346425427445447526535537546235", },
            { "Ball 2 caps      ", "326335337346425427445447526535537546235635", },
            { "Ball 2 caps hole ", "445447456416425427546526537346326335", },
            

            { "Pinwheel         ", "213214223224303315322334413414423424", },
            

            { "Columns          ", "445447457546556245255344346354", },
            { "Columns ext      ", "445447457546556245255344346354144243043053142152", },
            

            { "Staircase        ", "462472540541561563572640641650652", },
            { "Cupped hands     ", "035045134136144226234236244325335", },
            

            { "New 1            ", "013022114113125122134133225234", },
            { "New 2            ", "422423432522523544543634644643", },
            { "New 3            ", "344455433435443446554555534543546", },
            { "New 4            ", "111112202210213222301302310321411", },
            { "New 5            ", "343353442454452545542555533644643654", },
            { "New 6            ", "440450452461541550241251252340342362", },
            { "New 7            ", "427434444447535536545546326335336337", },
            { "New 8            ", "241250252261340341360362372450461471", },
            { "New 9            ", "414415435433524525234233344343325323", },
            { "New 10           ", "456466165166175176257264267274357365367", },
            { "New 11           ", "156165166255265266472502562571573601661663762771772", },
            { "New 12           ", "124125134136224226233237246336343347354356444446454455", },
            

            { "Compass          ", "333432434423443533", },
            

            { "P2 Wonky Compass ", "344454434443445554534", },
            { "P2 Double Compass", "461462552553572573661662", },
            

            { "P2 Blinker       ", "151161241250252260262271351361", },
            { "P2 Repeater      ", "445446534536544555645646", },
            { "P2 Propellor     ", "444424425436545535345334336", },
            { "P2 Magic Basket  ", "344345354334446443424425436546543524525533644645655635", },
            

            { "P4 Boat          ", "333334423424432435443444", },
            { "P4 Rotor         ", "225235316325327335337346426436", },
            { "P4 Beehive       ", "353344364355365453444464455465", },
            { "P4 Shuffler      ", "356445447455466545547555566656", },
            { "P4 Popper        ", "333334343344433435445454534544", },
            

            { "P4 Glider        ", "111112121122013023010020001002", },
        };

    /**
     *         Default constructor.
     *
     *  This just sets up a canvas and attaches it to the display. The actual
     *        initialization happens in startApp.
     */
    public Life3D() {
        // Set up the user interface.
        myDisplay = Display.getDisplay(this);
        myCanvas = new CallbackCanvas(this);
        myCanvas.setCommandListener(this);
        myCanvas.addCommand(exitCommand);
    }

    /**
     *        This initializes the game state, and generates a M3G world programmatically.
     */
    public void startApp() throws MIDletStateChangeException {
        // Catch exceptions here before they go too far.
        try {
            // Create a new M3G world.
            myWorld = new World();

            // In this world, we have a root group which will contain everything else
            // and which is tilted 15 degrees towards the camera.
            Group rootGroup2 = new Group();
            myWorld.addChild(rootGroup2);
            rootGroup2.setOrientation(15.0f, 1.0f, 0.0f, 0.0f);

            // Under this, we have a second group which will be the one we rotate
            // to get an all-round view of the game grid.
            rootGroup = new Group();
            rootGroup2.addChild(rootGroup);

            // We now create a parallel camera - parallel projection is faster than
            // perspective, and since we are rendering 512 separate objects that's a
            // saving worth having.
            Camera myCamera = new Camera();
            myWorld.addChild(myCamera);
            myWorld.setActiveCamera(myCamera);

            myCamera.setParallel(CUBESIZE * 1.5f, 1.0f, -CUBESIZE, CUBESIZE);

            // This is geometry data for the shape that represents a single cell - a cube. 
            // It consists of 6 triangle strips, one for each face, each of which
            // has 2 triangles (and therefore 4 vertices). We will set the vertex
            // colors so that the colors of the sides are different from each other.

            // This data is shared by all the cells in the grid, rather than each having
            // its own copy. This keeps memory overhead down.
            int[][] aaStripLengths = {
                    { 4 },
                    { 4 },
                    { 4 },
                    { 4 },
                    { 4 },
                    { 4 }
                };

            // These are the vertex positions
            short[] aPos =
                {
                    
                    // Front
                    -1, -1, 1, // B
                    1, -1, 1, // C
                    -1, 1, 1, // A
                    1, 1, 1, // D
                             // Bottom
                    -1, -1, -1, // F
                    1, -1, -1, // G
                    -1, -1, 1, // B
                    1, -1, 1, // C
                              // Top
                    -1, 1, 1, // A
                    1, 1, 1, // D
                    -1, 1, -1, // E
                    1, 1, -1, // H
                              // Right
                    1, 1, 1, // D
                    1, -1, 1, // C
                    1, 1, -1, // H
                    1, -1, -1, // G
                               // Left
                    -1, -1, 1, // B
                    -1, 1, 1, // A
                    -1, -1, -1, // F
                    -1, 1, -1, // E
                               // Back
                    1, -1, -1, // G
                    -1, -1, -1, // F
                    1, 1, -1, // H
                    -1, 1, -1 // E
                };

            // These are the colors for the vertices
            byte[] aCol =
                {
                    
                    // Front
                    -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, // Bottom
                    0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, // Top
                    0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 
                    // Right
                    -1, -1, 0, -1, -1, 0, -1, -1, 0, -1, -1, 0, 
                    // Left
                    -1, 0, -1, -1, 0, -1, -1, 0, -1, -1, 0, -1, // Back
                    0, -1, -1, 0, -1, -1, 0, -1, -1, 0, -1, -1,
                };

            // Calculate the number of submeshes and vertices directly from the sizes
            // of the arrays. This prevents us getting a mismatch if we decide to change
            // the cells to a different shape later.
            int cSubmeshes = aaStripLengths.length;
            int cVertices = aPos.length / 3;

            // We will share a default appearance between all the faces on the cube. Each
            // face is a separate "submesh" - it can have a separate appearance if we wish.
            Appearance app = new Appearance();

            // We need to specify an appearance and the submesh data for each face.
            Appearance[] appa = new Appearance[cSubmeshes];
            IndexBuffer[] iba = new IndexBuffer[cSubmeshes];

            int startIndex = 0;

            for (int i = 0; i < cSubmeshes; i++) {
                // We use the same appearance for each.
                appa[i] = app;

                // And we create a new triangle strip array for each submesh.
                // The start index for each one just follows on from previous submeshes.
                iba[i] = new TriangleStripArray(startIndex, aaStripLengths[i]);

                for (int j = 0; j < aaStripLengths[i].length; j++)
                    startIndex += aaStripLengths[i][j];
            }

            // Now we create a new vertex buffer that contains all the above information
            VertexBuffer vertexBuffer = new VertexBuffer();
            vertexBuffer.setDefaultColor(0xFFFFFFFF); // white

            {
                // Copy the vertex positions into a VertexArray object
                VertexArray vaPos = new VertexArray(cVertices, 3, 2);
                vaPos.set(0, cVertices, aPos);
                vertexBuffer.setPositions(vaPos, 0.40f, null);
            }

            {
                // Copy the vertex colors into a VertexArray object
                VertexArray vaCols = new VertexArray(cVertices, 3, 1);
                vaCols.set(0, cVertices, aCol);
                vertexBuffer.setColors(vaCols);
            }

            // Create all the cells, in a random state.
            // The X, Y and Z positions of the cells range from -CUBESIZE/2 to +CUBESIZE/2 units.
            // They are all children of the rootGroup object.
            cells = new Mesh[NUMCELLS];
            nextState = new byte[NUMCELLS];
            currentState = new byte[NUMCELLS];
            rand = new Random();

            int index = 0;

            for (int i = 0; i < CUBESIZE; i++) {
                float x = ((i * 2) - CUBESIZE) * 0.5f;

                for (int j = 0; j < CUBESIZE; j++) {
                    float y = ((j * 2) - CUBESIZE) * 0.5f;

                    for (int k = 0; k < CUBESIZE; k++) {
                        float z = ((k * 2) - CUBESIZE) * 0.5f;

                        Mesh m = new Mesh(vertexBuffer, iba, appa);
                        m.setTranslation(x, y, z);
                        rootGroup.addChild(m);

                        // This test gives a 1 in 4 chance of being alive at the start
                        currentState[index] = (rand.nextInt() > 0x40000000) ? (byte)1 : (byte)0;
                        cells[index++] = m;
                    }
                }
            }

            // Attach to display
            myDisplay.setCurrent(myCanvas);

            // Force a repaint so that we get the update loop started.
            myCanvas.repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *        If cell[i] is alive, this increments the "live neighbor" count
         *        on all the neighboring cells. This is more efficient than counting
         *        the neighboring cells for each cell, because there are likely to be
         *        fewer live cells than dead cells.
         *
         *        The cube wraps around, so that the neighbor to the right of a cell
         *        in the last row is in fact in the first row. The same happens with
         *        columns and planes. We speed things up here by using bit operations
         *        to effect this wrapping (which is why CUBESIZE must be a power of 2)
         *        and we also unroll the loop.
     */
    public void updateNeighbours(int i) {
        if (currentState[i] != 0) {
            int ix0 = (i - STEPX) & MASKX;
            int iy0 = (i - STEPY) & MASKY;
            int iz0 = (i - STEPZ) & MASKZ;

            int ix1 = (i) & MASKX;
            int iy1 = (i) & MASKY;
            int iz1 = (i) & MASKZ;

            int ix2 = (i + STEPX) & MASKX;
            int iy2 = (i + STEPY) & MASKY;
            int iz2 = (i + STEPZ) & MASKZ;

            ++nextState[ix0 | iy0 | iz0];
            ++nextState[ix0 | iy0 | iz1];
            ++nextState[ix0 | iy0 | iz2];
            ++nextState[ix0 | iy1 | iz0];
            ++nextState[ix0 | iy1 | iz1];
            ++nextState[ix0 | iy1 | iz2];
            ++nextState[ix0 | iy2 | iz0];
            ++nextState[ix0 | iy2 | iz1];
            ++nextState[ix0 | iy2 | iz2];

            ++nextState[ix1 | iy0 | iz0];
            ++nextState[ix1 | iy0 | iz1];
            ++nextState[ix1 | iy0 | iz2];
            ++nextState[ix1 | iy1 | iz0];

            //!		++nextState[ix1|iy1|iz1];
            ++nextState[ix1 | iy1 | iz2];
            ++nextState[ix1 | iy2 | iz0];
            ++nextState[ix1 | iy2 | iz1];
            ++nextState[ix1 | iy2 | iz2];

            ++nextState[ix2 | iy0 | iz0];
            ++nextState[ix2 | iy0 | iz1];
            ++nextState[ix2 | iy0 | iz2];
            ++nextState[ix2 | iy1 | iz0];
            ++nextState[ix2 | iy1 | iz1];
            ++nextState[ix2 | iy1 | iz2];
            ++nextState[ix2 | iy2 | iz0];
            ++nextState[ix2 | iy2 | iz1];
            ++nextState[ix2 | iy2 | iz2];
        }
    }

    /**
     *        Works out current alive/dead state based on neighbour count.
     *        If a cell is alive, it will die of loneliness if it has at fewer than
     *        minSurvive neighbors, but if it has more than maxSurvive neighbors it
     *        will die of overcrowding. If a cell has between minBirth and maxBirth
     *        neighbours, and it is currently dead, a new cell is born in that position.
     */
    public void updateState(int i) {
        byte count = nextState[i];
        nextState[i] = 0;

        if (currentState[i] == 0) {
            currentState[i] = ((count >= minBirth) && (count <= maxBirth)) ? (byte)1 : (byte)0;
        } else {
            currentState[i] = ((count >= minSurvive) && (count <= maxSurvive)) ? (byte)1 : (byte)0;
        }

        // After calculating the new state, set the appropriate rendering enable for the
        // cell object in the world, so we can see it. We take advantage of this test to
        // count the current live population, too.
        if (currentState[i] != 0) {
            cells[i].setRenderingEnable(true);
            ++population;
        } else {
            cells[i].setRenderingEnable(false);
        }
    }

    /**
     * On pause, simply shut everything down.
     */
    public void pauseApp() {
        myRefreshTask.cancel();
        myRefreshTask = null;
        // Release resources.
        myWorld = null;
    }

    /**
     * On exit, simply shut everything down
     */
    public void destroyApp(boolean unconditional) throws MIDletStateChangeException {
        myRefreshTimer.cancel();
        myRefreshTimer = null;
        myRefreshTask = null;

        // Release resources.
        myWorld = null;
        myCanvas = null;
    }

    /**
     *        MIDlet paint method.
     *
     *         This is called back from the inner Canvas class. It renders the current state of the
     *        cells, then updates them and schedules another update.
     */
    public void paint(Graphics g) {
        // We are not fully initialised yet; just return.
        if ((myCanvas == null) || (myWorld == null)) {
            return;
        }

        // If this was a scheduled update, cancel the timer task that caused it.
        if (myRefreshTask != null) {
            myRefreshTask.cancel();
            myRefreshTask = null;
        }

        // Render the world to our Graphics (the screen) using the m3g class Graphics3D
        // Note the use of try/finally to ensure that the Graphics3D always releases
        // the target no matter what happens.
        Graphics3D myGraphics3D = Graphics3D.getInstance();
        myGraphics3D.bindTarget(g);

        try {
            myGraphics3D.render(myWorld);
        } finally {
            myGraphics3D.releaseTarget();
        }

        // Draw information about the pattern: its name, the number of generations,
        // and the number of live cells.
        g.setColor(0xFFFFFFFF);
        g.drawString(generations + " : " + population, 0, 10, Graphics.LEFT | Graphics.TOP);
        g.drawString(patternName, 0, 0, Graphics.LEFT | Graphics.TOP);

        // Always rotate slowly. We set the orientation of rootGroup to achieve this,
        // rotating it about its local Y (vertical) axis.
        angle += 1.0f;
        rootGroup.setOrientation(angle, 0.0f, 1.0f, 0.0f);

        // This deals with the speed setting. We have a countdown that is set to
        // an initial value of "delay", and when it reaches 0, we update the cells.
        // This allows us to keep rotating the view, even if generation is paused.
        if (--delayCount <= 0) {
            delayCount = delay;
            ++generations;

            // Garbage collect regularly
            System.gc();

            // Now calculate next frame

            // Update all the cells
            for (int i = 0; i < NUMCELLS; i++)
                updateNeighbours(i);

            population = 0;

            for (int i = 0; i < NUMCELLS; i++)
                updateState(i);

            // If all the cells have died out, restart with a random state.
            if (population == 0) {
                loadRandomState();
            }
        }

        // And schedule another repaint. This uses the Java timers to schedule
        // another repaint in 50 milliseconds. This is more efficient than doing
        // it immediately, and limits the frame rate to 20 frames/second. It also
        // allows other applications some time!
        myRefreshTask = new RefreshTask();
        myRefreshTimer.schedule(myRefreshTask, 50);
    }

    /**
     *        This loads the cell state from a string stored in the pattern library.
     *        Each live cell is represented in the string using a triplet of characters
     *        showing its x, y and z position. For example, the string "000" represents
         *        a single live cell at the extreme bottom front right corner.
     */
    private void loadStateFromString(String positions) {
        clear();

        for (int i = 0; i < positions.length(); i += 3) {
            int xx = (positions.charAt(i + 0) - '0') & (CUBESIZE - 1);
            int yy = (positions.charAt(i + 1) - '0') & (CUBESIZE - 1);
            int zz = (positions.charAt(i + 2) - '0') & (CUBESIZE - 1);

            currentState[(((xx * CUBESIZE) + yy) * CUBESIZE) + zz] = 1;
        }
    }

    /**
     *        Clears the cell array to all dead.
     */
    private void clear() {
        for (int i = 0; i < NUMCELLS; i++)
            currentState[i] = 0;

        generations = 0;
    }

    /**
     *        Loads the cell array with a random state, with approximately 25%
     *        of the cells alive.
     */
    private void loadRandomState() {
        patternName = "Random";

        for (int i = 0; i < NUMCELLS; i++)
            currentState[i] = (rand.nextInt() > 0x40000000) ? (byte)1 : (byte)0;

        generations = 0;
    }

    /**
     *        Handle commands.
     *        Currently, the only command enabled is "Exit" - this just
     *        destroys the MIDlet immediately.
     */
    public void commandAction(Command cmd, Displayable disp) {
        if (cmd == exitCommand) {
            try {
                destroyApp(false);
                notifyDestroyed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *        Handles key presses.
     *         This is called back from the inner Canvas class, and implements
     *        the behavior of the various keys outlined in the class summary.
     */
    public void keyPressed(int keyCode) {
        switch (keyCode) {
        // 0,1,2,3 are speed keys.

        // 0: Pause - set a very long delay until the next update
        case Canvas.KEY_NUM0:
            delay = 1000000;
            delayCount = 1000000;

            break;

        // 1: Full speed - set no delay until next update
        case Canvas.KEY_NUM1:
            delay = 0;
            delayCount = 0;

            break;

        // 2: Speed up - if not already at full speed, decrease delay
        case Canvas.KEY_NUM2:

            if (delay > 0) {
                delay--;
            }

            delayCount = 0;

            break;

        // 3: Slow down - if not already at minimum speed, increase delay
        case Canvas.KEY_NUM3:

            if (delay < 20) {
                delay++;
            }

            delayCount = 0;

            break;

        // 4,5 are pattern keys

        // 4: Select and load previous pattern in library. If at the
        // start of the library, or using a random pattern, will start
        // again at the last pattern.
        case Canvas.KEY_NUM4:
            pattern--;

            if (pattern < 0) {
                pattern = patternLibrary.length - 1;
            }

            patternName = patternLibrary[pattern][0];
            loadStateFromString(patternLibrary[pattern][1]);

            break;

        // 5: Select and load next pattern in library. If at the
        // end of the library, or using a random pattern, will start
        // again at the first pattern.
        case Canvas.KEY_NUM5:
            pattern++;

            if (pattern >= patternLibrary.length) {
                pattern = 0;
            }

            patternName = patternLibrary[pattern][0];
            loadStateFromString(patternLibrary[pattern][1]);

            break;

        // *: Loads a random state
        case Canvas.KEY_STAR:
            loadRandomState();

            break;
        }
    }

    /**
    * Inner TimerTask-derived class for refreshing the view.
    */
    private class RefreshTask extends TimerTask {
        public void run() {
            // Get the canvas to repaint itself.
            myCanvas.repaint();
        }
    }

    /**
     * Inner Canvas-derived class for handling canvas events.
     */
    private class CallbackCanvas extends Canvas {
        Life3D mymidlet;

        /**
         * Construct a new canvas
         */
        CallbackCanvas(Life3D midlet) {
            mymidlet = midlet;
        }

        /**
         * Initialize self.
         */
        void init() {
        }

        /**
         * Cleanup and destroy.
         */
        void destroy() {
        }

        /**
         * Ask mymidlet to paint itself
         */
        protected void paint(Graphics g) {
            mymidlet.paint(g);
        }

        protected void keyPressed(int keyCode) {
            mymidlet.keyPressed(keyCode);
        }
    }
}
