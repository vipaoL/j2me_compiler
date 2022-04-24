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
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;


/**
 * @version
 */
final class GUIImageSender {
    private JFrame frame = null;
    private boolean uploading = false;
    private JLabel label = null;
    private JList list = null;
    private JProgressBar progressBar = null;
    private JButton sendButton = null;
    private JButton cancelButton = null;
    private Vector imageNames = null;
    private ObexImageSender obexSender = null;

    GUIImageSender(final JFrame parent, Point initLocation) {
        // Creates button which start and stop sending image process.
        sendButton = new JButton("Send Image");
        sendButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (uploading) {
                        obexSender.stop();
                        showImageList();
                    } else {
                        int imageIndex = list.getSelectedIndex();
                        String imageName = (String)imageNames.elementAt(imageIndex);

                        obexSender = new ObexImageSender(GUIImageSender.this);
                        obexSender.setImageName(imageName);
                        (new Thread(obexSender)).start();
                    }
                }
            });

        // Creates cancel button.
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (obexSender != null) {
                        obexSender.stop();
                    }

                    Point location = frame.getLocation();
                    frame.dispose();
                    parent.setLocation(location.x, location.y);
                    parent.show();
                }
            });

        // Creates buttons pane
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BorderLayout());
        buttonPane.add(sendButton, BorderLayout.EAST);
        buttonPane.add(cancelButton, BorderLayout.WEST);

        // Create progress bar and put it into special progress pane
        progressBar = new JProgressBar(0, 8);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        JPanel progressPane = new JPanel();
        progressPane.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 8));
        progressPane.setLayout(new BorderLayout());
        progressPane.add(progressBar, BorderLayout.CENTER);

        // Created label status string
        label = new JLabel();

        // Create "south" pane and put into it progress pane, label and button
        JPanel southPane = new JPanel();
        southPane.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        southPane.setLayout(new BorderLayout());
        southPane.add(label, BorderLayout.NORTH);
        southPane.add(progressPane, BorderLayout.CENTER);
        southPane.add(buttonPane, BorderLayout.SOUTH);

        // Create top level pane
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());

        // Create the list of images and put it in a scroll pane
        setupImageList();
        contentPane.add(new JScrollPane(list), BorderLayout.CENTER);
        contentPane.add(southPane, BorderLayout.SOUTH);

        // initialize selection mode
        showImageList();

        //Create the top-level container and add contents to it.
        frame = new JFrame("Image Sender");
        frame.getContentPane().add(contentPane, BorderLayout.CENTER);
        frame.setLocation(initLocation.x, initLocation.y);

        //Finish setting up the frame, and show it.
        frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    frame.dispose();
                    parent.dispose();
                    System.exit(0);
                }
            });
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Shows progress of image uploading
     */
    void showProgress(String message, int maxValue) {
        synchronized (this) {
            uploading = true;
        }

        list.setEnabled(false);
        progressBar.setEnabled(true);
        progressBar.setValue(0);
        progressBar.setMaximum(maxValue);
        sendButton.setText("Stop Sending");
        label.setText(message);
    }

    /**
     * Update progress of image uploading
     */
    void updateProgress(int value) {
        progressBar.setValue(value);
    }

    /**
     * Shows list with image names to select one  for sending to receiver
     */
    void showImageList() {
        synchronized (this) {
            uploading = false;
        }

        list.setEnabled(true);
        progressBar.setEnabled(false);
        progressBar.setValue(progressBar.getMinimum());
        sendButton.setText("Send Image");
        label.setText("Select image for sending");
    }

    /**
     * Shows error message box
     */
    void errorMessage() {
        JOptionPane.showMessageDialog(frame, "Can't read the image", "Error",
            JOptionPane.ERROR_MESSAGE);
        showImageList();
    }

    /**
     * Shows "not ready" message box
     */
    void notReadyMessage() {
        JOptionPane.showMessageDialog(frame, "Receiver isn't ready to download image", "Warning",
            JOptionPane.WARNING_MESSAGE);
        showImageList();
    }

    /**
     * Shows message box that uploading is stopped.
     */
    void stopMessage() {
        JOptionPane.showMessageDialog(frame, "Receiver terminated image loading", "Warning",
            JOptionPane.WARNING_MESSAGE);
        showImageList();
    }

    private static Vector parseList(String theStringList) {
        Vector v = new Vector(10);
        StringTokenizer tokenizer = new StringTokenizer(theStringList, " ");

        while (tokenizer.hasMoreTokens()) {
            String image = tokenizer.nextToken();
            v.addElement(image);
        }

        return v;
    }

    private void setupImageList() {
        ResourceBundle imageResource = ResourceBundle.getBundle("imagenames");
        String imageNamesString = imageResource.getString("imageNames");
        imageNames = parseList(imageNamesString);
        list = new JList(imageNames);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
    }
}
