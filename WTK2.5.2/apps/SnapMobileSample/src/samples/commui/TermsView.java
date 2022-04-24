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

import samples.ui.Component;
import samples.ui.Event;
import samples.ui.EventListener;
import samples.ui.View;

import com.nokia.sm.net.ItemList;

/**
 * This screen shows SNAP terms-and-conditions text and waits for user's
 * acceptance and/or rejection of the terms.  Upon acceptance, a new user
 * account creation request is submitted to the SNAP servers.  Upon rejection,
 * the user is returned to the main LoginView screen.  
 *
 */
public class TermsView extends Dialog implements AsyncCommandListener {

    private boolean pendingLogin;
    static protected String TERMS_AND_CONDITIONS		= 
    	"I agree to all of the SNAP Mobile Usage Terms and Privacy Policy, " +
    	"all found and reviewed by me at http://snapmobile.nokia.com/terms";

    /**
     * Create Terms dialog w/ text passed in 
     * 
     * @param community Main SnapLogin instance
     * @param name Name of View (used for inter-screen navigation)
     * @param msg Text to appear in dialog
     */
    public TermsView(Community community, String name, Component comp)
    {
        super( community, name, null, Community.YES, Community.NO, comp, null, Dialog.YES_NO, null);
        listener = this;
    }

    /**
     * Initiates <code>createUser</code> call on SNAP servers, with new user
     * account information.
     *
     */
    protected void doCreateUser()
    {
        synchronized (this) {
       	
	        if (!pendingLogin && !community.isLoggedIn()) 
	        {
	            setWaiting(true);
	            pendingLogin = true;
	
	            ItemList itemList = new ItemList();
	            itemList.setItem("cmd", 	"createUser");
	            itemList.setItem("user", 	community.username);
	            itemList.setItem("pass", 	community.password);
	            itemList.setItem("email", 	community.emailAddress);
	            itemList.setItem("dob", 	community.dateOfBirth);
	            // 911: add OperatorID here!
	            itemList.setItem("listener", this);
	
	            community.executeCmd( itemList);
	        }
        }
    }

    /**
     * Initiates <code>extendedLogin</code> call on SNAP servers with new
     * user account information.
     *
     */
    protected void doLogin()
    {
        synchronized (this) {
       	
            if (!pendingLogin && !community.isLoggedIn()) {
                setWaiting(true);
                pendingLogin = true;

                ItemList itemList = new ItemList();
                itemList.setItem("cmd", "extendedLogin");
                itemList.setItem("user", community.username);
                itemList.setItem("pass", community.password);
                itemList.setItem("gcid", community.getGCID());
                itemList.setItem("presence", "PRESENCEDATAHERE");
                itemList.setItem("motd", new Boolean(true));
                itemList.setItem("listener", community);

                community.executeCmd(itemList);
            }
        }
    }
    
 
    public boolean handleEvent(Event e) {
    	
    	if (e.getType() == Event.ITEM_DESELECTED) {
    		if (e.getValue().equals(Community.YES)) {
	        	doCreateUser();
	        } else {
	        	community.switchToView( Community.WELCOME);
	        }
    		return true;
    	}
    	return false;
    }
    /**
     * If user presses "YES" and accepts terms and conditions, then
     * create their new account.
     */
    public void leftSoftButtonPressed(String label) {
    	doCreateUser();
    }

    /** 
     * If user presses "NO" and rejects terms and conditions, return
     * them to the main login screen.
     */
    public void rightSoftButtonPressed(String label) {
    	community.switchToView( Community.WELCOME);
    }

    
    /**
     * Handles the results of SNAP calls, both successes and error conditions.
     * Throws up error dialogs for error conditions.  Upon success:
     * <ul>
     * <li><code>createUser</code>: tries to log in user with 
     *    <code>extendedLogin</code></li>
     * <li><code>extendedLogin</code>: exits app w/ successful login condition</li>
     * </ul>
     * 
     * @param cmd Name of command attempted
     * @param errorMessage Text of error message, if there is an error (null if not)
     * @param results Results of command, if successful
     */
     public boolean commandCompleted(String cmd, String errorMessage, int errorCode, ItemList results) {
 
        setWaiting(false);

        synchronized (this) {
            pendingLogin = false;
        }

        //-------------------------
        // Errors
        if (errorMessage != null) {

        	if (errorMessage.equals("WebServiceDupUser 962")) {
        		community.showDialog(
        			"Error", "User Name already in use.  Please use another.",
        			null, Community.USER_PASS, Dialog.ALERT
        			);
        		return true;
        	
        	} else if (errorMessage.equals("WebServiceDupEmail 972"))  {
        		community.showDialog(
        				"Error", "E-mail address is already in use, please use another.",
        				null, Community.EMAIL_DOB, Dialog.ALERT
            			);
        		return true;
        	
        	} else if (errorMessage.equals("WebServiceInvalidData 973"))  {
        		community.showDialog(
        				"Error", "Some of your signup info is incomplete! " +
        				"["+results.getItem("additional")+"]. " +
        				"Please go back and make sure all fields are filled in.",
        				null, Community.USER_PASS, Dialog.ALERT
            			);
        		return true;

        	} else if (errorMessage.equals("WebServiceAgeViolation 974"))  {
        		community.showDialog(
        				"Error", "Sorry, you are not old enough to create an account.",
        				null, Community.LOGIN, Dialog.ALERT
            			);
        		return true;

        	} else if (errorMessage.equals("WebServiceCreateUserError 980") ||
        				errorMessage.equals("WebServiceSystemError 961")
        				)  
        	{
        		community.showError(
        				"Unable to connect to server. Please try again later."
            			);
        		return true;

        	} else {
        		return false;
        	}
        
        //-------------------------
        // Success results
        } else if (cmd != null){
        
        	// Once we've created a user, then log them in
	    	if (cmd.equals( "createUser")) {
	    		doLogin();
	    		return true;

	    	// Once we're logged in, then go back to the online
	    	// play main menu 
//	    	} else if (cmd.equals( "extendedLogin")) {
//	    		community.setLoginInfo( results);
//	        	community.switchToView( Community.HOME);        		
//	        	return true;
	    	}
        }

        return false;
    }       
}
