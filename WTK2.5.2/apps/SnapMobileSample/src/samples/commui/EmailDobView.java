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

import java.util.Calendar;
import java.util.Date;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Image;

import samples.commui.Community;
import samples.ui.Label;
import samples.ui.TextField;

/**
 * EmailDobView is a screen for capturing a user's email address and 
 * date of birth for new account creation. It contains a text entry field 
 * for email address and a date entry field (text entry field with modified
 * behavior) for date of birth entry. 
 * <p>
 * Input restrictions are listed in the comments for the 
 * <code>validate()</code> method.
 * 
 */
public class EmailDobView extends CommunityView
{
    static protected String	DATE_SEPARATOR_CHAR			= "/";
    static protected String DATE_FORMAT					= "MM/DD/YYYY";
    private Label emailLabel;
    private Label dobLabel;
    private TextField emailField;
    private TextField dobField;
    final private int[] daysInMonth = {31,28,31,30,31,30,31,31,30,31,30,31};

	/**
	 * Sets up widget appearance and layout.
	 * 
	 * @param login <code>SnapLogin</code> main instance
	 * @param name Name of screen (used for inter-screen navigation by <SnapLogin>)
	 */
    public EmailDobView( Community community, String name) 
    {
	    super(community, name);
	    String email, date;

	    setLeftSoftButton( Community.NEXT);
	    setRightSoftButton( Community.BACK);
	    setBackgoundImage(Community.FADING_BACKGROUND);
	
        emailLabel = new Label( "Enter E-mail", false);
        emailLabel.setBackgroundImage( new Image[] {text_on});
        emailLabel.setDrawShadows( false);
        emailLabel.setDimension( text_off.getWidth(), text_off.getHeight());
        emailLabel.setLocation(( getWidth() - emailLabel.getWidth()) / 2, 30);

        add( emailLabel);

        email = community.emailAddress;
        if (email == null) email = "";

        emailField = new TextField(40);
        emailField.setText(email);
        emailField.setDrawShadows(false);
        emailField.setForeground(0x00c0c0c0);
        emailField.setFont(TEXTFIELD_FONT);
        emailField.setEntryMode( TextField.ENTRY_EMAILADDR);
        emailField.setLocation(emailLabel.getX(), emailLabel.getY() + emailLabel.getHeight());
        emailField.setDimension(emailLabel.getWidth(), 20);

        add( emailField);

        dobLabel = new Label("Enter Date of Birth", false);
        dobLabel.setBackgroundImage( new Image[] {text_on});
        dobLabel.setDrawShadows( false);
        dobLabel.setDimension( text_off.getWidth(), text_off.getHeight());
        dobLabel.setLocation(( getWidth() - dobLabel.getWidth()) / 2, emailField.getY() + emailField.getHeight() + 15);

        add( dobLabel);

        date = community.dateOfBirth;
        if (date == null) date = DATE_FORMAT;

	    dobField = new TextField(10);
        dobField.setText( date);
        dobField.setEntryMode( TextField.ENTRY_NUMERIC);
	    dobField.setDrawShadows(false);
	    dobField.setForeground(0x00c0c0c0);
	    dobField.setFont(TEXTFIELD_FONT);
	    dobField.setDimension(dobLabel.getWidth(), 20);
        dobField.setLocation(dobLabel.getX(), dobLabel.getY() + dobLabel.getHeight());

        add( dobField);

        handleFocusChanged();
    }

	 /**
	  * Special display behavior for date entry field: if focus is not on
	  * the date entry field, and it is empty, then display the date format
	  * string in it (e.g., "MM/DD/YYYY").  If the date entry field does
	  * get focus, and contains the date format string, then empty it out
	  * so the user can type their date of birth.
	  */
    public void handleFocusChanged()
    {
	    if (getFocus() == dobField) {
	    	if (dobField.getText().equals(DATE_FORMAT)) dobField.setText("");
	    } else {
	    	if (dobField.getText().equals("")) dobField.setText(DATE_FORMAT);
	    }
        repaint();
    }
    
    /**
     * For each key pressed while this View is active, implements special 
     * display and event processing behavior for date entry field:
     * <ol>
     * <li>Disallow user from using the left/right keys to move the cursor 
     *   back into the text</li>
     * <li>Automatically insert date separator characters after the first
     *   two pairs of numerals entered. E.g., after the user enters "11"
     *   for the month, insert a separator: "11/".  After the user then
     *   enters two more numerals for the day: "11/23", insert another 
     *   separator: "11/23/"</li>
     * <li>Automatically delete the separator characters as the user 
     *   presses the DELETE key.  E.g., if the date starts as: "11/23/1",
     *   and the user presses DELETE once, the date string would then
     *   read: "11/23"</li>
     * </ol>
     */
    public void keyPressed(int key) {
    	// Disallow mousing left/right in dob field, because we do our own
    	// automatic insertion of characters in the dob string.
    	if (getFocus()==dobField && (key==TextField.LEFT || key==TextField.RIGHT)) return;

    	// Handle events normally
    	super.keyPressed(key);
    	
    	// Auto-insert/delete the date separator character between the days/months/years
    	// portion of the date-of-birth string, whenever user adds/deletes from this field.
        if (getFocus()==dobField) {
        	String dob = dobField.getText();
        	int dobLen = dob.length();
        	if (dobLen==2 || dobLen==5) {
        		// Handle auto-insertion of "/" date separator after chars 2 and 4
        		if (key>=Canvas.KEY_NUM0 && key <= Canvas.KEY_NUM9) {
	        		dobField.setText( dob + DATE_SEPARATOR_CHAR);
    	        // Handle auto-deleting of "/" date separator after chars 2 and 4
            	} else if (key == TextField.DELETE || key == TextField.BACKSPACE) {
	        		dobField.setText( dob.substring(0,dobLen-1));
	        	}
        		repaint();       	
        	}
        }
    }


    /**
     * Validates email address and date of birth, according to rules
     * specified in <code>validateEmail()</code> and 
     * <code>validateAge()</code>.
     * 
     * @return <code>true</code> if email and date of birth validate
     */
    protected boolean validate()
    {
        String email = emailField.getText();
        String dob = dobField.getText();
        Date birthdate = validateAge(dob);
        if ((birthdate == null) && (!validateEmail( email))){
    		community.showError(
    				"Please enter a valid email address and birthdate " +
    				"in the format " + DATE_FORMAT);
    		return false;
        }
        else if (birthdate == null) {
        	community.showError( 
        			"Please enter a valid date of birth, " +
        			"in the format " + DATE_FORMAT);
        	dobField.setText("");
        	return false;     	
        }
        else if (!validateEmail( email)) {
        	community.showError(
        			"Please enter a valid e-mail address"+
        			" in the format <name>@<domain>.<root>"
        			);
        	return false;        	
        }
        else if (!isOldEnough( birthdate, community.getMinAge())) {
        	community.showError( 
        			"Sorry, you are not old enough to create an account", 
        			Community.LOGIN, Dialog.ALERT
        			);
        	return false;        	       	
        }
        
        community.emailAddress = email;
        community.dateOfBirth = dob;
        return true;
    }
    
    /**
     * Email validation rules:
     * <ul>
     * <li>Must have exactly one '@' character</li>
     * <li>Must have valid characters before the '@' sign: all 
     *   alphumeric, '_', '-', '.' and '+'</li>
     * <li>Valid characters after the '@' sign: alphanumeric, '.',
     *   and '-'</li>
     * <li>Domain portion must have at least one period, and never
     *   two or more adjacent to each other</li>
     * <li>There must be at least 1 character in the first subdomain
     *   before the first period after the '@' sign.</li>
     * <li>Last domain extension must have between 2 and 4 characters</li>
     * </ul>
     * 
     * @param email The email address to validate
     * @return <code>true</code> if the email address validates
     */
    public boolean validateEmail( String email)
    {
    	// Email name prefix (before '@' sign):
    	// Make sure email address has at least one @ sign
    	int pos = email.indexOf( '@');
    	if (pos <= 0) return false;
    	String suffix = email.substring( pos+1);
    	// Reject if email address has more than one @ sign
    	pos = suffix.indexOf( '@');
    	if (pos != -1) return false;
    	
    	// Email domain suffix (after '@' sign):
    	// Make sure domain has at least one period
    	pos = suffix.indexOf( '.');
    	if (pos <= 0) return false;
    	// Make sure domain doesn't have two periods back-to-back
    	pos = suffix.indexOf("..");
    	if (pos !=-1) return false;
    	// Make sure domain doesn't contain invalid chars
    	// (valid == alphanumeric, '.', and '-' only)
    	pos = suffix.indexOf('+');
    	if (pos != -1) return false;
    	pos = suffix.indexOf('_');
    	if (pos != -1) return false;
    	// Make sure last domain sub-suffix is between
    	// 2 and 4 characters long (.org, .info, .uk, etc.)
    	pos = email.lastIndexOf( '.');
    	int lastSuffixLen = email.length()-pos-1;
    	if (lastSuffixLen < 2 || lastSuffixLen > 4) return false;
    	
    	return true;
    }
    
    /**
     * Date of birth parsing rules:
     * <ul>
     * <li>Date must be in format MM/DD/YYYY or DD/MM/YYYY (the choice
     *   of which format is used is set by a .jad file parameter.)</li>
     * <li>The MM and DD part of the date must always be two characters,
     *   so day "7" must be entered as "07"</li>
     * <li>Birthdates before 1885 rejected (max known human age is 120).</li>
     * <li>Date must be a valid calendar date.  Leapyears (Feb. 29th on
     *   years divisible by 4, etc.) are accounted for.</li>
     * </ul>
     *   
     * @param dob Date of birth in string format, either: MM/DD/YYYY or 
     *   DD/MM/YYYY
     * @return Date instance if string parsed as a valid date, null
     *   otherwise 
     */
    public Date validateAge( String dob)
    {
    	int slash = dob.indexOf( DATE_SEPARATOR_CHAR);
    	if (slash == -1) return null;
    	String month = dob.substring(0, slash);
    	dob = dob.substring( slash+1);
    	slash = dob.indexOf( DATE_SEPARATOR_CHAR);
    	if (slash == -1) return null;
    	String day = dob.substring(0, slash);
    	String year = dob.substring(slash+1);
    	int d = -1, m = -1, y = -1;
    	try { 
    		d = Integer.parseInt( day);
    		m = Integer.parseInt( month);
    		y = Integer.parseInt( year);
    	} catch (Exception e) {
    		return null;
    	}
    	char dayChar = DATE_FORMAT.charAt(0);
    	if (dayChar=='D' || dayChar=='d') {
    		int tmp=m;  m=d;  d=tmp;
    	}
    	m -= 1; // J2ME represents months in range 0-11, not 1-12
    	// Reject outright users older than 120 years.
    	if (y<1885) return null;	
    	if (m<0 || m>11) return null;
    	// Check against valid days of the month
    	if (d<1 || d>daysInMonth[m]) {
        	// Adjust for leap years: "years divisible by four are leap years,
    		// with the exception of centurial leap years which are not divisible
    		// by 400 (e.g., 1700, 1800, 1900, 2100 are not leap years, and 
    		// 1600, 2000, and 2400 are.)"
    		if (!(m==1 && d==29 && y%4==0)) return null;
    		if (y%100==0 && y%400 != 0) return null;
    	}
    	Calendar birth = Calendar.getInstance();
    	birth.set( Calendar.YEAR, y);
    	birth.set( Calendar.MONTH, m);
    	birth.set( Calendar.DAY_OF_MONTH, d);
    	return birth.getTime();
    }
    
    /**
     * Receives a date, determines if a person born on that
     * date is at least <code>minAge</code> years old today.
     * 
     * @param dob Date of birth
     * @param minAge minimum years age
     * @return <code>true</code> if person born on date
     *   <code>dob</code> is at least <code>minAge</code>
     *   years old.
     */
    public boolean isOldEnough( Date dob, int minAge)
    {
    	Calendar time = Calendar.getInstance();
    	int curYear = time.get( Calendar.YEAR);
    	int curMonth = time.get( Calendar.MONTH);
    	int curDay = time.get( Calendar.DAY_OF_MONTH);
    	time.setTime( dob);
    	int dobYear = time.get( Calendar.YEAR);
    	if (curYear-dobYear > minAge) return true;
    	if (curYear-dobYear < minAge) return false;
    	int dobMonth = time.get( Calendar.MONTH);
    	if (curMonth > dobMonth) return true;
    	if (curMonth < dobMonth) return false;
    	int dobDay = time.get( Calendar.DAY_OF_MONTH);
    	if (curDay >= dobDay) return true;
    	if (curDay < dobDay) return false;
    	return false;
    }
    
    /**
     * If age and email validate, go on to Security Question
     * and Answer View.
     */
    public void leftSoftButtonPressed(String label) 
    {
    	if (validate()) {
			community.switchToView( Community.TERMS);    			
    	}
    }

    /**
     * Go back to Username/Password screen.
     */
    public void rightSoftButtonPressed(String label) {
        community.switchToView( Community.USER_PASS);
    }

}
