package samples.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;


public class CustomFont  {

    private static final int KNOCKOUT = 0x00010101;

    /**
     * Inner class for holding Font data -- most Font data is meant
     * to be cached and shared between multiple instances of CustomFont
     * classes which load the same font files.
     *
     */
    class FontData {
        private int[] data;
        private int[] sep;
        private int[] sepOffset;
        private String alphabet;
        private int height;
        private int bufWidth;
        private int dataWidth;
        public FontData( String name) {
            String sepString;
            String dataFile;
            InputStream in;
            Image img;
            int i, sum;

            in = null;
            dataFile = null;
            try {
                img = ResourceManager.getImage(name + ".png");
                height = img.getHeight();
                data = new int[img.getWidth() * height];
                dataWidth = img.getWidth();
                bufWidth = 240;
                img.getRGB(data, 0, img.getWidth(), 0, 0, img.getWidth(), height);

                for (i=0; i<data.length; i++) {
                    if (data[i] == 0xffffffff) data[i] = KNOCKOUT;
                }

                dataFile = name + ".dat";
                in = getClass().getResourceAsStream(dataFile);
                alphabet = Util.readUnicodeLine(in);

                sepString = Util.readUnicodeLine(in);

                sep = new int[sepString.length()];
                sepOffset = new int[sep.length];

                sum = 0;
                for (i=0; i<sep.length; i++) {
                    char width = sepString.charAt(i);
                    // Numerals (widths from 0 to 9 pixels)
                    if (width >= '0' && width <= '9') {
                        sep[i] = sepString.charAt(i) - '0';
                    // Letters (widths from 10 to 35 pixels)
                    } else if (width >= 'A' && width <= 'Z') {
                        sep[i] = sepString.charAt(i) - 'A' + 10;
                    }
                    
                    sepOffset[i] = sum;

                    sum += sep[i];

                }

            } catch (IOException ioe) {
                throw new RuntimeException("can't get font data: " + dataFile);
            } finally {
                if (in != null) {
                    try {in.close();}
                    catch (IOException e) {}
                }
            }
        }
    }

    static private Hashtable fontCache = new Hashtable();
    private FontData font = null;
    private int[] data;
    private int[] buf;
    private char[] charBuf;

    public CustomFont(String name) {
	System.out.println("CustomFont init "+name);
        if (!name.startsWith("/")) name = "/" + name;
        font = (FontData)fontCache.get( name);
        if (font == null) {
            font = new FontData( name);
            fontCache.put( name, font);
        }
        data = font.data;
        buf = new int[font.bufWidth * font.height];
        charBuf = new char[128];
    }

    public void setColor(int color) {
        if (color == KNOCKOUT) color = 0x00000000;

        // If we are still pointing to the shared cached font data,
        // then make us our own copy to begin changing the color of.
        int[] newData = (data == font.data) ?
                new int[data.length] : data;

        // Change all colors except for the knockout color.
        // NOTE: This will wreck antialiased fonts!!
        for (int i=0; i<data.length; i++) {
            if (data[i] != KNOCKOUT) newData[i] = color;
            else newData[i] = KNOCKOUT;
        }
        data = newData;
    }

    public int getHeight() {
        return font.height;
    }

    public int stringWidth(String string) {
        int n, width;

        width = 0;

        for (int i=0; i<string.length(); i++) {
            n = font.alphabet.indexOf(string.charAt(i));
            if (n < 0) continue;
            width += font.sep[n] + 1;
         }

        if (width > 0) width--;
        return width;
    }

    public int substringWidth(String string, int offset, int length){
        return charsWidth(string.toCharArray(), offset,length);

    }


    public int charsWidth(char[] chars, int offset, int length) {
        int n, width;

        width = 0;

        for (int i=offset; i<offset+length; i++) {
            n = font.alphabet.indexOf(chars[i]);
            if (n < 0) continue;

            width += font.sep[n] + 1;
        }

        if (width > 0) width--;
        return  width;
    }

    public void drawString(Graphics g, String string, int x, int y) {
        int i, n;

        n = Math.min(string.length(), charBuf.length);

        for (i=0; i<n; i++) {
            charBuf[i] = string.charAt(i);
        }

        drawChars(g, charBuf, 0, n, x, y);
    }

    public void drawChars(Graphics g, char[] chars, int offset, int length, int x, int y) {
        int i, j, dataX, bufX, n, width;

        bufX = 0;

        for (i=offset; i<offset+length; i++) {

            n = font.alphabet.indexOf(chars[i]);
            if (n < 0) continue;


            dataX = font.sepOffset[n];
            width = font.sep[n];


            for (j=0; j<font.height; j++) {
                System.arraycopy(
                    data,
                    dataX + j * font.dataWidth,
                    buf,
                    bufX + j * font.bufWidth,
                    width
                );

                buf[bufX + width + j * font.bufWidth] = KNOCKOUT;
            }

            bufX += width + 1;
            if (bufX >= font.bufWidth) break;

        }

		g.drawRGB(buf, 0, font.bufWidth, x, y, bufX, buf.length / font.bufWidth, true);
    }
}
