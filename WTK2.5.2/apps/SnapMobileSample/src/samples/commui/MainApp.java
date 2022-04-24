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

import javax.microedition.midlet.MIDlet;

/**
 * Interface to be implemented by MIDlets that wish to make use of the 
 * <code>Community</code> functionality.
 *
 */
public interface MainApp {
	/**
	 Implementation should return a reference to the MIDlet here 
	*/
    public MIDlet getMIDlet();
    
    /**
    * Implementation should return a MIDlet property here, default source
    * of properties is the .jad file.
    * @param name Property name
    * @return Property, if found
    */
    public String getProperty(String name);
    
    /** 
     * Implementation should exit the MIDlet when this method is called.
     */
    public void exit();
}