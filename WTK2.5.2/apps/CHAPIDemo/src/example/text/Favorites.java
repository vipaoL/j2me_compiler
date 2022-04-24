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
import javax.microedition.midlet.*;
import javax.microedition.rms.*;


/**
 * A Form to handle a Favorites list.  The favorites are saved
 * and restored from a RecordStore.
 *
 */
public class Favorites extends Form implements CommandListener, ItemCommandListener {
    /** Command to enter new Favorite. */
    private Command goCommand = new Command("Go", Command.ITEM, 1);

    /** Command to save the new Favorite. */
    private Command saveCommand = new Command("Save", Command.ITEM, 1);

    /** Command to delete a favorite. */
    private Command delCommand = new Command("Delete", Command.ITEM, 2);

    /** Command to create a new a favorite. */
    private Command newCommand = new Command("New", Command.ITEM, 2);

    /** When selected the "NEW" item sets up to enter a new title, url. */
    private StringItem newItem;

    /** the TextField for the title */
    private TextField titleItem;

    /** The TextField for the URL. */
    private TextField urlItem;

    /** The commandListener that gets notified of select events. */
    private CommandListener commandListener;

    /** The ItemCommandListener that gets notified of select events. */
    private ItemCommandListener itemCommandListener;

    /** The vector of urls that match the titles in the sequence of items. */
    private Vector urls;
    private boolean newFavorite = false;

    /**
     * Construct a new Favorites Form.
     * @param title the title
     */
    public Favorites(String title) {
        super(title);
        urls = new Vector();

        // Setup the initial "New" item
        newItem = new StringItem(null, "[New]", Item.PLAIN);
        newItem.setLayout(Item.LAYOUT_2 | Item.LAYOUT_NEWLINE_AFTER);
        newItem.addCommand(newCommand);
        newItem.setDefaultCommand(newCommand);
        newItem.setItemCommandListener(this);
        this.append(newItem);
        urls.addElement("<empty>");

        titleItem = new TextField("Title:", "", 30, TextField.INITIAL_CAPS_WORD);
        titleItem.setLayout(Item.LAYOUT_2 | Item.LAYOUT_NEWLINE_AFTER);
        titleItem.addCommand(saveCommand);
        titleItem.setItemCommandListener(this);
        urlItem = new TextField("Link:", "http://", 128, TextField.URL);
        urlItem.setLayout(Item.LAYOUT_2 | Item.LAYOUT_NEWLINE_AFTER);
        urlItem.addCommand(saveCommand);
        urlItem.setItemCommandListener(this);

        setCommandListener(this);
        readPrefs();
    }

    /**
     * Interpose on the commandListener.  This Form will receive
     * all commands from LCDUI and redispatch to the commandListener
     * registered.
     * @param listener the CommandListener the listener to interpose on
     */
    public void setCommandListener(CommandListener listener) {
        commandListener = listener;
        super.setCommandListener(this);
    }

    /**
     * Interpose on the itemCommandListener.  This Form will receive
     * all item commands from LCDUI and redispatch to the
     * ItemCommandListener registered.
     * @param itemListener the ItemCommandListener to interpose on
     */
    public void setItemCommandListener(ItemCommandListener itemListener) {
        itemCommandListener = itemListener;
    }

    /**
     * Handle commands selected by the user. Any commands not
     * recognized as being handled by this form are dispatched to
     * the registered command listener.
     * @param c the command
     * @param s the Displayable
     */
    public void commandAction(Command c, Displayable s) {
        if (commandListener != null) {
            commandListener.commandAction(c, s);
        }
    }

    /**
     * Called for Commands on Items.
     * @param c the Command invoked
     * @param item the item it was invoked on
     */
    public void commandAction(Command c, Item item) {
        try {
            if (c == saveCommand) {
                String title = titleItem.getString();
                String url = urlItem.getString();
                this.delete(itemIndex(titleItem));
                this.delete(itemIndex(urlItem));
                this.insert(0, newItem);

                url = encodeURL(url);
                addFavorite(title, url);
                newFavorite = false;
                savePrefs();
            } else if (((c == List.SELECT_COMMAND) || (c == newCommand)) && (item == newItem)) {
                this.delete(0);
                this.insert(0, titleItem);
                this.insert(1, urlItem);
                newFavorite = true;
            } else if (c == goCommand) {
                // Switch to SELECT command and redispatch
                if (itemCommandListener != null) {
                    itemCommandListener.commandAction(List.SELECT_COMMAND, item);
                }
            } else if (c == delCommand) {
                // Delete the current item
                int index = itemIndex(item);
                this.delete(index);

                if (newFavorite) {
                    urls.removeElementAt(index - 1);
                } else {
                    urls.removeElementAt(index);
                }

                savePrefs();
            } else {
                if (itemCommandListener != null) {
                    itemCommandListener.commandAction(c, item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a new Hyperlink StringItem with the go and delete commands.
     * @param string a string to add to the list
     * @param url of the resource to match the string
     */
    public void addFavorite(String string, String url) {
        StringItem si = new StringItem(null, string, Item.HYPERLINK);
        si.setLayout(Item.LAYOUT_2 | Item.LAYOUT_NEWLINE_AFTER);
        urls.addElement(url);
        si.setDefaultCommand(goCommand);
        si.addCommand(delCommand);
        si.setItemCommandListener(this);
        this.insert(this.size(), si);
    }

    /**
     * Get the title associated with the item.
     * @param item for which to get the title
     * @return the title at index
     */
    public String getTitle(Item item) {
        int index = itemIndex(item);

        return (index < 0) ? null : getTitle(index);
    }

    /**
     * Get the url associated with the item.
     * @param item for which to get the URL
     * @return the URL at index
     */
    public String getURL(Item item) {
        int index = itemIndex(item);

        return (index < 0) ? null : getURL(index);
    }

    /**
     * Get the title associated with the item.
     * @param index for which to get the title
     * @return the title at index
     */
    public String getTitle(int index) {
        return ((StringItem)this.get(index)).getText();
    }

    /**
     * Get the url associated with the item.
     * @param index for which to get the URL
     * @return the URL at index
     */
    public String getURL(int index) {
        return (String)urls.elementAt(newFavorite ? (index - 1) : index);
    }

    /**
     * Find the index of the item in the form.
     * @param item to find the index of
     * @return the index of the item or -1 if not found.
     */
    private int itemIndex(Item item) {
        int i = 0;

        for (i = this.size() - 1; i >= 0; i--) {
            if (this.get(i) == item) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Encode a typed URL.  Specifically, convert spaces to %20
     * @param string the string to encode.
     * @return the string encoded as a URL.
     */
    private String encodeURL(String string) {
        if (string.indexOf(' ') >= 0) {
            StringBuffer sb = new StringBuffer(string.length() + 10);
            int offset = 0;
            int ndx = 0;

            while ((ndx = string.indexOf(' ', offset)) >= 0) {
                sb.append(string.substring(offset, ndx));
                sb.append("%20");
                offset = ndx + 1;
            }

            sb.append(string.substring(offset, string.length()));
            string = sb.toString();
        }

        return string;
    }

    /**
     * Restore the title preference from last time.
     */
    private void readPrefs() {
        RecordStore store = null;

        try {
            store = RecordStore.openRecordStore("Favorites", true);

            byte[] record = store.getRecord(1);
            ByteArrayInputStream is = new ByteArrayInputStream(record);
            DataInputStream in = new DataInputStream(is);

            try {
                int size = in.readInt(); // number of pairs

                for (int i = 0; i < size; i++) {
                    String title = in.readUTF();
                    String url = in.readUTF();
                    addFavorite(title, url);
                }
            } catch (IOException ioe) {
            }
        } catch (RecordStoreException rms) {
        } finally {
            if (store != null) {
                try {
                    store.closeRecordStore();
                } catch (Exception e) {
                    // Ignore
                }

                store = null;
            }
        }
    }

    /**
     * Save the title preference.
     */
    private void savePrefs() {
        RecordStore store = null;

        try {
            store = RecordStore.openRecordStore("Favorites", true);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(os);

            // Write out titles and urls alternately
            try {
                int fav = 0;

                if (newFavorite) {
                    out.writeInt(this.size() - 2); // 2 additional fields(new)
                    fav = 2;
                } else {
                    out.writeInt(this.size() - 1); // number of elements
                    fav = 1;
                }

                for (int i = fav; i < this.size(); i++) {
                    out.writeUTF(getTitle(i));
                    out.writeUTF(getURL(i));
                }

                out.close();
            } catch (IOException ioe) {
            }

            byte[] ba = os.toByteArray();

            int id = store.getNextRecordID();

            if (id == 1) {
                store.addRecord(ba, 0, ba.length);
            } else {
                store.setRecord(1, ba, 0, ba.length);
            }
        } catch (RecordStoreException rms) {
        } finally {
            if (store != null) {
                try {
                    store.closeRecordStore();
                } catch (Exception e) {
                    // Ignore
                }

                store = null;
            }
        }
    }
}
