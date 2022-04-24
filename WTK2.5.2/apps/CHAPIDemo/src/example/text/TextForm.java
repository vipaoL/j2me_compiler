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

import java.io.*;

import java.util.*;

import javax.microedition.content.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.List;


/**
 * A text viewer; the contents of the URL are read and displayed
 * in a form.  The text is scanned for patterns that match HTTP
 * URLs.  For example, "http://host/index.html".  The non=URL
 * text is inserted in the form as Strings, the URL text is
 * inserted as Hyperlinks and the default SELECT command is
 * associated with the URL items.  The itemCommandListener
 * is the TextViewer which will retrieve the URL and invoke it.
 * <p>
 * This class handles fetching the text from the URL and all
 * of the parsing operations.
 */
public class TextForm extends Form {
    /** The TextViewer to for reporting events and commands. */
    private TextViewer viewer;

    /** Terminators of a URL string; start is "http:" */
    private final String terminators = " <>|{}^[]\n";

    /** Command to open a link target. */
    private Command goCommand = new Command("Go", Command.ITEM, 1);

    /**
     * Create a new Text viewer and set the display.
     * Check for a new Invocation; if there is one pending
     * skip the splash screen.
     */
    TextForm(TextViewer viewer, String title) {
        super(title);
        this.viewer = viewer;
    }

    /**
     * Display the contents of the URL in the Form.
     * The stream is read into a StringBuffer.  It is scanned
     * for embedded http URLs. When a URL is found, the text
     * before the URL is entered as a StringItem.
     * The URL string is entered as a separate StringItem with
     * the attribute for URL.
     */
    void displayText(Invocation invoc) {
        // Delete the form contents
        this.deleteAll();

        String url = null;

        if ((invoc == null) || ((url = invoc.getURL()) == null)) {
            this.append("<no content to display>");

            return;
        }

        HttpConnection conn = null;
        InputStream input = null;
        StringBuffer b;

        try {
            long len = 0;

            conn = (HttpConnection)invoc.open(false);
            conn.setRequestMethod(HttpConnection.GET);

            int status = conn.getResponseCode();

            if (status != conn.HTTP_OK) {
                this.append(conn.getResponseMessage() + " (" + conn.getResponseCode() + ")");

                return;
            }

            // Download the content of the URL. We limit our download
            // to form.getMaxSize()), as most small
            // devices may not be able to handler larger size.
            //
            // A "real program", of course, needs to handle large
            // downloads intelligently. If possible, it should work
            // with the server to limit downloads to small sizes. If
            // this is not possible, it should download only part of
            // the data and allow the user to specify which part to
            // download.
            input = conn.openInputStream();
            len = conn.getLength();

            int max = 4096;
            b = new StringBuffer((len >= 0) ? (int)len : max);

            // Read content-Length bytes, or until max is reached.
            int ch = 0;
            len = 0;

            while ((ch = input.read()) != -1) {
                if (ch <= ' ') {
                    ch = ' ';
                }

                b.append((char)ch);

                if (b.length() >= max) {
                    break;
                }
            }

            // Process the buffer and insert text and links into the Form
            String s = b.toString();
            int offset = process(s);

            if (offset < s.length()) {
                s = s.substring(offset);
                this.append(new StringItem(null, s, Item.PLAIN));
            }
        } catch (OutOfMemoryError mem) {
            // Mmm, we still run out of memory, even after setting
            // max download to 4096 bytes. Tell user about the error.
            //
            // A "real program" should decide on the max download
            // size depending on available heap space, or perhaps
            // allow the user to set the max size
            b = null;
            mem.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (input != null) {
                    input.close();
                }

                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    /**
     * Process the StringBuffer entering text and URLs
     * into the Form.
     * The URL must begin with "http://" and end with a terminator.
     * @param string a String to process
     * @return the index of the next unprocessed character in the
     *  string
     */
    private int process(String string) {
        String s = null;
        StringItem item;
        int offset = 0;
        int ndx = 0;

        while ((ndx = string.indexOf("http:", offset)) >= 0) {
            if (ndx > 0) {
                // Insert the string up to the URL
                s = string.substring(offset, ndx);
                item = new StringItem(null, s, Item.PLAIN);
                item.setLayout(Item.LAYOUT_2);
                this.append(item);
                offset = ndx;
            }

            // Find the end of the url and insert it.
            offset = ndx;
            ndx = indexOfDelim(string, offset, terminators);

            if (ndx >= 0) {
                s = string.substring(offset, ndx);
                item = new StringItem(null, s, Item.HYPERLINK);
                item.setLayout(Item.LAYOUT_2 | Item.LAYOUT_NEWLINE_AFTER);
                item.setDefaultCommand(goCommand);
                item.setItemCommandListener(viewer);
                this.append(item);

                offset = ndx;
            } else {
                // End of URL not found, return until there's more input.
                break;
            }
        }

        return offset;
    }

    /**
     * Locate the index of the next terminator character.
     * @param string to scan
     * @param offset to start
     * @param terminators characters to stop at
     * @return index of first terminator or -1 if none before end of string.
     */
    private int indexOfDelim(String string, int offset, String terminators)
        throws StringIndexOutOfBoundsException {
        for (; offset < string.length(); offset++) {
            char ch = string.charAt(offset);

            if (terminators.indexOf(ch) >= 0) {
                return offset;
            }
        }

        return -1;
    }
}
