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

import javax.microedition.lcdui.Image;

import samples.commui.Community;
import samples.ui.Label;
import samples.ui.TextField;

/**
 * UserPassView is a screen for capturing a user's username and password 
 * for new account creation. It contains one username entry field and 
 * two password entry fields ("password" and "confirm password").
 * Username is displayed in plaintext, while passwords are masked.
 * <p>
 * Input restrictions are listed in the comments for the 
 * <code>validate()</code> method.
 * 
 */
public class UserPassView extends CommunityView {
	
    private Label userLabel;
    private Label passLabel;
    private Label passLabel2;
    private TextField userField;
    private TextField passField;
    private TextField passField2;

    /**
     * Sets up widget appearance and layout.
     * 
     * @param login <code>SnapLogin</code> main instance
     * @param name Name of screen (used for inter-screen navigation by <SnapLogin>)
     */
    public UserPassView( Community login, String name) 
    {
	    super( login, name);

	    setLeftSoftButton( Community.NEXT);
	    setRightSoftButton( Community.BACK);
	    setBackgoundImage(Community.FADING_BACKGROUND);
	    
        userLabel = new Label("Enter Username", false);
        userLabel.setBackgroundImage( new Image[] {text_on});
        userLabel.setDrawShadows( false);
        userLabel.setDimension( text_off.getWidth(), text_off.getHeight());
        userLabel.setLocation(( getWidth() - userLabel.getWidth()) / 2, 30);

        add( userLabel);

	    userField = new TextField(15);

        userField.setEntryMode( TextField.ENTRY_USERNAME);
        userField.setDrawShadows(false);
        userField.setForeground(0x00c0c0c0);
        userField.setFont(TEXTFIELD_FONT);
        userField.setLocation(userLabel.getX(), userLabel.getY() + userLabel.getHeight());
        userField.setDimension(userLabel.getWidth(), 20);

        add( userField);

        passLabel = new Label("Enter Password", false);
        passLabel.setBackgroundImage( new Image[] {text_on});
        passLabel.setDrawShadows( false);
        passLabel.setDimension( text_off.getWidth(), text_off.getHeight());
        passLabel.setLocation(( getWidth() - passLabel.getWidth()) / 2, userField.getY() + userField.getHeight() + 15);
   
        add( passLabel);

	    passField = new TextField(15);
        passField.setEntryMode( TextField.ENTRY_ASCII);
        passField.setDispMode( TextField.DISP_PASSWORD);
        passField.setDrawShadows(false);
        passField.setForeground(0x00c0c0c0);
        passField.setFont( TEXTFIELD_FONT);
        passField.setLocation(passLabel.getX(), passLabel.getY() + passLabel.getHeight());
        passField.setDimension(passLabel.getWidth(), 20);

        add( passField);

        passLabel2 = new Label("Confirm Password", false);
        passLabel2.setBackgroundImage( new Image[] {text_on});
        passLabel2.setDrawShadows( false);
        passLabel2.setDimension( text_off.getWidth(), text_off.getHeight());
        passLabel2.setLocation(( getWidth() - passLabel2.getWidth()) / 2, passField.getY() + passField.getHeight() + 15);

        add( passLabel2);

	    passField2 = new TextField(15);

        passField2.setEntryMode( TextField.ENTRY_ASCII);
        passField2.setDispMode( TextField.DISP_PASSWORD);
        passField2.setDrawShadows(false);
        passField2.setForeground(0x00c0c0c0);
        passField2.setFont( TEXTFIELD_FONT);
        passField2.setLocation(passLabel2.getX(), passLabel2.getY() + passLabel2.getHeight());
        passField2.setDimension(passLabel2.getWidth(), 20);

        add( passField2);
    }

    /**
     * Validates username and password entries according to the following rules:
	 * <ul>
	 * <li>Valid characters for username: alphanumeric and underscore. 
	 * No spaces.</li>
	 * <li>Valid characters for passwords: all ASCII values from 32-126.</li>
	 * <li>Usernames and passwords must be 4-15 characters long, and cannot
	 * match.</li>
	 * <li>Password and confirm password entries must match</li>
	 * </ul>
	 * 
     * @return true if input validates
     */
    protected boolean validate()
    {
        String username = userField.getText();
        if(username.length()==0) {
        	community.showError( "Please complete Username");
        	return false;
        }
        String password = passField.getText();
        if(password.length()==0) {
        	community.showError( "Please complete Password");
        	return false;
        }        
        if (username.equals(password)) {
        	community.showError("Your User Name must be different from your Password");
        	passField.setText("");
        	passField2.setText("");
        	return false;        	
        }
        String password2 = passField2.getText();
        if(password2.length()==0) {
        	community.showError( "Please complete Confirm Password");
        	return false;        	
        }
        if (!password.equals( password2)) {
        	community.showError( "Passwords do not match.  Please try again");
        	passField.setText("");
        	passField2.setText("");
        	repaint();
        	return false;
        }    	
        if (password.length() < 4 || password.length() > 15 ||
        	username.length() < 4 || username.length() > 15) 
        {
        	community.showError( 
        			"Your user name and password must be from 4 to 15 characters long. "        		
//        			" long and contain only letters from \"a-z\", \"A-Z\", \"0-9\", " +
//        			"\"_\" (underscores) or \"-\" (hyphens)."
        			);
        	passField.setText("");
        	passField2.setText("");
        	repaint();
        	return false;
        }    	
        community.username  = username;
        community.password  = password;
        community.password2 = password2;
        return true;
    }
    
    /**
     * If inputs validate when left softkey pressed, then to go
     * Email/Date-of-Birth entry screen.
     * 
     * @param label Current left soft key label text
     */
    public void leftSoftButtonPressed(String label) 
    {
    	if (validate()) {
    		community.switchToView( Community.EMAIL_DOB);    			
    	}
    }

    /**
     * If right soft key pressed, return to main LoginView screen.
     * 
     * @param label Current left soft key label text
     */
    public void rightSoftButtonPressed(String label) {
    	community.switchToView( Community.LOGIN);
    }
}
