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

import java.io.*;

import java.util.*;

import javax.microedition.lcdui.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;
import javax.microedition.midlet.*;


/**
 * Utility functions and listener interfaces
 *
 * @version 1.9
 */
public class Utils {
    /** DEBUG = true to produce debugging output. */
    public static boolean DEBUG = false;

    /**
     * Splits the URL in the parts.
     * E.g: http://www.12fb.com:80/Media/MIDI/fb.mid#1
     *
     * 0: protocol (e.g. http)
     * 1: host (e.g. www.12fb.com)
     * 2: port (e.g. 80)
     * 3: path (e.g. /Media/MIDI)
     * 4: file (e.g. fb.mid)
     * 5: anchor (e.g. 1)
     *
     * LIMITATION: URL must end with a slash if it is a directory
     * @param url the URL to parse
     * @return an array of strings parsed from the URL
     */
    public static String[] splitURL(String url) throws Exception {
        StringBuffer u = new StringBuffer(url);
        String[] result = new String[6];

        for (int i = 0; i <= 5; i++) {
            result[i] = "";
        }

        // get protocol
        boolean protFound = false;
        int index = url.indexOf(":");

        if (index > 0) {
            result[0] = url.substring(0, index);
            u.delete(0, index + 1);
            protFound = true;
        } else if (index == 0) {
            throw new Exception("url format error - protocol");
        }

        // check for host/port
        if ((u.length() > 2) && (u.charAt(0) == '/') && (u.charAt(1) == '/')) {
            // found domain part
            u.delete(0, 2);

            int slash = u.toString().indexOf('/');

            if (slash < 0) {
                slash = u.length();
            }

            int colon = u.toString().indexOf(':');
            int endIndex = slash;

            if (colon >= 0) {
                if (colon > slash) {
                    throw new Exception("url format error - port");
                }

                endIndex = colon;
                result[2] = u.toString().substring(colon + 1, slash);
            }

            result[1] = u.toString().substring(0, endIndex);
            u.delete(0, slash);
        }

        // get filename
        if (u.length() > 0) {
            url = u.toString();

            int slash = url.lastIndexOf('/');

            if (slash > 0) {
                result[3] = url.substring(0, slash);
            }

            if (slash < (url.length() - 1)) {
                String fn = url.substring(slash + 1, url.length());
                int anchorIndex = fn.indexOf("#");

                if (anchorIndex >= 0) {
                    result[4] = fn.substring(0, anchorIndex);
                    result[5] = fn.substring(anchorIndex + 1);
                } else {
                    result[4] = fn;
                }
            }
        }

        return result;
    }

    /**
     * Concatenate parts of url into a single string.
     * @param url parsed URL parts
     * @return a url
     */
    public static String mergeURL(String[] url) {
        return ((url[0] == "") ? "" : (url[0] + ":/")) + ((url[1] == "") ? "" : ("/" + url[1])) +
        ((url[2] == "") ? "" : (":" + url[2])) + url[3] + "/" + url[4] +
        ((url[5] == "") ? "" : ("#" + url[5]));
    }

    /**
     * Guess the content type from the suffixes int a url.
     * @param url a URL to parse
     * @return a content type
     */
    public static String guessContentType(String url) throws Exception {
        // guess content type
        String[] sURL = splitURL(url);
        String ext = "";
        String ct = "";
        int lastDot = sURL[4].lastIndexOf('.');

        if (lastDot >= 0) {
            ext = sURL[4].substring(lastDot + 1).toLowerCase();
        }

        if (ext.equals("mpg") || url.equals("avi")) {
            ct = "video/mpeg";
        } else if (ext.equals("mid") || ext.equals("kar")) {
            ct = "audio/midi";
        } else if (ext.equals("wav")) {
            ct = "audio/x-wav";
        } else if (ext.equals("txt")) {
            ct = "audio/x-txt";
        }

        return ct;
    }
}
