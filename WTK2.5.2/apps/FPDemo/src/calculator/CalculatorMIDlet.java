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
package calculator;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;


/**
 * The calculator demo is a simple floating point calculator
 * which powered by floating point support available in cldc1.1.
 *
 * @version
 */
public final class CalculatorMIDlet extends MIDlet implements CommandListener {
    /** The number of characters in numeric text field. */
    private static final int NUM_SIZE = 20;

    /** Soft button for exiting the game. */
    private final Command exitCmd = new Command("Exit", Command.EXIT, 2);

    /** Menu item for changing game levels. */
    private final Command calcCmd = new Command("Calc", Command.SCREEN, 1);

    /** A text field to keep the first argument. */
    private final TextField t1 = new TextField(null, "", NUM_SIZE, TextField.DECIMAL);

    /** A text field to keep the second argument. */
    private final TextField t2 = new TextField(null, "", NUM_SIZE, TextField.DECIMAL);

    /** A text field to keep the result of calculation. */
    private final TextField tr = new TextField("Result", "", NUM_SIZE, TextField.UNEDITABLE);

    /** A choice group with available operations. */
    private final ChoiceGroup cg =
        new ChoiceGroup("", ChoiceGroup.POPUP,
            new String[] { "add", "subtract", "multiply", "divide" }, null);

    /** An alert to be reused for different errors. */
    private final Alert alert = new Alert("Error", "", null, AlertType.ERROR);

    /** Indicates if the application is initialized. */
    private boolean isInitialized = false;

    /**
     * Creates the calculator view and action buttons.
     */
    protected void startApp() {
        if (isInitialized) {
            return;
        }

        Form f = new Form("FP Calculator");
        f.append(t1);
        f.append(cg);
        f.append(t2);
        f.append(tr);
        f.addCommand(exitCmd);
        f.addCommand(calcCmd);
        f.setCommandListener(this);
        Display.getDisplay(this).setCurrent(f);
        alert.addCommand(new Command("Back", Command.SCREEN, 1));
        isInitialized = true;
    }

    /**
     * Does nothing. Redefinition is required by MIDlet class.
     */
    protected void destroyApp(boolean unconditional) {
    }

    /**
     * Does nothing. Redefinition is required by MIDlet class.
     */
    protected void pauseApp() {
    }

    /**
     * Responds to commands issued on CalculatorForm.
     *
     * @param c command object source of action
     * @param d screen object containing the item the action was performed on.
     */
    public void commandAction(Command c, Displayable d) {
        if (c == exitCmd) {
            destroyApp(false);
            notifyDestroyed();

            return;
        }

        double res = 0.0;

        try {
            double n1 = getNumber(t1, "First");
            double n2 = getNumber(t2, "Second");

            switch (cg.getSelectedIndex()) {
            case 0:
                res = n1 + n2;

                break;

            case 1:
                res = n1 - n2;

                break;

            case 2:
                res = n1 * n2;

                break;

            case 3:
                res = n1 / n2;

                break;

            default:
            }
        } catch (NumberFormatException e) {
            return;
        } catch (ArithmeticException e) {
            alert.setString("Divide by zero.");
            Display.getDisplay(this).setCurrent(alert);

            return;
        }

        /*
         * The resulted string may exceed the text max size.
         * We need to correct last one then.
         */
        String res_str = Double.toString(res);

        if (res_str.length() > tr.getMaxSize()) {
            tr.setMaxSize(res_str.length());
        }

        tr.setString(res_str);
    }

    /**
     * Extracts the double number from text field.
     *
     * @param t the text field to be used.
     * @param type the string with argument number.
     * @throws NumberFormatException is case of wrong input.
     */
    private double getNumber(TextField t, String type)
        throws NumberFormatException {
        String s = t.getString();

        if (s.length() == 0) {
            alert.setString("No " + type + " Argument");
            Display.getDisplay(this).setCurrent(alert);
            throw new NumberFormatException();
        }

        double n;

        try {
            n = Double.parseDouble(s);
        } catch (NumberFormatException e) {
            alert.setString(type + " argument is out of range.");
            Display.getDisplay(this).setCurrent(alert);
            throw e;
        }

        return n;
    }
} // end of class 'CalculatorMIDlet' definition
