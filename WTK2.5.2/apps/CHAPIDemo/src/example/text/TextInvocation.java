/*
 *
 * Copyright (c) 2007, Sun Microsystems, Inc.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of Sun Microsystems nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package example.text;

import java.io.IOException;

import java.lang.Thread;

import javax.microedition.content.*;


/**
 * Handler for Invocations for the TextViewer.
 * It runs in a separate Thread to handle Invocations.
 */
public class TextInvocation extends Thread {
    /** The ContentHandler we are processing. */
    ContentHandlerServer handler;

    /** The current invocation. */
    private Invocation invoc;

    /** The previous invocation. */
    private Invocation prevInvoc;

    /** The TextViewer to notify of requests and state changes. */
    private TextViewer viewer;

    /**
     * Set to <code>true</code> to indicate stop processing
     * as soon as possible.
     */
    private boolean stopping;

    /** The registry for access to invocations and responses. */
    Registry registry;

    TextInvocation(TextViewer viewer, Invocation initialInvoc, Invocation previousInvoc) {
        this(viewer, initialInvoc);
        prevInvoc = previousInvoc;
    }

    /**
     * Create a new TextInvocation handler with an initial
     * Invocation, (may be null).
     * @param viewer the TextViewer to notify of events.
     * @param initialInvoc an initial Invocation to process
     */
    TextInvocation(TextViewer viewer, Invocation initialInvoc) {
        invoc = initialInvoc;
        this.viewer = viewer;

        // Get a reference to the registry
        registry = Registry.getRegistry(viewer.getClass().getName());

        // And to the content handler
        try {
            handler = Registry.getServer(viewer.getClass().getName());
        } catch (ContentHandlerException che) {
            // Registration is missing
        }
    }

    /**
     * Stop processing of Invocation requests as soon as possible.
     */
    void stop() {
        stopping = true;
    }

    /**
     * Get the current active Invocation.
     * @return the current Invocation
     */
    Invocation currentInvocation() {
        return invoc;
    }

    /**
     * Mark the current Invocation as complete with the status indicated.
     * @param invocation the Invocation to mark complete
     * @param status the new status to return
     * @return true if the application should exit
     */
    boolean done(Invocation invocation, int status) {
        if (invoc == invocation) {
            invoc = null;
        }

        return handler.finish(invocation, status);
    }

    /**
     * Run method to continually check for invocations and service them.
     * If the invocation is brand new; just invoke it and return.
     * If a new invocation arrives while an Invocation is current,
     * the current invocation is CANCELLED and the new one processed.
     */
    public void run() {
        do {
            // If there is an Invocation process it.
            if (invoc != null) {
                if (invoc.getStatus() == Invocation.INIT) {
                    invokeAsNew();

                    // Return to terminate the thread
                    return;
                } else {
                    process();
                }
            }

            /*
             * Dequeue the next invocation
             * Loop back to the top of this block to process.
             */
            Invocation inv = null;

            while (!stopping && (inv == null)) {
                // Get the next real Invocation
                inv = handler.getRequest(true);
            }

            invoc = inv;
        } while (!stopping);

        // CANCEL any pending invocation
        if ((invoc != null) && (invoc.getStatus() == Invocation.ACTIVE)) {
            handler.finish(invoc, Invocation.CANCELLED);
        }
    }

    /**
     * Process the current invocation according to its STATUS.
     */
    private void process() {
        int status = invoc.getStatus();

        switch (status) {
        case Invocation.ACTIVE:
            // read the text and setup the form
            viewer.show(invoc);

            break;

        case Invocation.INIT:
            break;
        }
    }

    /**
     * Called by the response handler when the response to a invocation arrives.
     */
    void finishedInvocation(Invocation invocation) {
        // If there was a previous Invocation, make it current again
        invoc = invocation.getPrevious();
    }

    /**
     * The invocation needs to be Invoked.
     */
    private void invokeAsNew() {
        try {
            // Do the invocation and terminate the MIDlet if required
            if (registry.invoke(invoc, prevInvoc)) {
                viewer.destroyApp(true);
                viewer.notifyDestroyed();
            }
        } catch (ContentHandlerException cex) {
            viewer.showAlert("Content handler not available for " + invoc.getURL());
        } catch (Exception ioe) {
            ioe.printStackTrace();
            viewer.showAlert("Resource not available: " + invoc.getURL());
        }
    }
}
