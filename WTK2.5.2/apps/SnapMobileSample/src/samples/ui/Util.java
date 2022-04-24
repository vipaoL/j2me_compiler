package samples.ui;

import java.io.*;
import java.util.Random;
import javax.microedition.lcdui.Image;

public final class Util {
    private static final int MSB = 0x80;
    private static final int TWO_CHAR_MASK = 0xc0;
    private static final int THREE_CHAR_MASK = 0xe0;

    private static Object object = new Object();
    private static Random rnd = new Random();

    private Util() {
    }

    public static int getRandom(int low, int high) {
        return low + Math.abs(rnd.nextInt()) % (high - low);
    }

    public static Image getImage(String name) {
        InputStream is = null;

        try {
            Image img;

            is = object.getClass().getResourceAsStream(name);
            img = Image.createImage(is);

            return img;
        } catch (IOException e) {
            throw new RuntimeException("could not create image '"
                + name + "': " + e.toString());
        } finally {
            if (is != null) {
                try {is.close();}
                catch (IOException e) {}
            }
        }
    }

    public static String readUnicodeLine(InputStream in) throws IOException {
        StringBuffer buf;
        int a, b, c;

        buf = new StringBuffer();

        for (;;) {
        	//System.out.println("Try 11");
            a = in.read();
           // System.out.println("inputStream from datafile" + a);

            if ((a & MSB) == MSB) {
                if ((a & THREE_CHAR_MASK) == TWO_CHAR_MASK) {
                    b = in.read();

                    if ((b & TWO_CHAR_MASK) != MSB) {
                        throw new RuntimeException("invalid second char: " + Integer.toHexString(b));
                    }

                    a = (char)(((a & 0x1f) << 6) | (b & 0x3f));
                } else if ((a & THREE_CHAR_MASK) == THREE_CHAR_MASK) {
                    b = in.read();
                    c = in.read();

                    if ((b & TWO_CHAR_MASK) != MSB) {
                        throw new RuntimeException("invalid second char: " + Integer.toHexString(b));
                    }

                    if ((c & TWO_CHAR_MASK) != MSB) {
                        throw new RuntimeException("invalid third char: " + Integer.toHexString(c));
                    }

                    a = (char)(((a & 0x0f) << 12) | ((b & 0x3f) << 6) | (c & 0x3f));
                } else throw new RuntimeException("invalid group start: " + Integer.toHexString(a));
            }

            if (a == '\r') continue;
            if (a == '\n') break;

            buf.append((char)a);
        }

        return buf.toString();
    }
}