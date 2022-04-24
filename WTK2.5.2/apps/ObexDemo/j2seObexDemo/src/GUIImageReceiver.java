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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;


/**
 * @version
 */
final class GUIImageReceiver {
    private JFrame frame = null;
    private boolean downloading = false;
    private JLabel label = null;
    private JProgressBar progressBar = null;
    private JButton receiveButton = null;
    private JButton cancelButton = null;
    private byte[] imageData = null;
    private ObexImageReceiver obexReceiver = null;

    GUIImageReceiver(final JFrame parent, Point initLocation) {
        // Creates button which start and stop receiving process.
        receiveButton = new JButton("Receive Image");
        receiveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (downloading) {
                        obexReceiver.stop(true);
                        showImage(null);
                    } else {
                        showWaiting();

                        obexReceiver = new ObexImageReceiver(GUIImageReceiver.this);
                        (new Thread(obexReceiver)).start();
                    }
                }
            });

        // Creates cancel button.
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (obexReceiver != null) {
                        obexReceiver.stop(true);
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
        buttonPane.add(receiveButton, BorderLayout.EAST);
        buttonPane.add(cancelButton, BorderLayout.WEST);

        // Create progress bar and put it into special progress pane
        progressBar = new JProgressBar(0, 8);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        JPanel progressPane = new JPanel();
        progressPane.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 8));
        progressPane.setLayout(new BorderLayout());
        progressPane.add(progressBar, BorderLayout.CENTER);

        // Create "south" pane and put into it progress pane and button pane
        JPanel southPane = new JPanel();
        southPane.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        southPane.setLayout(new BorderLayout());
        southPane.add(progressPane, BorderLayout.CENTER);
        southPane.add(buttonPane, BorderLayout.SOUTH);

        // Create top level pane
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());

        // Create the label to show images and put it in a scroll pane
        label = new JLabel();
        label.setPreferredSize(new Dimension(192, 192));
        contentPane.add(new JScrollPane(label), BorderLayout.CENTER);

        // Put "south" pane into top level pane
        contentPane.add(southPane, BorderLayout.SOUTH);

        //Create the top-level container and add contents to it.
        frame = new JFrame("Image Receiver");
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

    /** Ascs user to download image. */
    boolean askPermission(String imageName, int imageLength) {
        int download =
            JOptionPane.showConfirmDialog(frame,
                "Incoming image:\nName   = " + imageName + "\nLength = " + imageLength +
                "\nWould you like to receive it ?", "Connected!", JOptionPane.YES_NO_OPTION);

        if (download == JOptionPane.YES_OPTION) {
            return true;
        }

        return false;
    }

    /**
     * Shows empty image for connection waiting.
     */
    void showWaiting() {
        synchronized (this) {
            downloading = true;
        }

        label.setIcon(null);
        progressBar.setEnabled(true);
        progressBar.setValue(progressBar.getMinimum());
        receiveButton.setText("Stop Waiting");
    }

    /**
     * Shows progress of image downloading
     */
    void showProgress(int maxValue) {
        synchronized (this) {
            downloading = true;
        }

        label.setIcon(null);
        progressBar.setEnabled(true);
        progressBar.setValue(0);
        progressBar.setMaximum(maxValue);
        receiveButton.setText("Stop Receiving");
    }

    /**
     * Update progress of image downloading
     */
    void updateProgress(int value) {
        progressBar.setValue(value);
    }

    /**
     * Shows downloaded image.
     */
    void showImage(byte[] imageData) {
        synchronized (this) {
            downloading = false;
        }

        label.setIcon(createImage(imageData));
        progressBar.setEnabled(false);
        progressBar.setValue(progressBar.getMinimum());
        receiveButton.setText("Receive Image");
    }

    /**
     * Shows "Can not connect..." message box.
     */
    void canNotConnectMessage() {
        JOptionPane.showMessageDialog(frame, "Can not connect to any sender", "Warning",
            JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Shows message box that downloading is stopped.
     */
    void stopMessage() {
        JOptionPane.showMessageDialog(frame, "Sender stopped image uploading", "Warning",
            JOptionPane.WARNING_MESSAGE);
    }

    private static Icon createImage(byte[] imageData) {
        if (imageData == null) {
            return null;
        }

        return new ImageIcon(imageData);
    }
}
