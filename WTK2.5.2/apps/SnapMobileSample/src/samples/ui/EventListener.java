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

package samples.ui;

/**
 * Callback interface for event notification. This
 * interface is used by the Component class to notify registered
 * listeners of events.  Listeners need to implement this interface
 * to be able to register themselves.
 *
 * @see Component#addEventListener
 * @see Component#removeEventListener
 * @see Event#Event
 */
public interface EventListener {
    /**
     * Instances of the Component class will call this method for
     * registered listeners each time new events are available.
     *
     * @param e A user interface event.
     * @returns true if event handled, false if not.
     */
    public boolean handleEvent(Event e);
}
