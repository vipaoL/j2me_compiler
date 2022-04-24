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
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 * @version
 */
public final class ObexDemoMain {
    /**
     * The X location where the last window was placed.
     */
    static int lastX;

    /**
     * The Y location where the last window was placed.
     */
    static int lastY;

    /**
     * Spacing between successively placed windows.
     */
    static final int stepX = 45;

    /**
     * Spacing between successively placed windows.
     */
    static final int stepY = 40;

    /**
     * Min values for the top and left edges of a window.
     */
    static int minX;

    /**
     * Min values for the top and left edges of a window.
     */
    static int minY;

    /**
     * Max values for the right and bottom edges of a window.
     */
    static int maxX;

    /**
     * Max values for the right and bottom edges of a window.
     */
    static int maxY;

    static {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int w = screen.width;
        int h = screen.height;

        /*
         * Min X, Y location should be close to the top-left corner
         * of the screen.
         */
        minX = 50;
        minY = 50;

        if (minX > w) { // Sanity -- in case we have a really small screen
            minX = w / 2;
        }

        if (minY > h) { // Sanity -- in case we have a really small screen
            minY = h / 2;
        }

        /*
         * Max X, Y location should around 4/5 of the screen size.
         * of the screen.
         */
        maxX = w - 50;
        maxY = h - 50;

        if (maxX > ((w * 4) / 5)) {
            maxX = (w * 4) / 5;
        }

        if (maxY > ((h * 4) / 5)) {
            maxY = (h * 4) / 5;
        }

        lastX = minX + (int)(Math.random() * (maxX - minX) * 0.8);
        lastY = minY + (int)(Math.random() * (maxY - minY) * 0.8);
    }

    private JFrame frame = null;

    /**
     * Placed a window on a "default" location on the screen. Successive
     * calls will place the given window at different locations so
     * that it won't overlap with a window placed by a previous call
     * this this method.
     */
    private static void place(JFrame window) {
        int x = lastX + stepX;
        int y = lastY + stepY;
        int w = window.getWidth();
        int h = window.getHeight();

        if ((x + w) > maxX) {
            x = minX;
        }

        if ((y + h) > maxY) {
            y = minY;
        }

        // In case of a *really large* device skin
        if ((minX + w) > maxX) {
            x = 5;
        }

        if ((minY + h) > maxY) {
            y = 5;
        }

        window.setLocation(x, y);
        lastX = x;
        lastY = y;
    }

    private void createComponents() {
        JButton senderStartButton = new JButton("Start Image Sender");
        JButton receiverStartButton = new JButton("Start Image Receiver");
        JButton exittButton = new JButton("Exit");
        senderStartButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    frame.hide();
                    new GUIImageSender(frame, frame.getLocation());
                }
            });
        receiverStartButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    frame.hide();

                    new GUIImageReceiver(frame, frame.getLocation());
                }
            });
        exittButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    frame.dispose();
                    frame = null;
                    System.exit(0);
                }
            });

        /*
         * An easy way to put space between a top-level container
         * and its contents is to put the contents in a JPanel
         * that has an "empty" border.
         */
        JPanel pane = new JPanel();
        pane.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        pane.setLayout(new GridLayout(0, 1));
        pane.add(senderStartButton);
        pane.add(receiverStartButton);
        pane.add(exittButton);

        //Create the top-level container and add contents to it.
        frame = new JFrame("J2SE OBEX Demo");
        frame.getContentPane().add(pane, BorderLayout.CENTER);

        //Finish setting up the frame, and show it.
        frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
        frame.pack();
        place(frame);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        ObexDemoMain app = new ObexDemoMain();
        app.createComponents();
    }
}
