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
package example.mmademo;

import javax.microedition.content.*;


/**
 * The handler for ContentHandler requests.
 *
 */
public class VideoHandler implements Runnable {
    /** The invoking VideoAlbum midlet for context. */
    VideoTest videotest;

    /** The Handler being serviced. */
    ContentHandlerServer handler;

    /** The current Invocation being processed. */
    Invocation request;

    /** True if this handler should be stopping. */
    boolean stopping;

    /**
     * Create a new instance of VideoHandler and start the thread
     * to service it.
     */
    public VideoHandler(VideoTest videotest) {
        this.videotest = videotest;

        try {
            handler = Registry.getServer(videotest.getClass().getName());
        } catch (ContentHandlerException che) {
            // handle exception
        }

        // check for a pending request;
        request = handler.getRequest(false);
        new Thread(this).start();
    }

    /**
     * Return the current invocation.
     * @return the current Invocation.
     */
    public Invocation current() {
        return request;
    }

    /**
     * Stop this handler as soon a possible.
     * Any current request will be cancelled.
     */
    public void stop() {
        stopping = true;
    }

    /**
     * Handle incoming requests.
     */
    public void run() {
        do {
            if (request != null) {
                videotest.newRequest(request.getURL());
            }

            // Look for a new request
            Invocation inv = null;

            while (!stopping && (inv == null)) {
                inv = handler.getRequest(true);
            }

            // Have new request; need to finish the previous one, if...
            if (request != null) {
                // If previous invocation was not finished; cancel it
                if (handler.finish(request, Invocation.CANCELLED)) {
                    // Must exit to deliver the response
                    videotest.destroyApp(false);
                    videotest.notifyDestroyed();
                }

                request = null;
            }

            // Make the new invocation current and notify the VideoTest
            request = inv;
        } while (!stopping);
    }

    /**
     * Finish the current invocation.
     */
    public void finish(boolean success) {
        if (request != null) {
            if (handler.finish(request, success ? Invocation.OK : Invocation.CANCELLED)) {
                // Must exit to deliver the response
                videotest.destroyApp(false);
                videotest.notifyDestroyed();
            }

            request = null;
        }
    }
}
