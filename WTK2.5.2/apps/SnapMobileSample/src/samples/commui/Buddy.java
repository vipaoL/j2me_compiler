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

/**
 * Buddy class for chat functionality.
 */
public class Buddy {
    private String name;
    private String message;
    private int status;
    private Integer gcid;
    public static final int OFFLINE = 0;
    public static final int ONLINE_AVAILABLE = 1;
    public static final int ONLINE_UNAVAILABLE = 2;

    /**
     * constructor
     * @param name Buddy Name
     * @param available Availability status
     */
    public Buddy(String name, int status) {
        //System.out.println("Buddy.Buddy(), name = " + name + ", available = " + available);

        this.name = name;
        this.status = status;
        this.gcid = null;
   }

    /**
     * constructor
     * @param name Buddy Name
     * @param available Availability status
     */
    public Buddy(String name, int status, Integer gcid) {
        //System.out.println("Buddy.Buddy(), name = " + name + ", available = " + available);

        this.name = name;
        this.status = status;
        this.gcid = gcid;
   }

    public void setMessage(String message) {
        //System.out.println("Buddy.setMessage(): " + message);
        this.message = message;
    }

    /**
     * Gets Message from Buddy
     * @return message String
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets name of the Buddy
     * @return Buddy name
     */
    public String getName() {
        return name;
    }

    /**
     * Set Availability status
     * @param available
     */
    public void setStatus(int status) {
        //System.out.println("Buddy.setAvailable(): " + available);
        this.status = status;
    }

    /**
     * Set Game Class ID
     * @param available
     */
    public void setGcid(Integer gcid) {
        //System.out.println("Buddy.setAvailable(): " + available);
        this.gcid = gcid;
    }

    public int getStatus() {
        return status;
    }

    public Integer getGcid() {
        return gcid;
    }

    /**
     * check if Buddy is available
     * @return available is Buddy is available
     */
    public boolean isAvailable() {
        boolean b = (status == ONLINE_AVAILABLE) ? true : false;
//      System.out.println(this + "  Buddy.isAvailable(): " + b);
        return b;
    }

    public String toString() {
        return "buddy '" + name + "': status = " + status;
    }
}
