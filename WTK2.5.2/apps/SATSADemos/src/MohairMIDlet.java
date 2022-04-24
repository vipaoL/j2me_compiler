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
import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.securityservice.*;


public class MohairMIDlet extends MIDlet implements CommandListener {
    private static String kQuerySlots = "Find slots";
    private static String kSignTest = "SATSA-PKI Sign test";
    Display mDisplay;
    List mMenu;
    Command mBackCommand;
    Command mExitCommand;

    public void startApp() {
        if (mDisplay == null) {
            mDisplay = Display.getDisplay(this);
        }

        if (mMenu == null) {
            mMenu = new List("MohairMIDlet", List.IMPLICIT);
            mMenu.append(kQuerySlots, null);
            mMenu.append(kSignTest, null);

            mBackCommand = new Command("Back", Command.BACK, 0);
            mExitCommand = new Command("Exit", Command.EXIT, 0);

            mMenu.addCommand(mExitCommand);
            mMenu.setCommandListener(this);
        }

        mDisplay.setCurrent(mMenu);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    public void commandAction(Command c, Displayable d) {
        if (c == mExitCommand) {
            destroyApp(true);
            notifyDestroyed();
        } else if ((c == List.SELECT_COMMAND) && (d == mMenu)) {
            int selection = mMenu.getSelectedIndex();
            String item = "[none]";

            if ((selection >= 0) && (selection < mMenu.size())) {
                item = mMenu.getString(selection);
            }

            if (item == kQuerySlots) {
                Form slotList = new Form("Slots");
                String slots = System.getProperty("microedition.smartcardslots");
                int index = 0;

                while (index < slots.length()) {
                    String slot;
                    int comma = slots.indexOf(',', index);

                    if (comma == -1) {
                        slot = slots.substring(index).trim();
                        index = slots.length();
                    } else {
                        slot = slots.substring(index, comma).trim();
                        index = comma + 1;
                    }

                    StringItem slotItem = new StringItem(null, slot);
                    slotItem.setLayout(Item.LAYOUT_2 | Item.LAYOUT_NEWLINE_AFTER);
                    slotList.append(slotItem);
                }

                slotList.addCommand(mBackCommand);
                slotList.setCommandListener(this);
                mDisplay.setCurrent(slotList);
            } else if (item == kSignTest) {
                System.out.println("Starting sign test...");

                /*
                try {
                  CMSMessageSignatureService.sign(
                    "This is the message to sign",
                    CMSMessageSignatureService.SIG_INCLUDE_CONTENT |
                        CMSMessageSignatureService.SIG_INCLUDE_CERTIFICATE,
                    null,
                    "This is the prompt");
                }
                catch (Exception e) {
                  System.out.println("sign() barfed: " + e);
                }*/
                new Thread() {
                        public void run() {
                            fromSpecification();
                            System.out.println("Sign test finished.");
                        }
                    }.start();
            }
        } else if (c == mBackCommand) {
            mDisplay.setCurrent(mMenu);
        }
    }

    private void fromSpecification() {
        // String caName = new String("cn=ca_name,ou=ou_name,o=org_name,c=ie");
        String caName = "c=US,st=CA,l=Santa Clara,o=dummy CA,ou=JCT,cn=thehost";
        String[] caNames = new String[1];
        String stringToSign = new String("JSR 177 Approved");
        String userPrompt =
            new String("Please insert the security element " + "issued by bank ABC" +
                "for the application XYZ.");
        byte[] byteArrayToSign = new byte[8];
        byte[] authSignature;
        byte[] signSignature;

        caNames[0] = caName;

        try {
            // Generate a formatted authentication signature that includes the
            // content that was signed in addition to the certificate.
            // Selection of the key is implicit in selection of the certificate, 
            // which is selected through the caNames parameter.
            // If the appropriate key is not found in any of the security 
            // elements present in the device, the implementation may guide 
            // the user to insert an alternative security element using 
            // the securityElementPrompt parameter.
            authSignature = CMSMessageSignatureService.authenticate(byteArrayToSign,
                    CMSMessageSignatureService.SIG_INCLUDE_CERTIFICATE |
                    CMSMessageSignatureService.SIG_INCLUDE_CONTENT, caNames, userPrompt);

            // Generate a formatted signature that includes the
            // content that was signed in addition to the certificate.
            // Selection of the key is implicit in selection of the certificate, 
            // which is selected through the caNames parameter.
            // If the appropriate key is not found in any of the 
            // security elements present in the device, the implementation 
            // may guide the user to insert an alternative
            // security element using the securityElementPrompt parameter.
            signSignature = CMSMessageSignatureService.sign(stringToSign,
                    CMSMessageSignatureService.SIG_INCLUDE_CERTIFICATE |
                    CMSMessageSignatureService.SIG_INCLUDE_CONTENT, caNames, userPrompt);
        } catch (IllegalArgumentException iae) {
            // Perform error handling
            iae.printStackTrace();
        } catch (CMSMessageSignatureServiceException ce) {
            if (ce.getReason() == ce.CRYPTO_FORMAT_ERROR) {
                System.out.println("Error formatting signature.");
            } else {
                System.out.println(ce.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Other exception: " + e);
        }
    }
}
