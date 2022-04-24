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

import com.nokia.sm.net.ItemList;
import com.nokia.sm.net.ServerComm;
import com.nokia.sm.net.SnapEventListener;
import samples.ui.*;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Image;
import java.util.Random;
import java.util.Vector;

/**
 * Main manager class for MazeRacer sample game functionality.
 * <p>
 * Primary responsiblities include: management of SNAP session
 * and all communication with SNAP servers, including sending 
 * events and intercepting results and errors; management of 
 * the various screens in the app GUI; and storage and management
 * of all screen-independent user and game data.
 */
public class Community implements AsyncCommandListener, SnapEventListener, ButtonListListener, EventListener, Runnable {
    public static Community instance;
    public static final String CHALLENGE_PREFIX = "+++ ChAlLeNgE +++: ";
    public static final String NON_CRITICAL = "non-critical";
    static final int    MAX_NEW_ACCOUNTS_PER_DAY= 5;
    
    private static final Random rnd = new Random();

    static public final Image FADING_BACKGROUND  = ResourceManager.getImage("/fading_background.png");
    static final Image OVERLAY_BACKGROUND = ResourceManager.getImage("/overlay_background.png");
    static final Image SELECTED_SECTION   = ResourceManager.getImage("/selected_section.png");
    static final Image SPLASH_IMG         = ResourceManager.getImage("/device_splash.png");
    
    static final String About_txt 		  = "/about.txt";
    static String Motd_txt 		  			= "/motd.txt";
    static final String Game_Help_txt 	  = "/game_help.txt";
    static final String SM_Help_txt 	  = "/sm_help.txt";
    static final String Operator_Help_txt = "/operator_help.txt";

    static final String SPLASH              = "Splash";
    static final String GAME                = "Game";
    static final String MOTD                = "Message of the day";
    static final String WELCOME				= "Welcome";
    static final String GO_ONLINE           = "go online";
    static final String SINGLE_USER         = "single user game";
    static final String ABOUT				= "about";
    static final String CREATE_ACCOUNT      = "create account";
    static final String DIALOG              = "Dialog";
    static final String CHAT                = "chat messages";
    static final String FRIENDS             = "Friends";
    static final String HOME                = "Home";
    static final String PLAY                = "Play";
    static final String PENDING_GAMESTART   = "Waiting for player";
    static final String RANKING             = "rankings";
    static final String TOP_10              = "Top 10";
    static final String MY_RANKINGS         = "My Rankings";
    static final String STATS               = "Stats";
    static  String HELP                		= "help";
    static final String SM_HELP             = "SNAP Mobile Help";
    static final String GAME_HELP           = "Game Help";
    static final String OPERATOR_HELP       = "Operator Help";
    static final String EMAIL_DOB           = "E-mail & Birthdate";
    static final String USER_PASS           = "Username & Password";
    static final String TERMS               = "Terms";
    static final String SUBMIT_SCORES       = "SubmitScores";

    static final String SELECT              = "select";
    static final String SEND                = "send";
    static final String CANCEL              = "cancel";
    static final String BACK                = "back";
    static final String MAZEEXIT          	= "mazeexit";
    static final String EXIT                = "exit";
    static final String REALLY_EXIT         = "exit!";
    static final String LOGIN               = "login";
    static final String LOGOUT              = "log out";
    static final String REALLY_LOGOUT       = "log out!";
    static final String NEXT                = "next";
    static final String OK                  = "ok";
    static final String YES                 = "yes";
    static final String NO                  = "no";

    static final String RMS_USERNAME        = "userName";
    static final String RMS_PASSWORD        = "passWord";
//    static final String RMS_SAVE_PASSWORD   = "savePass";

    //  GUI data
    private ViewCanvas canvas;
    private MazeRacer mazeRacer;
    private Vector cmdList;
    private BuddyList buddyList;
    private String[] statList;
    private boolean inGame;

    //Snap specific data
    private String webSessionID;
    private String impsSessionID;
    private String snapSessionID;
    private String snapUserID;
    private ServerComm comm;
    private Vector viewList;
    private View motd;
    private MainApp main;
    private String lastError;
    private int lastErrorSeverity;
    private Integer gcid;
    private String operatorId;
    private boolean done;
    private boolean waitingForGame;
    private AsyncCommandListener asyncListener=null;
    private String asyncCommand=null;
    private boolean		inGameReg			= false;
    private boolean		saveLogin			= false;

    // User login/registration info -- save here for later retrieval
    // if login/registration screens are revisited after errors take the
    // user back to the main menu.  (Upon return to the main menu, the
    // registration screens are destroyed, and recreated from scratch
    // if revisited -- so previously entered data must be saved here
    // if it is to be retained.)
    public String       username            = "";
    public String       password            = "";
    public String       password2           = "";
    public String       dateOfBirth         = "";
    public String       emailAddress        = "";
    public int          minAge              = 13;

    /** Returns <code>com.nokia.sm.net.ServerComm</code> instance. */
    public ServerComm getServerComm()       { return comm; }


    protected int       getMinAge()             { return minAge; }
    protected MainApp   getMainApp()            { return main; }

    /**
     * Sets user's SNAP session IDs.
     *
     * @param returnValues an ItemList returned from a SNAP login command.
     */
    public void setLoginInfo(ItemList returnValues)
    {
        if (returnValues == null) return;
        setLoginInfo(
        		returnValues.getString("webSessionID"),
        		returnValues.getString("impsSessionID"),
        		returnValues.getString("snapSessionID"),
        		returnValues.getString("snapUserID")
        	);
    }

    /**
     * Sets user's SNAP session IDs.
     *
     * @param webSessionID
     * @param impsSessionID
     * @param snapSessionID
     * @param snapUserID
     */
    public void setLoginInfo(String webSessionID, String impsSessionID, String snapSessionID, String snapUserID) {
        this.webSessionID = webSessionID;
        this.impsSessionID = impsSessionID;
        this.snapSessionID = snapSessionID;
        this.snapUserID = snapUserID;
    }


    /*
     * =======================================================================
     *  Instance lifecycle methods: static initializer, constructor, pause
     *  resume and exit methods.
     * =======================================================================
     */

    /**
     * Calls static initializers on View and SnapLoginView.
     */

    static void initialize() {
        CommunityView.initialize();
        View.initialize();
        BuddyView.initialize();
        ButtonListView.initialize();
        Chat.initialize();
        Dialog.initialize();
        LoginView.initialize();
        RankingView.initialize();
        TextView.initialize();
    }

    /** Loads SNAP properties from .jad file */
    protected void loadSnapProperties()
    {
        try { minAge 	= Integer.parseInt( main.getProperty("SNAP-Mobile-MinAge")); } catch (Exception e) {}
        String reg 		= main.getProperty("SNAP-Mobile-InGameReg");
        String save 	= main.getProperty("SNAP-Mobile-SaveLogin");
        if (reg  != null) inGameReg = reg.equals ("true");
        if (save != null) saveLogin = save.equals("true");	
    }

    /**
     * Constructor.  Loads settings from .jad file.  Performs network
     * connectivity check if connectivity has not been established in the
     * past.  Then, instantiates and displays LoginView screen.
     *
     * @param main Reference to caller's code, via the <code>MainApp</code>
     *  interface.  This reference is used to return control back to the
     *  calling code once login and/or registration are complete, by means
     *  of the handleEvent() method.
     */
    public Community(MainApp main) {

        this.main = main;

        cmdList = new Vector();
        viewList = new Vector();
        buddyList = new BuddyList();

        gcid = new Integer( 49721);
        username = "";

        View splash = getView(SPLASH);
        splash.setActive(true);

        canvas = new ViewCanvas(splash);
        Display.getDisplay(main.getMIDlet()).setCurrent(canvas);
        canvas.waitForResize();

        mazeRacer = new MazeRacer(this);
        inGame = false;
        waitingForGame = false;
        lastError = null;
        lastErrorSeverity = -1;

        initialize();

        // Note: set to true in order to force initialization of the
        // network connection early, in the ServerComm constructor.
        boolean initializeNetwork = false;

        System.out.println("server url:" + main.getProperty("SNAP-Mobile-Host"));
        System.out.println("server port:" + Integer.parseInt(main.getProperty("SNAP-Mobile-Port")));
        System.out.println("protocol:" + main.getProperty("SNAP-Mobile-Protocol"));
        System.out.println("*** Note:  Set 'Small-Maze' parameter in .jad file to 'true' to play with a smaller (faster) maze.");
        try {
	        comm = new ServerComm(
	                main.getProperty("SNAP-Mobile-Host"),
	                Integer.parseInt(main.getProperty("SNAP-Mobile-Port")),
	                main.getProperty("SNAP-Mobile-Protocol"),
	                Integer.parseInt(main.getProperty("SNAP-Mobile-SKU")),
	                initializeNetwork
	        	);
            comm.addSnapEventListener(this);
//            switchToView(WELCOME);
    	} catch (RuntimeException r) {
            // Check for SecurityExceptions (see if user
            // pressed "no" to the GPRS permissions dialog popup.)
            // If so, tell them to exit app and restart.  Note:
            // ServerComm takes the summary of CheckedExceptions,
            // then rewraps them as RuntimeExceptions before
            // rethrowing, so we need to check for the name of the
            // original exception inside the body of the Runtime
            // Exception.
    		if (r.toString().toLowerCase().indexOf("security")!=-1) {
        		showError(
        				"Unable to connect to network.  You must " +
        				"press 'YES' in the GPRS/Airtime Permissions " +
        				"dialog.  Please exit app and restart.",
        				Community.REALLY_EXIT, Dialog.ALERT
        				);
    		}
    	}

        operatorId = getProperty("SNAP-Mobile-OperatorID");

        int count = Integer.parseInt(getProperty("Stat-Count"));
        statList = new String[count];

        for (int i=0; i<statList.length; i++) {
            statList[i] = getProperty("Stat-" + (i + 1));
        }

        Thread thread = new Thread(this);
        thread.start();

        instance = this;
    }

    public void pause()
    {
    	mazeRacer.pause();
    }

    public void resume()
    {
    	mazeRacer.resume();
    }

    /**
     * Saves username/password if login saving is enabled, and is checked
     * "on" by the user. Then, returns control back to calling code via the
     * <code>handleEvent()</code> method of the
     * <code>com.nokia.sm.miniui.EventHandler</code> interface, which
     * the calling code must implement.
     */
    public void exit() {
        System.out.println("EXITING MIDLET");
        synchronized (this) {
            cmdList.removeAllElements();
            if (isLoggedIn()) {
                ItemList itemList = new ItemList();
                itemList.setItem("cmd", "unifiedLogout");
                itemList.setItem("listener", this);
                executeCmd(itemList);
            }
            comm.removeSnapEventListener(this);
            comm.stop();
            done = true;
        }
        main.exit();
    }

    /*
     * =======================================================================
     *  View management and GUI wrangling responsibilities.
     * =======================================================================
     */

    /**
     * Returns active View.
     *
     * @return View
     */
    public View getView()
    {
    	return canvas.getView();
    }

    /**
     * Returns a particular View by name.  View is created if it does not
     * already exist, or if View caching is turned off.  Requesting View
     * "back" will pop the current View off the View stack, destroy it,
     * and return the previous View.  Requesting View "exit" will cause
     * SnapLogin to exit.
     *
     * @param name Name of the view to return
     */
    View getView(String name) {
        String url;
        View view;
        int i;

        view = null;

        synchronized (viewList) {
            if (viewList.size() > 1 && name.equals(BACK)) {
                viewList.removeElementAt(viewList.size() - 1);
                view = (View)viewList.elementAt(viewList.size() - 1);
                return view;
            }
        }

        view = findView(name);

        if (view != null) return view;

        // Interpreted as an action, instead of an actual
        //  view, like requests for Community.BACK.
        // Like BACK, but potentially jumps back two views
        // down the view stack instead of just one.  First
        // BACK returns from the "Really exit?" or "Game
        // Over" dialog to the maze game, second BACK returns
        // from the maze game to whatever screen the user was
        // on before they started.
        else if (name.equals(MAZEEXIT)) {
        	mazeRacer.gameOver( false);
        	view = getView(BACK);
        	// Only jump back two views if the view we were
        	// on when a MAZEEXIT dialog popped up was the
        	// MazeRacer game itself.  (This avoids edge-case
        	// problems when we accidentally receive this event
        	// after we've left the game view.)
        	if (view==mazeRacer.getView()) {
        		view = getView(BACK);
        	}
        }

        // Username/Password screen (part of new account creation)
        else if (name.equals(USER_PASS))
            view = new UserPassView  ( this, USER_PASS);

        // Email/Date-of-Birth screen (part of new account creation)
        else if (name.equals(EMAIL_DOB))
            view = new EmailDobView( this, EMAIL_DOB);

        // Terms and conditions screen (part of new account creation)
        else if (name.equals(TERMS)) {
            TextBox textBox = new TextBox();
            textBox.setText(  TermsView.TERMS_AND_CONDITIONS);
            textBox.setBackground(0x00ffffff);
            textBox.setForeground(0x00c0c0c0);
            textBox.setFocusable(false);
            textBox.setDimension(canvas.getWidth() - 10, 12 * textBox.getFont().getHeight() + TextBox.WIDTH_OFFSET);
            view = new TermsView( this, TERMS, textBox);
        }

        // "Are you sure" logout dialog
        else if (name.equals(LOGOUT)) {
        	showDialog( "Log Out", "Are you sure you want to log out?",
        			null, Community.REALLY_LOGOUT, Dialog.YES_NO
        			);
        }

        // Action, instead of a View.  Logs the user out.
        else if (name.equals(REALLY_LOGOUT)) {
            view = null;
            logout();
        }

        // "Are you sure" app exit dialog
        else if (name.equals(EXIT)) {
        	showDialog( "Exit", "Are you sure you want to exit?",
        			null, Community.REALLY_EXIT, Dialog.YES_NO
        			);
        }

        // Action, instead of a view.  Exits the MIDlet.
        else if (name.equals(REALLY_EXIT)) {
            view = null;
            exit();
        }

        // Splash screen
        else if (name.equals(SPLASH)) {
            view = new SplashView(
                this,
                SPLASH,
                null,
                SPLASH_IMG,
                WELCOME,
                3000
            );
        }

        // Message of the Day screen
        if (name.equals(MOTD)) {
        	view = motd = new TextView(
                        this,
                        MOTD,
                        null,
                        this,
                        Community.HOME,
                        Community.OK,
                        null,
                        Motd_txt,
                        true
                    );
        }

        // Offline main menu, which gives the option of going online,
        // reading help/info, or playing single-player.
        else if (name.equals(Community.WELCOME)) {
            view = new ButtonListView(
                this,
                Community.WELCOME,
                this,
                null,
                new String[] {Community.GO_ONLINE, Community.SINGLE_USER, Community.ABOUT, Community.HELP},
                Community.SELECT,
                Community.EXIT
            );
        }

        // Online main menu (play, friends, rankings, help)
        else if (name.equals(HOME)) {
            view = new ButtonListView(
                this,
                HOME,
                this,
                buddyList,
                new String[] {"play", "rankings", "friends", "help"},
                Community.SELECT,
                Community.LOGOUT
            );

        }

        // Play menu (challenge or quick match)
        else if (name.equals(PLAY)) {
            view = new ButtonListView(
                this,
                PLAY,
                this,
                buddyList,
                new String[] {"quick match", "challenge"},
                Community.SELECT,
                Community.BACK
            );
        }

        // Waiting for quickmatch or challenge to complete, dialog
        else if (name.equals(PENDING_GAMESTART)) {
            view = new TextView(
                this,
                PENDING_GAMESTART,
                buddyList,
                this,
                null,
                null,
                Community.CANCEL,
                "Press 'cancel' to abort",
                false
            );
        }

        // Rankings menu (stats vs. my rankings)
        else if (name.equals(RANKING)) {
            view = new ButtonListView(
                this,
                RANKING,
                this,
                buddyList,
                new String[] {Community.STATS, Community.MY_RANKINGS},
                Community.SELECT,
                Community.BACK
            );
        }

        else if (name.equals(TOP_10)) {
            SectionNavigator sn;

            sn = new SectionNavigator(
                SELECTED_SECTION,
                new String[] {}
            );
            sn.setFont(LoginView.TEXTFIELD_FONT);
            sn.setForeground(0x00c0c0c0);

            view = new RankingView(
                this,
                TOP_10,
                buddyList,
                sn,
                new String[] {"#", "Username", "Score"}
            );
        }

        // Stats menu (lists game rankings categories)
        else if (name.equals(STATS)) {
            view = new ButtonListView(
                this,
                STATS,
                this,
                buddyList,
                statList,
                Community.SELECT,
                Community.BACK
            );
        }

        // My rankings view
        else if (name.equals(MY_RANKINGS)) {
            SectionNavigator sn;

            sn = new SectionNavigator(
                SELECTED_SECTION,
                new String[] {}
            );
            sn.setFont(LoginView.TEXTFIELD_FONT);
            sn.setForeground(0x00c0c0c0);

            view = new RankingView(
                this,
                MY_RANKINGS,
                buddyList,
                sn,
                new String[] {"Rnk", "Stat", "Score"}
            );
        }

        // Main help menu
        else if (name.equals(HELP)) {
            view = new ButtonListView(
                this,
                Community.HELP,
                this,
                buddyList,
                new String[] {"snap mobile", "game", "operator"},
                Community.SELECT,
                Community.BACK
            );
        }

        // SNAP Mobile help screen
        else if (name.equals(SM_HELP)) {
            view = new TextView(
                this,
                SM_HELP,
                buddyList,
                this,
                null,
                null,
                Community.BACK,
                SM_Help_txt,
                true
            );
        }

        // Game help screen
        else if (name.equals(GAME_HELP)) {
            view = new TextView(
                this,
                GAME_HELP,
                buddyList,
                this,
                null,
                null,
                Community.BACK,
                Game_Help_txt,
                true
            );
        }

        // Operator help screen
        else if (name.equals(OPERATOR_HELP)) {
            view = new TextView(
                this,
                OPERATOR_HELP,
                buddyList,
                this,
                null,
                null,
                Community.BACK,
                Operator_Help_txt,
                true
            );
        }

        // About info screen
        else if (name.equals(ABOUT)) {
            view = new TextView(
                this,
                ABOUT,
                null,
                this,
                null,
                null,
                Community.BACK,
                About_txt,
                true
            );
        }

        // Login (and/or create new account) screen
        else if (name.equals(LOGIN)) {
            view = new LoginView(
                this,
                LOGIN,
                this
            );
        }

        // Chat screen
        else if (name.equals(CHAT)) {
            view = new Chat(
                this,
                CHAT,
                buddyList
            );
        }

        // Friends list screen
        else if (name.equals(FRIENDS)) {
            view = new BuddyView(
                this,
                FRIENDS,
                buddyList
                );
        }

        return view;
    }

    /**
     * Tells GUI to instantiate (if necessary) and display a
     * particular named view.  "back" and "exit" have special
     * behavior (see <code>getView</code>).
     *
     * @param name Name of View to display
     */

    public void switchToView(String name) {
//        System.out.println("Community.switchToView(), name = " + name);
        switchToView(getView(name), !name.equals(BACK));
    }

    /**
     * Tells GUI to instantiate (if necessary) and display a
     * particular named view.  "back" and "exit" have special
     * behavior (see <code>getView</code>).
     *
     * @param view View to display
     * @param cache Caching flag; if <code>false</code>, current
     *  view will not be preserved on the View stack (disabling
     *  "back" behavior for that View)
     */
    public void switchToView(View view, boolean cache) {
        View prev;

        if (view == null) {
//            System.out.println("NULL view !");
            return;
        }

        prev = canvas.getView();
        prev.setActive(false);
        view.setActive(true);
        canvas.setView(view);

        if (cache) {
            synchronized (viewList) {
                // Removes loops in the "BACK" View stack. That is, looks back
                // down the stack for the last entry of the view being added.
                // If found, remove all Views above it in the stack.
                while (viewList.contains( view)) {
                    viewList.removeElementAt( viewList.size()-1);
                }
                viewList.addElement(view);
            }
        }
    }

    /**
     * Searches for a particular named View in the View stack (cache).
     *
     * @param name Name of View to find
     * @return Returns named View, if found
     */
    public View findView(String name) {
        View view;
        int i;

        synchronized (viewList) {
            for (i=viewList.size()-1; i>=0; i--) {
                view = (View)viewList.elementAt(i);
                if (name.equals(view.getName())) return view;
            }
        }

        return null;
    }

    /**
     * Removes a particular named View from the View stack.
     * @param name Name of View to remove.
     */
    public void removeView(String name) {
        synchronized (viewList) {
            View view = findView(name);
            if (view != null) viewList.removeElement(view);
        }
    }

    /**
     * Causes currently visible canvas to repaint itself.
     *
     */
    public void repaint()
    {
        getCanvas().repaint();
    }

    /**
     * Returns currently visible canvas.
     */
    public Canvas getCanvas() {
        return canvas;
    }


    /*
     * =======================================================================
     *  Dialog convenience methods
     * =======================================================================
     */


    /**
     * Shows an error dialog w/ a string message and one "OK"
     * softkey in the left corner of the screen.  "OK" just
     * goes back to the previous screen.
     *
     * @param msg text of error message to display
     */
    public void showError(String msg) {
        showError( msg, null, Dialog.ALERT);
    }

    /**
     * Shows error message passed in, in full-screen Dialog.
     *
     * @param msg The error message to be displayed
     * @param target If not <code>null</code>, error dialog will
     *   go to this View upon exiting.  Otherwise, will typically
     *   go "BACK"
     * @param type type of dialog.  See <code>Dialog<code> class.
     */
    public void showError(String msg, String target, int type)
    {
        //System.out.println("Community.showError(), msg = " + msg);
    	String name = "Error";
    	switch (type) {
    	case Dialog.ALERT:
    		name = "Alert";
    		break;
    	case Dialog.ALERT_FATAL:
    	case Dialog.ALERT_LOGOUT:
    		name = "Logout Error";
            msg += " -- You must log back in from the main menu.";
    		break;
    	}
        showDialog( name, msg, null, target, type);
    }


    /**
     * Shows message passed in, in a full-screen Dialog.
     *
     * @param title
     * @param contents Must be either a String or a Component
     * @param arg Argument passed to listener when activated via SELECT
     * @param targetView If not null, View to go to upon exiting
     * @param type Type of dialog (see Dialog class for types).
     */
    public void showDialog(String title, Object contents, Object arg, String targetView, int type) {

        String rightSoft = null;
        String leftSoft = null;

        // Contents to be displayed - must be either a String or a Component
        Component comp = null;
        // If contents is a Component (usually a text input component)
        // then display it directly.
        if (contents instanceof Component) {
        	comp = (Component)contents;
        // If contents is a String, then put the String in a TextBox and
        // display that.
        } else {
	        TextBox box = new TextBox();
	        if (contents instanceof String) {
	        	box.setText( (String)contents);
	        } else {
	        	box.setText(
	        			"Error! Unrecognized contents: " +
	        			contents.toString()
	        			);
	        }
	        box.setBackground(0x00ffffff);
	        box.setForeground(0x00c0c0c0);
	        box.setFocusable(false);
	        box.setDimension( canvas.getWidth() - 10, 12 * box.getFont().getHeight() + TextBox.WIDTH_OFFSET);
	        comp = box;
        }

        // Set left/right softkeys based on type
        switch (type) {
        case Dialog.YES_NO:
        	leftSoft = Community.YES;
            rightSoft = Community.NO;
            break;
        case Dialog.DATA_ENTRY:
        case Dialog.OK_CANCEL:
        	leftSoft = Community.OK;
        	rightSoft = Community.CANCEL;
        	break;
        // All other alerts have only "OK" on left softkey
        default:
        	leftSoft = Community.OK;
            break;
        }

        Dialog dialog = new Dialog(
            this,
            title,
            this,
            leftSoft,
            rightSoft,
            comp,
            arg,
            type,
            targetView
        );

        switchToView(dialog, true);
    }

    /*
    public void showDebug(String msg) {
        TextView view = new TextView(
                this,
                "Debug",
                null,
                this,
                Community.BACK,
                Community.OK,
                null,
                msg,
                true
        );
        switchToView( view, true );
    }
    */

/*
     * =======================================================================
     *  Event handling - managing input from various Views
     * =======================================================================
     */

    /**
     * Callback for button list views
     *
     * @param view Name of View
     * @param button Name of Button
     */
    public void buttonPressed(String view, String button) {
    	// Welcome screen (offline main menu) buttons
        if (view.equals(Community.WELCOME)) {
            if (button.equals( Community.SINGLE_USER)) {
                startGame(true);
            }

            else if (button.equals( Community.GO_ONLINE)) {
                switchToView(LOGIN);
            }
            else if (button.equals( Community.ABOUT)) {
            	switchToView( ABOUT);
            }
            else if (button.equals( Community.HELP)) {
            	switchToView( HELP);
            }
        }

        // Login screen buttons
        else if (view.equals(LOGIN)) {
        	 if (button.equals(Community.LOGIN)) {
        		 switchToView(LOGIN);
        	 }

        	 else if (button.equals(Community.CREATE_ACCOUNT)) {
        		 switchToView(CREATE_ACCOUNT);
        	 }
        }

        // Rankings screen buttons (stats vs. my rankings)
        else if (view.equals(RANKING)) {
            if (button.equals( Community.STATS)) {
                switchToView(STATS);
            }

            else if (button.equals( Community.MY_RANKINGS)) {
                switchToView(MY_RANKINGS);
            }
        }

        // Play screen buttons (challenge vs. quick match)
        else if (view.equals(PLAY)) {
            if (button.equals("quick match")) {

            	handleQuickMatch();

                View next = getView(PENDING_GAMESTART);
                next.setWaiting(true);
                switchToView(next, true);
            }

            else if (button.equals("challenge")) {
                switchToView(FRIENDS);
            }
        }

        // Help screen buttons
        else if (view.equals(HELP)) {
            if (button.equals("snap mobile")) {
                switchToView(SM_HELP);
            }

            else if (button.equals("game")) {
                switchToView(GAME_HELP);
            }

            else if (button.equals("operator")) {
                switchToView(OPERATOR_HELP);
            }
        }

        // Stats buttons
        else if (view.equals(STATS)) {
            RankingView next = (RankingView)getView(TOP_10);
            next.setName(button);
            switchToView(next, true);
        }

        // Online main menu buttons
        else if (view.equals(HOME)) {
            if (button.equals("play")) {
                switchToView(PLAY);
            }

            else if (button.equals("rankings")) {
                switchToView(RANKING);
            }

            else if (button.equals("friends")) {
                switchToView(FRIENDS);
            }

            else if (button.equals("help")) {
                switchToView(HELP);
            }
        }
    }

    /*
     * Event handler implemented from samples.ui.EventListener interface.
     *
     * @param e The event
     */
    public boolean handleEvent(Event e) {
        //System.out.println("Community.handleEvent(), source = " + e.getSource() + ", value = " + e.getValue());
        View view = (View)e.getSource();
        String action = (String)e.getValue();
        ItemList il;

        if (e.getSource() instanceof LoginView) {
        	switchToView(HOME);
            return true;
        }

        // Splash screen
        else if (e.getSource() instanceof SplashView) {
            switchToView(Community.WELCOME);
            return true;
        }

        // Pending game start screen
        else if (e.getSource() instanceof TextView) {
            if (view.getName().equals(PENDING_GAMESTART)) {
                if (action.equals(Community.CANCEL)) {
                    il = new ItemList();
                    il.setItem("cmd", "gameStartCancel");
                    il.setItem("listener", this);
                    executeCmd(il);
                    switchToView(Community.BACK);
                    return true;
                }
            }
        }

        // Dialogs
        else if (e.getSource() instanceof Dialog) {
            Dialog dialog = (Dialog)e.getSource();
            String name = dialog.getName();

            il = new ItemList();

            if (dialog.getType()==Dialog.ALERT_FATAL) {
                setLoginInfo(null, null, null, null);
                if (mazeRacer.isPlaying()) mazeRacer.gameOver(false);
                switchToView(Community.WELCOME);
                return true;
            }

            else if (dialog.getType()==Dialog.ALERT_LOGOUT) {
                if (mazeRacer.isPlaying()) mazeRacer.gameOver(false);
                logout();
                return true;
            }

            else if (name.equals("Alert") || name.equals("Error") || name.equals("Note")) {
                //System.out.println("got dialog event, switching to back");
                switchToView(Community.BACK);
                return true;
            }

            else if (name.equals("Friend Request")) {
                String from = (String)dialog.getArg();
                if (action.equals(Community.YES)) {

                    buddyList.add(new Buddy(from, Buddy.ONLINE_AVAILABLE));
                    //System.out.println("approved buddy request. from = " + from);
                    //System.out.println("buddyList.get(from) = " + buddyList.get(from));
                }

                il.setItem("cmd", action.equals(Community.YES)
                    ? "acceptBuddyRequest"
                    : "declineBuddyRequest");
                il.setItem("name", from);
                il.setItem("listener", this);

                executeCmd(il);

                switchToView(Community.BACK);
                return true;
            }

            else if (name.equals("Add Friend")) {
                if (action.equals(Community.OK)) {
                	addFriend( ((TextField)dialog.getComponent()).getText());
                    return true;
                }
            }

            else if (name.equals("Remove Friend")) {
                if (action.equals(Community.YES)) {
                    String buddy = (String)dialog.getArg();
                    il.setItem("cmd", "removeBuddy");
                    il.setItem("name", buddy);
                    il.setItem("listener", this);
                    executeCmd(il);
                    buddyList.remove(buddy);
                    switchToView( Community.BACK);
                    return true;
                }
            }

            // user wants to accept challenge
            else if (name.equals("Challenge")) {
                if (action.equals(Community.YES)) {
                	switchToView( Community.BACK);
                    handleChallenge(null, (String)dialog.getArg(), 0);
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * =======================================================================
     *  Helper methods for managing SNAP data, state, and command execution.
     * =======================================================================
     */


    /**
     * Validates friend name and if valid submits request to
     * add a friend to the friends list.
     *
     * @param name
     */
    public void addFriend( String name) {
//    	System.out.println( "addFriend(): " + name);
        if (name == null || name.equals("") || name.length()<4) {
        	showError("You must enter a friend name of at least 4 letters.");
        } else {
            ItemList il = new ItemList();
        	il.setItem("cmd", "requestBuddy");
            il.setItem("name", name);
            il.setItem("message", "Be my buddy?");
            il.setItem("listener", this);
            il.setItem("asyncListener", this);
            executeCmd(il);
            switchToView(Community.BACK);
        }
    }

    /**
     * Get the last SNAP error intercepted via the SnapEventListener
     * interface's processServerError() method (implemented in this
     * class).
     *
     * @return Error
     */
    public String getLastError() {
        synchronized (this) {
            String error = lastError;
            lastError = null;
            return error;
        }
    }

    /**
     * Get the last SNAP error severity level intercepted via the
     * SnapEventListener interface's processServerError() method
     * (implemented in this class).
     *
     * @return Severity level
     */
    public int getLastErrorSeverity() {
        synchronized (this) {
            int error = lastErrorSeverity;
            lastErrorSeverity = -1;
            return error;
        }
    }

    /**
     * Invent a random gameroom name.
     *
     * @return Name
     */
    public String getRandomGameRoomName() {
        StringBuffer buf = new StringBuffer();

        for (int i=0; i<8; i++) {
            buf.append((char)('a' + Math.abs(rnd.nextInt() % 26)));
        }


        return buf.toString();
    }

    /**
     * Sets buddy list.
     *
     * @param buddies
     */
    public void setBuddyList( Vector buddies)
    {
		buddyList.set( buddies);
    }

    /**
     * Sets MOTD messages
     * @param il ItemList returned from either getMOTD() or
     *  extendedLogin() call.
     */
    public void setMOTD( ItemList il)
    {
    	if (il==null) {
    		return;
    	}
    	Vector messages = il.getList( "messageList");
    	if (messages == null || messages.size()==0) {
    		System.out.println("Zero messages in message list!");
    		return;
    	}
    	System.out.println( messages.size() + " messages in MOTD");

    	String motdText = "";
    	for (int i=0; i<messages.size(); i++) {
    		ItemList il2 = (ItemList)messages.elementAt(i);
    		String message = il2.getString( "message");
    		System.out.println( "Message " + i + ": " + message);
    		motdText += message;
    		if ( !(motdText.endsWith(".") || motdText.endsWith("!") ||
    				motdText.endsWith("?") || motdText.endsWith(",")))
    		{
    			motdText += ".";
    		}
    		motdText += "\n";
    	}
        Motd_txt = motdText;
    }

    /**
     * Submits request for a random quickmatch pairing
     * to the SNAP servers.
     */
    public void handleQuickMatch()
    {
    	ItemList il = new ItemList();
        il.setItem("cmd", "randomStart");
        il.setItem("lobbyID", "Random");
        il.setItem("lobbyMaxUsers", new Integer(0)); // Note: 0 = infinite
        il.setItem("gameRoomMaxUser", new Integer(2));
        il.setItem("listener", this);

        executeCmd(il);
    }

    /**
     * Handle incoming game challenges from other users.
     * <p>
     * If "leader" is 1, then this player is initiating the challenge,
     * so a specially formatted Chat message will be sent to the
     * opponent, inviting them to a game.
     * <p>
     * If leader is "0", then we were challenged, and this method is
     * called in order to accept the challenge.
     * <p>
     * Both challenger and challenged call "gameStart" to intiate the
     * game.
     *
     * @param name Name of opponent in game challenge
     * @param gameRoom Name of gameroom to join to start game
     * @param leader "1" if we are the challenger, "0" if we are the
     *    challenged.
     */
    public void handleChallenge(String name, String gameRoom, int leader) {
            ItemList il;

            View view = getView(PENDING_GAMESTART);
            view.setWaiting(true);
            switchToView(view, true);

            // If we are the challenger, send a challenge message
            if (leader == 1) {
                il = new ItemList();
                il.setItem("cmd", "sendBuddyMessage");
                il.setItem("name", name);
                il.setItem("msg", CHALLENGE_PREFIX + gameRoom);
                executeCmd(il);
            }

            // In either case, send a gameStart command.
            // If challenger, and the opponent declines, our gameStart
            // request will terminate.
            // If challenged, then the challenger will have already
            // issued a gameStart command, so shortly after we issue
            // ours in response, the game should begin.
            il = new ItemList();
            il.setItem("cmd", "gameStart");
            il.setItem("lobbyID", "Challenge");
            il.setItem("lobbyMaxUsers", new Integer(0)); // Note: 0 = infinite
            il.setItem("gameRoomID", gameRoom);
            il.setItem("gameRoomMaxUsers", new Integer(2));
            il.setItem("mode", new Integer(1));
            il.setItem("leader", new Integer(leader));
            il.setItem("listener", this);

            executeCmd(il);
    }

    /**
     * Handles incoming friend Presence update callbacks from SNAP Servers.
     * Analyzes user state such as GCID, in-game flag, availability,
     * and matchmaking mode to decide whether the user is online and
     * available, online and unavailable, or offline.
     *
     * @param il ItemList containing buddy presence information
     */
    public void handlePresenceUpdate( ItemList il)
    {
        String avail = il.getString("availability");
        Integer gcid = null;
        try { gcid = (Integer)(il.getItem("gameClassID")); } catch (Exception ignore) {}
        int matchMode = 0;
        try { matchMode = il.getInteger("matchMode"); } catch (Exception ignore) {}
        boolean inGame = false;
        try { inGame = il.getBoolean("inGame"); } catch (Exception ignore) {}
        String name = clipName(il.getString("fromName"));

/*
        System.out.println("Got presence update------------");
        System.out.println("name = " + name);
        System.out.println("gcid = " + gcid);
        System.out.println("availability = " + avail);
        System.out.println("matchMode = " + matchMode);
        System.out.println("inGame = " + inGame);
*/

        // Offline
        if ( avail.equals("unavailable")) {
            buddyList.updatePresence( name, Buddy.OFFLINE, gcid);

        // Online, unavailable
        } else if( matchMode != 0 || inGame || avail.equals("away")) {
            buddyList.updatePresence( name, Buddy.ONLINE_UNAVAILABLE, gcid);

        // Online, available
        } else {
            buddyList.updatePresence( name, Buddy.ONLINE_AVAILABLE, gcid);
        }
    }

    /**
     * Convenience method to clip off auth domain information
     * from fully-qualified SNAP Mobile user names.
     *
     * @param name
     */
    private String clipName(String name) {
        int index = name.indexOf('@');

        if (index < 0) return name;
        return name.substring(0, index);
    }

    /**
     * Extracts opponent's name from return value ItemList of a
     * successful gameStart or randomStart request.
     *
     * @param il ItemList returned from successful gameStart or
     *    randomStart command
     * @return Opponent's name, or "Opponent" if no name found.
     */
    protected String getOpponentName( ItemList il)
    {
    	String name = "Opponent";
    	if (il == null) {
        	System.out.println("Community.getOpponentName(): Null Item List!");
    		return name;
    	}
    	Vector rivals = il.getList("rivalList");
    	if (rivals != null) {
	    	for (int i=0; i<rivals.size(); i++) {
	    		ItemList user = (ItemList)rivals.elementAt(i);
	    		String userName = user.getString("userName");
	    		if (userName != null && !userName.equals(username)) {
	    			return userName;
	    		}
	    	}
    	}
    	return name;
    }


    /**
     * Starts a MazeRacer game, and MazeRacerView takes over the GUI.
     *
     * @param singleUser
     */
    private void startGame(boolean singleUser) {
        mazeRacer.reset( singleUser);
        switchToView( mazeRacer.getView(), true);
    }

    /**
     * Logs the user out, then puts them back on the main menu screen.
     */
    public void logout() {
//        System.out.println("LOGGING OUT, RETURNING TO MAIN MENU");

    	// Set username to null and empty out buddy list.
        username     = null;
        password     = null;
        password2    = null;
        emailAddress = null;
        dateOfBirth  = null;
        buddyList.set( null);
        if (isLoggedIn()) {
            cmdList.removeAllElements();
            ItemList itemList = new ItemList();
            itemList.setItem("cmd", "unifiedLogout");
            itemList.setItem("listener", this);
            executeCmd(itemList);
            showDialog(
                    "Logging Out",
                    "Logging out... one moment please.",
                    null, Community.WELCOME,
                    Dialog.ALERT
                    );
        } else {
            switchToView( WELCOME);
        }
    }

    /** Returns <code>true</code> if user has successfully logged in. */
    public boolean isLoggedIn() {
        return webSessionID != null;
    }

    /**
     * Returns MIDlet property.
     * @param name
     */
    public String getProperty(String name) {
        return main.getProperty(name);
    }

    /**
     * Returns MazeRacer's game class ID.
     */
    public Integer getGCID() {
        return gcid;
    }

    /**
     * Sets user's username.
     * @param username
     */
    void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns user's username
     */
    String getUsername() {
        return username;
    }


    /*
     * =======================================================================
     *  SNAP communcation management, including the main event loop that submits
     *  SNAP commands, as well as the processServerError() and processEvents()
     *  callbacks used for receiving errors and data back from SNAP.
     * =======================================================================
     */


    /**
     * Adds a SNAP command to the command queue.
     *
     * @param cmd ItemList containing command name and parameters.
     */
    public void executeCmd(ItemList cmd) {
        synchronized (this) {
            if (!done) {
            	// If gameStartCancel command, execute now in its own thread.
            	// Otherwise add command to queue for processing in SNAP
            	// messaging thread (Community.run(), below).
            	if (!handleGameStartCancel( cmd)) {
	                cmdList.addElement(cmd);
	                notifyAll();
            	}
            } else {
            	System.out.println("Community.executeCmd():");
                System.out.println("Event loop finished (most likely fatally), cannot execute command!");
            }
        }
    }

    /**
     * gameStartCancel commands must be sent out in a parallel
     * thread from the one that sends the gameStart command,
     * since gameStart itself blocks for up to 30 or so seconds
     * (depending on SNAP Server defaults.)
     * <p>
     * gameStartCancel() is the only SNAP command that may be
     * sent out in parallel to any other SNAP commands [with
     * the exception of the retrieveAllEvents() command, which
     * is typically called behind the scenes by the SNAP
     * EventListener thread inside ServerComm.]  If any other
     * SNAP commands are executed in parallel in this fashion,
     * they will result in a "755 CommandInProgress" error.
     *
     * @param cmd ItemList containing command parameters
     *    (primarily, the command listener.)
     */
    protected boolean handleGameStartCancel( ItemList cmd)
    {
    	if ("gameStartCancel".equals(cmd.getString("cmd"))) {
    		AsyncCommandListener listener =
    			(AsyncCommandListener)cmd.getItem("listener");
    		Thread gscThread = new Thread() {
    			public void run() {
                	comm.gameStartCancel();
//                	System.out.println("Canceled game start!");
    			}
    		};
    		gscThread.start();
    		waitingForGame = false;
    		return true;
    	}
    	return false;
    }

    /**
     * Cancels "non-critical" game commands by removing them from the
     * outgoing SNAP command queue.  Non-critical commands are player
     * position updates and heartbeat notifications.  This method is
     * called when a game state transition message must go out (e.g.,
     * if a player has reached the end of the maze) -- typically it's
     * better to let this command supercede all queued up movement
     * commands and get out to the opponent immediately.
     */
    public synchronized void cancelNonCriticalCmds() {
        ItemList il;

        for (int i=0; i<cmdList.size(); i++) {
            il = (ItemList)cmdList.elementAt(i);

            if (il.getString(NON_CRITICAL) != null) {
                cmdList.removeElementAt(i--);
            }
        }
    }

    /**
     * SnapEventListener callback. All SNAP Mobile server errors are
     * received here, both synchronous and asynchronous errors.
     * This method saves server errors, to be checked for later in the
     * main event loop that submits commands.  Some Views handle their
     * own errors (via commandCompleted() implementations), but unhandled
     * errors eventually get displayed in generic error dialogs by
     * Community (in the run() method).
     * <p>
     * Note: only errors of type SEVERITY_NON_FATAL are handled by
     * individual Views.  All other severity levels are handled
     * generically, in this method.
     *
     * @param error Error number to process
     * @param msg Error Message
     * @param severity Severity of the Error Message
     */
    public void processServerError(int error, String msg, int severity) {
//        System.out.println("Community.processServerError(), error = " + error + ", msg = " + msg + ", severity = " + severity);

        synchronized (this) {
            lastError = msg + " " + error;
            lastErrorSeverity = severity;
        }

        //----------------------------------------------------------------
        // Fatal error -- SessionIDs already invalidated on server.
        // User must go back to main menu and re-log in.
        if (severity == SEVERITY_FATAL) {
            System.out.println("call showError() from processServerError() FATAL");
            setLoginInfo( null, null, null, null);
            if (mazeRacer.isPlaying()) mazeRacer.gameOver(false);
            synchronized (this) {
            	cmdList.removeAllElements();
            }
            showError( msg, Community.WELCOME, Dialog.ALERT_FATAL);
        }
        //----------------------------------------------------------------
        // Requires log out error.  App must send a logout command as
        // soon as possible go back to the main menu, and re-log in.
        else if (severity == SEVERITY_REQUIRES_LOGOUT) {
            System.out.println("call showError() from processServerError() REQUIRES_LOGOUT");
            if (mazeRacer.isPlaying()) mazeRacer.gameOver(false);
            synchronized (this) {
            	cmdList.removeAllElements();
            }
            if (error == 901) {
            	if (msg==null) msg = "You may be logged in twice! (901)";
            }
            if (isLoggedIn()) {
                showError( msg, Community.REALLY_LOGOUT, Dialog.ALERT_LOGOUT);
            } else {
                showError( msg, Community.WELCOME, Dialog.ALERT);
            }
        }
        //----------------------------------------------------------------
        // Transport error.  Last request assumed to fail to complete its
        // round-trip to the server, and should be safe to attempt again.
        // Session state on server stays valid, this is strictly a client-
        // side or network error.
        else if (severity == SEVERITY_TRANSPORT) {
            System.out.println("call showError() from processServerError() TRANSPORT");
            showError( msg + " - A connection error may have occurred. " +
            		"Please check your signal strength. ");
        }
        //----------------------------------------------------------------
        // Warning from underlying ServerComm networking code that there
        // may be some slowdown in network performance.  Network signal
        // may have been lost, or may soon.  No action required, but can
        // be smart to warn users to make sure they are in a full-signal
        // area before continuing.
        else if (severity == SEVERITY_TRANSPORT_WARNING) {
        	// ALL listener thread errors get reported as SEVERITY_TRANSPORT_WARNING
            System.out.println("call showError() from processServerError() TRANSPORT_WARNING");
            if (!getView().getName().equals("Network Warning")) {
	            showDialog("Network Warning", "" + msg + "(" + error + ") - Network currently is slow " +
	            		"or not responding", null, Community.BACK, Dialog.ALERT);
            }
        }
        //----------------------------------------------------------------
        // Error code specific to the last command sent, but a valid error
        // condition that has no effect on the user's server session.
        // Some nonfatal errors are handled in context-dependent ways by
        // individual Views, some are displayed in a "generic" dialog by
        // Community.
        else if (severity == SEVERITY_NON_FATAL) {
    //        System.out.println("call showError() from processServerError() NON_FATAL: " + lastError);
            // Give the current View (if it implements AsyncCommandListener,
            // and is registered as the listener for the current command) the
            // opportunity to show its own error-code-specific error screen in
            // the case of nonfatal errors.
            // If there is no listener, or if the listener does not handle the
            // error, then Community will pop up a generic error dialog in its
            // run() method where commands are dispatched to the SNAP servers.
            if (asyncListener != null) {
            	asyncListener.commandCompleted( asyncCommand, lastError, severity, new ItemList());
            }
        }
    }

    /**
     * SnapEventListener callback.  Handles all incoming asynchronous
     * events and messages from the SNAP Mobile servers, and deals
     * with them or delegates them to various parts of the app as
     * necessary.
     *
     * @param list Vector of ItemLists, each element is a separate
     *  SNAP message, event, or callback.
     */
    public void processEvents(Vector list) {
        ItemList il;
        String from, msg, gameRoom;
        byte[] data;
        String event = "";

        //System.out.println("Community.processEvents()");

        try {

        	for (int i=0; i<list.size(); i++) {
            //System.out.println("  " + i);
            il = (ItemList)list.elementAt(i);


            switch(il.getInteger("id")) {

            	// Buddy messages
                case ItemList.IMPS_IM_MESSAGE:
                    //System.out.println("got imps message");

                    from = clipName(il.getString("fromName"));
                    msg = il.getString("message");

                    // Challenge requests are just specially formatted
                    // buddy messages.
                    if (msg.startsWith(CHALLENGE_PREFIX)) {
                        gameRoom = msg.substring(CHALLENGE_PREFIX.length());
                        //System.out.println("got challenge message, game room is " + gameRoom);

                        showDialog( "Challenge",
                        		"Player '" + from + "' is challenging you to a game. Accept?",
                        		gameRoom, null, Dialog.YES_NO
                        		);
                    } else {
                        View view = findView(CHAT);

                        if (view instanceof Chat) {
                            Chat chat = (Chat)view;
                            chat.addBuddyMessage(from, msg);
                        } else {
                            buddyList.addChat(from, msg);
                        }
                    }
                    break;


                // Presence updates for friends (online/offline, availability)
                case ItemList.IMPS_IM_PRESENCE:
                    handlePresenceUpdate( il);
                    break;

                // Incoming request to be friends (from someone who
                // is not already a friend.)
                case ItemList.IMPS_BUDDY_REQ:
                    from = clipName(il.getString("fromUserName"));
                    msg = il.getString("message");
                    showDialog("Friend Request", "From " + from + ". Accept?", from, null, Dialog.YES_NO);
                    break;

                // Previous friend request of ours was either accepted
                // or rejected
                case ItemList.IMPS_BUDDY_ACC_REJ:
                    int accepted = il.getInteger("accepted");
                    from = clipName(il.getString("fromUserName"));

                    if (accepted != 0) buddyList.add(new Buddy(from, Buddy.ONLINE_AVAILABLE));
                    showDialog(
                            "Note", "'" + from + "' " +
                            (accepted != 0 ? "accepted" : "rejected") +
                            " buddy request.", null, null, Dialog.ALERT);
                    break;

                // We've been removed from one of our friends'
                // friends list.
                case ItemList.IMPS_BUDDY_REVOKED:
                    from = clipName(il.getString("fromUserName"));
                    showDialog("Note", "'" + from + "' is no longer your buddy.", null, null, Dialog.ALERT);
                    buddyList.remove(from);
                    break;

                // Incoming game packet from our opponent in the
                // MazeRacer game.
                case ItemList.GAME_PACKET:
                    from = clipName(il.getString("from"));
                    //System.out.println("  received game packet from " + from);
                    data = il.getByteArray("gameData");

                    mazeRacer.gameDataReceived(from, data);
                    break;
            }
        }
        } catch (Exception e) {
        	System.out.println("Exception in Community.handleEvents()!");
        	e.printStackTrace();
        }
    }

    /**
     * Called when ServerComm completes a call for which we were a listener.
     * (either successfully, or with errors from the servers.)
     *
     * @param cmd Name of command ("extendedLogin", etc.)
     * @param errorMessage Error String returned by servers in case of error
     * @param results ItemList containing results of successful commands.
     */
    public boolean commandCompleted(String cmd, String errorMessage, int errorSeverity, ItemList results) {
//        System.out.println("Community.commandCompleted(): " + cmd);

        removeView(PENDING_GAMESTART);

        // If command was succesful:
        if (errorMessage == null) {

            // If we just logged in, set our various SNAP session IDs,
            // and our buddy list.
        	if (cmd.equals("extendedLogin")) {
            	System.out.println("ItemList.toString(): {\n"+results.toString()+"}");
        		setLoginInfo( results);
            	
            	setMOTD( results);
				buddyList.set( results.getList("buddyList"));
				if(results.getList("messageList")!=null && results.getList("messageList").size()>0) {
					switchToView( Community.MOTD);
				} else {
					switchToView( Community.HOME);
				}
				return true;
        	}
        	// If we started a game successfully, switch to game view.
        	else if (cmd.equals("gameStart") || cmd.equals("randomStart")) {
            	mazeRacer.setOpponent( getOpponentName( results));
            	startGame( false);
                return true;
            }
        	// Logout.
            else if (cmd.equals("unifiedLogout")) {
            	setLoginInfo( null, null, null, null);
            	switchToView( Community.WELCOME);
                return true;
            }
        } else {
        	if (cmd.equals("randomStart")) {
        		if (errorMessage.equals("CmdTimeout 762") && waitingForGame) {
        			showError("No players were available " +
        					"Please try again."
        					);
        			return true;
        		}
        		if (!waitingForGame) return true;
        	}
        	else if (cmd.equals("gameStart")) {
        		if (errorMessage.equals("CmdTimeout 762") && waitingForGame) {
        			showError("Sorry, the player didn't respond to your " +
        					"challenge in time.  Please try again!"
        					);
        			return true;
        		}
        		if (!waitingForGame) return true;
        	}
        	else if (cmd.equals("requestBuddy")) {
        		if (errorMessage.equals("Not Found 914")) {
        			showError("Sorry, couldn't find a user by that name. " +
        					"Please try another name!"
        					);
        			return true;
        		}
        	}
        }
        return false;
    }

    /**
     * Main event loop for sending SNAP commands to server.
     * Handles sending commands, catching error results, and notifying
     * Views of the success/failure of their requests.
     */
    public void run() {
        AsyncCommandListener listener;
        ItemList itemList, returnValues;
        String error, cmd;

        try {
            //-----------------------------------
            //  run main loop
            while (!done) {
                //-------------------------------
                // Wait for next request
                synchronized (this) {
                    while (cmdList.isEmpty()) {
                        try {wait();}
                        catch (InterruptedException e) {}
                    }

                    synchronized (cmdList) {
                    	itemList = (ItemList)cmdList.elementAt(0);
                    	cmdList.removeElementAt(0);
                    }
                }

                cmd = itemList.getString("cmd");
                returnValues = null;
                lastError = null;
                lastErrorSeverity = -1;

                System.out.println("Community.run(), cmd = " + cmd);

                // Login and CreateUser are special, in that they are
                // guaranteed to be first SNAP commands attempted in the
                // CommUI app.  Therefore, if there are network-related
                // errors or exceptions, we display extra-informational
                // error dialogs suggesting users check their permissions
                // and GPRS/phone account service settings & plans.
                if (cmd.equals("extendedLogin") || cmd.equals("createUser")) {
                    try {
                        if (cmd.equals("extendedLogin")) {
                        	returnValues = comm.extendedLogin(
                                itemList.getString("user"),
                                itemList.getString("pass"),
                                itemList.getInteger("gcid"),
                                itemList.getString("presence"),
                                itemList.getBoolean("motd")
                            );
                        } else {
                            returnValues = comm.createUser(
                                    itemList.getString("user"),
                                    itemList.getString("pass"),
                                    null, null,
                                    itemList.getString("email"),
                                    itemList.getString("dob"),
                                    null, null,
                                    operatorId
                                );
                        }
                	} catch (RuntimeException r) {
                        // Check for SecurityExceptions (see if user 
                        // pressed "no" to the GPRS permissions dialog popup.)
                        // If so, tell them to exit app and restart.  Note:
                        // ServerComm takes the summary of CheckedExceptions,
                        // then rewraps them as RuntimeExceptions before 
                        // rethrowing, so we need to check for the name of the
                        // original exception inside the body of the Runtime
                        // Exception.
                		if (r.toString().toLowerCase().indexOf("security")!=-1) {
                    		showError( 
                    				"Unable to connect to network.  You must " +
                    				"press 'YES' in the GPRS/Airtime Permissions " +
                    				"dialog.  Please exit app and restart.",
                    				Community.REALLY_EXIT, Dialog.ALERT
                    				);
                    		continue;
                		}                    				
                    } catch (Exception e) {
                        // Since extendedLogin or createUser are typically
                        // the first command executed after a person starts
                        // the app, in case of failure to connect to the server,
                        // display a special error message about verifying
                        // connectivity settings.
                		showError( "There has been a problem connecting, "
                            + "please check your device network profile "
                            + "or permission settings."
                            );
                        continue;
                    }
                    
                // Get top rankings
                } else if (cmd.equals("topRankings")) {
                    returnValues = comm.getTopRankings(
                        itemList.getInteger("gcid"),
                        itemList.getString("stat"),
                        itemList.getInteger("count"),
                        null,
                        null
                    );
                
                // Get player statistics
                } else if (cmd.equals("playerStatistics")) {
                    returnValues = comm.getPlayerStatistics(
                        itemList.getInteger("gcid"),
                        null,
                        null,
                        null,
                        null
                    );
                    
                // Send a friend message    
                } else if (cmd.equals("sendBuddyMessage")) {
                    comm.sendBuddyMessage(
                        itemList.getString("name"),
                        itemList.getString("msg")
                    );
                
                // Accept another user's friend request
                } else if (cmd.equals("acceptBuddyRequest")) {
                    comm.acceptBuddyRequest(itemList.getString("name"));

                // Decline another user's friend request
                } else if (cmd.equals("declineBuddyRequest")) {
                    comm.declineBuddyRequest(itemList.getString("name"));

                // Ask another user to be your friend
                } else if (cmd.equals("requestBuddy")) {
                    comm.requestBuddy(
                        itemList.getString("name"),
                        itemList.getString("message")
                    );

                // Remove a friend
                } else if (cmd.equals("removeBuddy")) {
                    comm.removeBuddy(itemList.getString("name"));

                // Stop the game you are currently playing, 
                // potentially reporting your scores as well.
                } else if (cmd.equals("gameStop")) {
                    comm.gameStop(
                            itemList.getInteger("gcid"),
                            itemList.getString("category1"),
                            itemList.getString("value1"),
                            itemList.getString("category2"),
                            itemList.getString("value2"),
                            itemList.getString("category3"),
                            itemList.getString("value3"),
                            itemList.getString("category4"),
                            itemList.getString("value4"),
                            itemList.getString("presence")
                        );
                    
                // Start a game in a particular game room/lobby.
                } else if (cmd.equals("gameStart")) {
                	waitingForGame = true;
                    returnValues = comm.gameStart(
                        itemList.getString("lobbyID"),
                        itemList.getInteger("lobbyMaxUsers"),
                        itemList.getString("gameRoomID"),
                        itemList.getInteger("gameRoomMaxUsers"),
                        itemList.getInteger("mode"),
                        itemList.getInteger("leader")
                    );
                    
                // Request to be placed into a game with an 
                // opponent the SNAP server selects for you
                } else if (cmd.equals("randomStart")) {
                	waitingForGame = true;
                    returnValues = comm.randomStart(
                        itemList.getString("lobbyID"),
                        itemList.getInteger("lobbyMaxUsers"),
                        itemList.getInteger("gameRoomMaxUser")
                    );
                    
                // Send a game command to your opponent(s)
                } else if (cmd.equals("sendGamePacket")) {
                    comm.sendGamePacket(itemList.getByteArray("data"));
                    
                // Log out
                } else if (cmd.equals("unifiedLogout")) {
                    comm.unifiedLogout();

                // Unrecognized command
                } else {
                    throw new RuntimeException("unknown snap cmd: " + cmd);
                }

                // Notify listener of command results
                boolean handled = false;
                listener = (AsyncCommandListener)itemList.getItem("listener");
                if (listener != null) {
                    handled = listener.commandCompleted(cmd, lastError, lastErrorSeverity, returnValues);
                }

                // Register asynchronous error listener, if there is one for this command.
                asyncListener = (AsyncCommandListener)itemList.getItem("asyncListener");
                if (asyncListener != null) asyncCommand = cmd;
                
                // If command resulted in a nonfatal error, and if the listener
                // did not handle this error, then display a generic nonfatal
                // error dialog.
                if (!handled && lastErrorSeverity == SnapEventListener.SEVERITY_NON_FATAL) {
                    showError( lastError);
                }
                waitingForGame = false;

            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Application has encountered an unexpected problem and needs to close.");
            e.printStackTrace();
            try { Thread.sleep( 5000); } catch (Exception ignore) {}
        }

        System.out.println("DONE WITH MAIN EVENT LOOP!");
        exit();
    }
}
