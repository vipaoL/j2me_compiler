/* ------------------------------------------------------------------------- *
          Copyright 2004-2005 Nokia Corporation  All rights reserved.
          Nokia Mobile Phones

          Restricted Rights: Use, duplication, or disclosure by the
          U.S. Government is subject to restrictions as set forth in
          subparagraph (c)(1)(ii) of DFARS 252.227-7013, or in FAR
          52.227-19, or in FAR 52.227-14 Alt. III, as applicable.

          This software is proprietary to and embodies the
          confidential technology of Nokia Possession, use, or copying
          of this software and media is authorized only pursuant to a
          valid written license from Nokia or an authorized
          sublicensor.

          Nokia  - Wireless Software Solutions
 * ------------------------------------------------------------------------- */

package samples.commui;

import java.util.Random;

/**
 * Utility class that implements math functions:Random
 *
 */
public class Util {
    private static Random rnd = new Random();

    /**
     * constructor
     *
     */
    private Util() {
    }

    public static void setSeed(int seed) {
        //System.out.println("Util.setSeed(), seed: " + seed);
        rnd.setSeed(seed);
    }

    /**
     * get Random number
     * @param low
     * @param high
     * @return
     */
    public static int getRandom(int low, int high) {
        return low + Math.abs(rnd.nextInt() % (high - low));
    }
}