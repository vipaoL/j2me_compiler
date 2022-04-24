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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;
import com.nokia.sm.net.ItemList;
import com.nokia.sm.net.SnapEventListener;

import samples.ui.*;
/**
 * Main screen of the Community LoginView of the <code>Community</code> package.
 *  Allows user to:
 * <ul>
 * <li>Log in with username/password</li>
 * <li>Retrieve their password (via username) if they have forgotten it</li>
 * <li>Create a new user account</li>
 * </ul>
 * Also, shows "Save LoginView" checkbox so user can choose whether to have
 * their username and password saved to RMS, to be auto-filled-in next
 * time they run the app.
 * <p>
 * The "Save LoginView" and "Create New User" options can be removed from the
 * GUI (based on operator policies) by changing flags in the .jad file
 * settings.
 *
 */
public class LoginView extends CommunityView implements EventListener, AsyncCommandListener
{
    private Label 			userLabel;
    private Label 			passLabel;
    private TextField 		userField;
    private TextField 		passField;
    private Button 			loginButton;
    private Button 			registerButton;
    private Choice 			remember;
    private EventListener 	listener;
    private boolean 		pendingLogin;
    private String			username;
    private String 			password;

    static final String RMS_USERNAME        = "userName";
    static final String RMS_PASSWORD        = "passWord";

    /**
     * Sets up UI appearance and layout.
     *
     * @param community <code>Community</code> main instance
     * @param name Name of screen (used for inter-screen navigation)
     */
    public LoginView(Community community, String name, EventListener listener) {
        super(community, name);
        String uid, pw;
        
        username = "";
        password = "";
        this.listener = listener;
        setLeftSoftButton( Community.SELECT);
        setRightSoftButton( Community.BACK);
        setBackgoundImage( Community.FADING_BACKGROUND);

        userLabel = new Label("Username", false);
        userLabel.setBackgroundImage(new Image[] {text_on});
        userLabel.setDrawShadows(false);
        userLabel.setDimension(text_off.getWidth(), text_off.getHeight());
        userLabel.setLocation((getWidth() - userLabel.getWidth()) / 2, 18);

        add(userLabel);

        uid = getRmsValue( RMS_USERNAME);
        if (uid.length() == 0) uid = "";

        userField = new TextField(15);
        userField.setText( uid);
        userField.setDrawShadows( false);
        userField.setForeground( 0x00c0c0c0);
        userField.setFont( TEXTFIELD_FONT);
        userField.setEntryMode( TextField.ENTRY_USERNAME);
        userField.setLocation( userLabel.getX(), userLabel.getY() + userLabel.getHeight());
        userField.setDimension( userLabel.getWidth(), 20);

        add(userField);

        passLabel = new Label("Password", false);
        passLabel.setBackgroundImage(new Image[] {text_off});
        passLabel.setDrawShadows(false);
        passLabel.setLocation(userField.getX(), userField.getY() + userField.getHeight() + 5);
        passLabel.setDimension(text_off.getWidth(), text_off.getHeight());

        add(passLabel);

        pw = getRmsValue( RMS_PASSWORD);
        if (pw.length() == 0) pw = "";

        passField = new TextField(15);
        passField.setText(pw);
        passField.setDrawShadows(false);
        passField.setFont(TEXTFIELD_FONT);
        passField.setForeground(0x00c0c0c0);
        passField.setEntryMode( TextField.ENTRY_ASCII);
        passField.setDispMode( TextField.DISP_PASSWORD);
        passField.setLocation(passLabel.getX(), passLabel.getY() + passLabel.getHeight());
        passField.setDimension(passLabel.getWidth(), 20);

        add(passField);

        remember = new Choice(check_off, check_on, "Save Login?");
        remember.setState( !(uid.equals("") && pw.equals(""))); 
        remember.setDrawShadows(false);
        remember.setBackgroundImage(new Image[] {text_off, text_on});
        remember.setLocation(passField.getX(), passField.getY() + passField.getHeight() + 5);
        remember.setDimension(text_off.getWidth(), text_off.getHeight());

        add(remember);

        loginButton = new Button("login");
        loginButton.setFont(ButtonListView.BUTTON_FONT);
        loginButton.setStateData(ButtonListView.IMAGE_LIST, ButtonListView.COLOR_LIST);
        loginButton.addEventListener(this);
        loginButton.setDimension(
                ButtonListView.IMAGE_LIST[0].getWidth(),
                ButtonListView.IMAGE_LIST[0].getHeight()
        );
        loginButton.setLocation((getWidth() - loginButton.getWidth()) / 2, remember.getY() + remember.getHeight() + 5);

        add(loginButton);

        registerButton = new Button(Community.CREATE_ACCOUNT);
        registerButton.setFont(ButtonListView.BUTTON_FONT);
        registerButton.setStateData(ButtonListView.IMAGE_LIST, ButtonListView.COLOR_LIST);
        registerButton.addEventListener(this);
        registerButton.setDimension(
                ButtonListView.IMAGE_LIST[0].getWidth(),
                ButtonListView.IMAGE_LIST[0].getHeight()
        );
        registerButton.setLocation((getWidth() - registerButton.getWidth()) / 2, loginButton.getY() + loginButton.getHeight() + 5);

        add(registerButton);
    }
    /**
     * Save LoginView info in database
     * @param name  Name of the LoginView Field
     * @param value LoginView ID value
     */
    private void saveRmsValue(String name, String value) {
        try {
            RecordStore store = RecordStore.openRecordStore(name, true);

            if (store.getNumRecords() > 0) {
                store.setRecord(1, value.getBytes(), 0, value.length());
            } else {
                store.addRecord(value.getBytes(), 0, value.length());
            }

            store.closeRecordStore();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get LoginView from RMS
     * @param name LoginView Name
     * @return login String
     */
    private String getRmsValue(String name) {
        String value = "";

        try {
            RecordStore store = RecordStore.openRecordStore(name, true);

            if (store.getNumRecords() > 0) {
                byte[] buf = store.getRecord(1);
                value = buf == null ? "" : new String(store.getRecord(1));
            }

            store.closeRecordStore();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    /**
     * Handle key presses. Update the state of this instance in light of a
     * key press action.
     *
     * @param key The key code for the key that was pressd.
     */
    public void keyPressed(int key) {
        super.keyPressed(key);

        if (getFocus() == userField) {
            userLabel.setBackgroundImage(new Image[] {text_on});
            passLabel.setBackgroundImage(new Image[] {text_off});

            repaint();
        } else if (getFocus() == passField) {
            userLabel.setBackgroundImage(new Image[] {text_off});
            passLabel.setBackgroundImage(new Image[] {text_on});

            repaint();
        } else {
            userLabel.setBackgroundImage(new Image[] {text_off});
            passLabel.setBackgroundImage(new Image[] {text_off});

            repaint();
        }
    }

    /**
     * Handles "SELECTED" events for pushbuttons:
     *
     * Submits <code>extendedLogin</code> request to the SNAP servers.
     */

    public boolean handleEvent(Event e) {
        //System.out.println("LoginView.handleEvent(): " + e);

        if (e.getType() == Event.ITEM_DESELECTED) {
            if (e.getSource() == registerButton) {
                community.switchToView( Community.USER_PASS);
            }	
            else if (e.getSource() == loginButton) {
            	doLogin();
            }
           	return true;    
        }
        return false;
    }

    /**
     * Submits <code>extendedLogin</code> request to the SNAP servers.
     */
    protected void doLogin()
    {
        synchronized (this) {

            if (validate(false) && !community.isLoggedIn()) {
                setWaiting(true);

                ItemList itemList = new ItemList();
                itemList.setItem("cmd", "extendedLogin");
                itemList.setItem("user", username);
                itemList.setItem("pass", password);
                itemList.setItem("gcid", community.getGCID());
                itemList.setItem("presence", "EXTENDEDPRESENCEHERE");
                itemList.setItem("motd", new Boolean(true));
                itemList.setItem("listener", community);

                // Save or clear login info from RMS, depending on
                // whether "Save Login" checkbox is checked.
                if (remember.getState()) {
                    saveRmsValue( RMS_USERNAME, username);
                    saveRmsValue( RMS_PASSWORD, password);
                } else {
                    saveRmsValue( RMS_USERNAME, "");
                    saveRmsValue( RMS_PASSWORD, "");                	
                }

                community.setUsername(userField.getText());
                community.executeCmd(itemList);
            }
        }
    }


    /**
     * Makes sure user has entered a username and password (for login),
     * or else just a username (for password retrieval).
     *
     * @param retrieve <code>true</code> if validating for password
     *   retrieval, <code>false</code> for login
     * @return <code>true</code> if validates
     */
    protected boolean validate( boolean retrieve)
    {
        username = userField.getText();
        if (username.equals("")) {
            if (retrieve) {
                community.showError( "Please enter your User Name");
            } else {
   
            	community.showError( "Please enter your User Name and Password");
            }
            return false;
        }
        password = passField.getText();
        if (password.equals("") && retrieve == false) {
            community.showError( "Please enter your User Name and Password");
            return false;
        }

        return true;
    }


    /**
     * Handle results of SNAP server requests.  If error, show
     * error message.  If successful, user is logged in.
     *
     * @param cmd Name of command attempted
     * @param errorMessage Text of error message, if there is an error (null if not)
     * @param results Results of command, if successful
     */
    public boolean commandCompleted(String cmd, String errorMessage, int errorSeverity, ItemList results) {
//        System.out.println("LoginView.commandCompleted(): " + cmd);

        setWaiting(false);

        synchronized (this) {
            pendingLogin = false;
        }

        //--------------------------
        // Errors
        if (errorMessage != null) {

            if (errorMessage.equals("WebServiceAuthFailure 965") ||
            	errorMessage.equals("WebServiceInvalidData 973"))
            {
                community.showError(
                        "Login failed.  Please check that you have " +
                        "entered the correct Username and " +
                        "Password.  " +
                        "If this is your first time playing online, " +
                        "please select CREATE ACCOUNT."
                        );
                password = "";
                passField.setText( "");
                return true;

            } else if (errorMessage.equals("WebServiceUnknownUser 966")) {
                community.showError(
                            "Username does not exist.  Please check " +
                            "that you have entered the correct Username. " +
                            "If this is your first time playing online, " +
                            "please select CREATE ACCOUNT."
                            );
                return true;
           }
/*
        //--------------------------
        // Command successful
        } else if (cmd != null){

            // After login, set buddies and go to online play main menu
            if (cmd.equals("extendedLogin")) {
                community.setLoginInfo( results);
                community.setMOTD( results);
                community.setBuddyList( results.getList("buddyList"));
                community.switchToView( community.MOTD);
                return true;
            }
*/
        }

        return false;
    }

    /**
     * Submit LoginView Request
     */

    public void leftSoftButtonPressed(String label) {
        //System.out.println("LoginView.leftSoftButtonPressed(): " + label);

        getFocus().keyPressed(Canvas.FIRE, ENTER_BUTTON);
        getFocus().keyReleased(Canvas.FIRE, ENTER_BUTTON);

    }

    /**
     * If right loginButton pressed switch back to Community View
     */
    public void rightSoftButtonPressed(String label) {
    		community.switchToView(Community.BACK);
    }
}