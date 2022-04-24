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
import java.security.*;
import java.security.spec.*;

import javax.crypto.*;
import javax.crypto.spec.*;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;


public class CryptoMIDlet extends MIDlet implements CommandListener, Runnable {
    private static final byte[] kDigest =
        {
            (byte)0xA9, (byte)0x99, (byte)0x3E, (byte)0x36, (byte)0x47, (byte)0x06, (byte)0x81,
            (byte)0x6A, (byte)0xBA, (byte)0x3E, (byte)0x25, (byte)0x71, (byte)0x78, (byte)0x50,
            (byte)0xC2, (byte)0x6C, (byte)0x9C, (byte)0xD0, (byte)0xD8, (byte)0x9D
        };
    private static final byte[] kRSAPublicKey =
        {
            (byte)0x30, (byte)0x82, (byte)0x1, (byte)0x22, (byte)0x30, (byte)0xd, (byte)0x6,
            (byte)0x9, (byte)0x2a, (byte)0x86, (byte)0x48, (byte)0x86, (byte)0xf7, (byte)0xd,
            (byte)0x1, (byte)0x1, (byte)0x1, (byte)0x5, (byte)0x0, (byte)0x3, (byte)0x82, (byte)0x1,
            (byte)0xf, (byte)0x0, (byte)0x30, (byte)0x82, (byte)0x1, (byte)0xa, (byte)0x2,
            (byte)0x82, (byte)0x1, (byte)0x1, (byte)0x0, (byte)0xe0, (byte)0xe2, (byte)0x9f,
            (byte)0xc2, (byte)0x75, (byte)0x4c, (byte)0x10, (byte)0x53, (byte)0xbb, (byte)0x48,
            (byte)0xcb, (byte)0x54, (byte)0x23, (byte)0xe4, (byte)0x91, (byte)0x17, (byte)0xa2,
            (byte)0xec, (byte)0x59, (byte)0x9f, (byte)0x6f, (byte)0x57, (byte)0x7f, (byte)0x9b,
            (byte)0x6a, (byte)0x1f, (byte)0x93, (byte)0x5e, (byte)0x69, (byte)0xf1, (byte)0xd4,
            (byte)0x56, (byte)0xb9, (byte)0x65, (byte)0x9e, (byte)0x14, (byte)0x27, (byte)0xb8,
            (byte)0xb1, (byte)0xb5, (byte)0x9d, (byte)0xea, (byte)0xd6, (byte)0xef, (byte)0xc2,
            (byte)0x3, (byte)0x4e, (byte)0x9b, (byte)0x28, (byte)0x1e, (byte)0x1b, (byte)0x8,
            (byte)0x1a, (byte)0x5, (byte)0x4d, (byte)0xf7, (byte)0xb5, (byte)0xe7, (byte)0x92,
            (byte)0xcd, (byte)0x3a, (byte)0x59, (byte)0xd8, (byte)0xb6, (byte)0xb6, (byte)0x20,
            (byte)0xf3, (byte)0xc8, (byte)0x2b, (byte)0xf8, (byte)0x1e, (byte)0x38, (byte)0xd9,
            (byte)0xb4, (byte)0xf4, (byte)0x23, (byte)0xc0, (byte)0x3, (byte)0xc9, (byte)0x2,
            (byte)0x71, (byte)0x7a, (byte)0xac, (byte)0x40, (byte)0x25, (byte)0x67, (byte)0xfe,
            (byte)0xc2, (byte)0x6a, (byte)0xd2, (byte)0x3b, (byte)0x25, (byte)0x14, (byte)0x29,
            (byte)0xf5, (byte)0x99, (byte)0x8c, (byte)0xef, (byte)0x51, (byte)0x25, (byte)0xa4,
            (byte)0x37, (byte)0xda, (byte)0xb1, (byte)0x65, (byte)0xb6, (byte)0x49, (byte)0xf7,
            (byte)0x9d, (byte)0x1e, (byte)0x5a, (byte)0x34, (byte)0xe, (byte)0x17, (byte)0xf2,
            (byte)0x50, (byte)0x92, (byte)0x85, (byte)0xbb, (byte)0x1c, (byte)0x6c, (byte)0xae,
            (byte)0x6a, (byte)0xe4, (byte)0xe0, (byte)0x29, (byte)0xe5, (byte)0xfd, (byte)0xcd,
            (byte)0x10, (byte)0x1a, (byte)0xab, (byte)0x7, (byte)0xc7, (byte)0xa4, (byte)0x32,
            (byte)0xd7, (byte)0xbd, (byte)0x70, (byte)0x24, (byte)0xc6, (byte)0x53, (byte)0x73,
            (byte)0x33, (byte)0x95, (byte)0x62, (byte)0x84, (byte)0x99, (byte)0xb5, (byte)0x3b,
            (byte)0x83, (byte)0x90, (byte)0xe, (byte)0xbc, (byte)0x91, (byte)0x58, (byte)0xf0,
            (byte)0x95, (byte)0x96, (byte)0x15, (byte)0xf, (byte)0xed, (byte)0x68, (byte)0xba,
            (byte)0x46, (byte)0x5, (byte)0x22, (byte)0x99, (byte)0x55, (byte)0x1e, (byte)0x39,
            (byte)0xbe, (byte)0xf5, (byte)0x34, (byte)0xcd, (byte)0xb9, (byte)0x43, (byte)0xde,
            (byte)0x1c, (byte)0xeb, (byte)0xf0, (byte)0x79, (byte)0xee, (byte)0x9d, (byte)0x60,
            (byte)0xa5, (byte)0x50, (byte)0x78, (byte)0xe0, (byte)0x38, (byte)0xf9, (byte)0x28,
            (byte)0x96, (byte)0xaf, (byte)0x7, (byte)0x99, (byte)0xd6, (byte)0xce, (byte)0x7c,
            (byte)0xbc, (byte)0x3b, (byte)0x4, (byte)0xfd, (byte)0xd, (byte)0x9, (byte)0x70,
            (byte)0xb1, (byte)0xad, (byte)0xcf, (byte)0xa5, (byte)0x46, (byte)0xc8, (byte)0x41,
            (byte)0x5c, (byte)0x7, (byte)0xd8, (byte)0x9b, (byte)0xcb, (byte)0xd7, (byte)0xcb,
            (byte)0x5c, (byte)0xc4, (byte)0x96, (byte)0xe, (byte)0x41, (byte)0x84, (byte)0x3b,
            (byte)0x28, (byte)0x91, (byte)0x7, (byte)0xc5, (byte)0xdc, (byte)0x9e, (byte)0x71,
            (byte)0x78, (byte)0x10, (byte)0x41, (byte)0x8d, (byte)0x5, (byte)0x3d, (byte)0x36,
            (byte)0x3f, (byte)0x78, (byte)0xa1, (byte)0x9c, (byte)0xb3, (byte)0x37, (byte)0x81,
            (byte)0x2a, (byte)0xa5, (byte)0xd0, (byte)0x25, (byte)0xad, (byte)0xfe, (byte)0x71,
            (byte)0x7, (byte)0x2, (byte)0x3, (byte)0x1, (byte)0x0, (byte)0x1
        };
    private static final byte[] kData = { 0, 1, 2, 3, 4 };
    private static final byte[] kSignature =
        {
            (byte)0xb0, (byte)0x29, (byte)0xb7, (byte)0x74, (byte)0xfc, (byte)0xdc, (byte)0xf7,
            (byte)0xae, (byte)0xaf, (byte)0x11, (byte)0x60, (byte)0x16, (byte)0xcb, (byte)0x72,
            (byte)0x20, (byte)0xb0, (byte)0x98, (byte)0xeb, (byte)0x68, (byte)0x5b, (byte)0xa0,
            (byte)0x37, (byte)0xe1, (byte)0x20, (byte)0xf, (byte)0x1a, (byte)0x4a, (byte)0xb7,
            (byte)0x4b, (byte)0xf9, (byte)0xa2, (byte)0x50, (byte)0x6, (byte)0x8c, (byte)0x6d,
            (byte)0x6, (byte)0xc2, (byte)0x7a, (byte)0xfd, (byte)0x22, (byte)0xd0, (byte)0xf,
            (byte)0xaa, (byte)0xbd, (byte)0x62, (byte)0x73, (byte)0x69, (byte)0x30, (byte)0x8e,
            (byte)0xea, (byte)0xfa, (byte)0x73, (byte)0x7d, (byte)0x50, (byte)0x25, (byte)0x34,
            (byte)0xaa, (byte)0x54, (byte)0x7c, (byte)0xac, (byte)0xc3, (byte)0xcb, (byte)0xe3,
            (byte)0xf0, (byte)0x85, (byte)0xf7, (byte)0x38, (byte)0xd0, (byte)0xa8, (byte)0xd9,
            (byte)0x89, (byte)0xd4, (byte)0x6b, (byte)0x3, (byte)0x60, (byte)0x54, (byte)0xf4,
            (byte)0xcc, (byte)0xc7, (byte)0x2e, (byte)0x28, (byte)0x25, (byte)0x59, (byte)0x1d,
            (byte)0xec, (byte)0x67, (byte)0x7d, (byte)0xd1, (byte)0x24, (byte)0x77, (byte)0xd0,
            (byte)0x80, (byte)0x12, (byte)0x23, (byte)0xeb, (byte)0x57, (byte)0xdb, (byte)0x12,
            (byte)0x48, (byte)0x9a, (byte)0x5d, (byte)0xeb, (byte)0xef, (byte)0x28, (byte)0x34,
            (byte)0x5d, (byte)0x2b, (byte)0x23, (byte)0x7e, (byte)0x52, (byte)0xdd, (byte)0xe,
            (byte)0xbf, (byte)0x5d, (byte)0xed, (byte)0xf, (byte)0x36, (byte)0x73, (byte)0x77,
            (byte)0xe3, (byte)0x15, (byte)0xf6, (byte)0xa0, (byte)0xf2, (byte)0x50, (byte)0x5c,
            (byte)0x7d, (byte)0x7e, (byte)0x90, (byte)0x50, (byte)0xa4, (byte)0xea, (byte)0x6a,
            (byte)0x2, (byte)0x5b, (byte)0x5b, (byte)0xdf, (byte)0x51, (byte)0xe9, (byte)0x1f,
            (byte)0x60, (byte)0x46, (byte)0x16, (byte)0xb3, (byte)0xb6, (byte)0x72, (byte)0xc1,
            (byte)0x70, (byte)0x8c, (byte)0x59, (byte)0xb2, (byte)0xae, (byte)0x83, (byte)0xb5,
            (byte)0x19, (byte)0x30, (byte)0x5b, (byte)0x4, (byte)0x22, (byte)0x39, (byte)0x2,
            (byte)0x4f, (byte)0x13, (byte)0x17, (byte)0xe1, (byte)0xed, (byte)0x7b, (byte)0x41,
            (byte)0x34, (byte)0x97, (byte)0x8c, (byte)0x54, (byte)0x44, (byte)0xb4, (byte)0xa2,
            (byte)0xf3, (byte)0xe, (byte)0x51, (byte)0x59, (byte)0x0, (byte)0x54, (byte)0xc5,
            (byte)0xd2, (byte)0xae, (byte)0x9f, (byte)0xe3, (byte)0x38, (byte)0xc6, (byte)0x2,
            (byte)0x7a, (byte)0x9b, (byte)0xb0, (byte)0xda, (byte)0xfb, (byte)0x58, (byte)0x9c,
            (byte)0x4e, (byte)0x15, (byte)0xb7, (byte)0x75, (byte)0xe9, (byte)0xe3, (byte)0x93,
            (byte)0xee, (byte)0x2, (byte)0xa, (byte)0xef, (byte)0xe6, (byte)0xc2, (byte)0xa0,
            (byte)0xde, (byte)0x3d, (byte)0x81, (byte)0x36, (byte)0xa0, (byte)0xe, (byte)0x78,
            (byte)0xd5, (byte)0x81, (byte)0x3a, (byte)0xa3, (byte)0xb7, (byte)0xdb, (byte)0x4e,
            (byte)0x72, (byte)0xa1, (byte)0x6a, (byte)0xa8, (byte)0xd9, (byte)0x58, (byte)0xc9,
            (byte)0x99, (byte)0xd9, (byte)0x39, (byte)0x94, (byte)0xf9, (byte)0x1a, (byte)0xd1,
            (byte)0x84, (byte)0x26, (byte)0xc7, (byte)0xfd, (byte)0x54, (byte)0x51, (byte)0xba,
            (byte)0xd7, (byte)0xff, (byte)0xc2, (byte)0x72, (byte)0xe5, (byte)0x7d, (byte)0x2,
            (byte)0x91, (byte)0xb0, (byte)0xe, (byte)0x3e
        };
    private static final byte[] kAESKey =
        {
            (byte)0x2b, (byte)0x7e, (byte)0x15, (byte)0x16, (byte)0x28, (byte)0xae, (byte)0xd2,
            (byte)0xa6, (byte)0xab, (byte)0xf7, (byte)0x15, (byte)0x88, (byte)0x09, (byte)0xcf,
            (byte)0x4f, (byte)0x3c
        };
    private static final byte[] kDESKey =
        {
            (byte)0x2b, (byte)0x7e, (byte)0x15, (byte)0x16, (byte)0x28, (byte)0xae, (byte)0xd2,
            (byte)0xa6
        };
    private static final byte[] kDESEDEKey =
        {
            (byte)0x2b, (byte)0x7e, (byte)0x15, (byte)0x16, (byte)0x28, (byte)0xae, (byte)0xd2,
            (byte)0xa6, (byte)0x2b, (byte)0x7e, (byte)0x15, (byte)0x16, (byte)0x28, (byte)0xae,
            (byte)0xd2, (byte)0xa6, (byte)0x2b, (byte)0x7e, (byte)0x15, (byte)0x16, (byte)0x28,
            (byte)0xae, (byte)0xd2, (byte)0xa6,
        };
    private static final byte[] kBlockPlaintext =
        {
            (byte)0x32, (byte)0x43, (byte)0xf6, (byte)0xa8, (byte)0x88, (byte)0x5a, (byte)0x30,
            (byte)0x8d, (byte)0x31, (byte)0x31, (byte)0x98, (byte)0xa2, (byte)0xe0, (byte)0x37,
            (byte)0x07, (byte)0x34
        };
    private static final byte[] kAESiv =
        {
            (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07,
            (byte)0x08, (byte)0x09, (byte)0x0a, (byte)0x0b, (byte)0x0c, (byte)0x0d, (byte)0x0e,
            (byte)0x0f, (byte)0x10
        };
    private static final byte[] kDESiv =
        {
            (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07,
            (byte)0x08,
        };
    private Display mDisplay;
    private Form mMainForm;
    private Command mExitCommand;
    private Command mGoCommand;
    private Command mBackCommand;
    private Form mProgressForm;

    public CryptoMIDlet() {
        mExitCommand = new Command("Exit", Command.EXIT, 0);
        mGoCommand = new Command("Go", Command.SCREEN, 0);
        mBackCommand = new Command("Back", Command.BACK, 0);

        mMainForm = new Form("Crypto Example");
        mMainForm.append("Press Go to use the SATSA-CRYPTO API " + "to perform cryptography.");
        mMainForm.addCommand(mExitCommand);
        mMainForm.addCommand(mGoCommand);
        mMainForm.setCommandListener(this);
    }

    public void startApp() {
        mDisplay = Display.getDisplay(this);

        mDisplay.setCurrent(mMainForm);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    public void commandAction(Command c, Displayable s) {
        if (c == mExitCommand) {
            notifyDestroyed();
        } else if (c == mGoCommand) {
            mProgressForm = new Form("Working...");
            mDisplay.setCurrent(mProgressForm);

            Thread t = new Thread(this);
            t.start();
        } else if (c == mBackCommand) {
            mDisplay.setCurrent(mMainForm);
        }
    }

    public void run() {
        try {
            runDigest();

            runSignature();

            runCipher();

            byte[] nonBlockPlaintext = "This is not a block length.".getBytes();

            runCipherSymmetric("AES/ECB/NoPadding", kAESKey, "AES", null, kBlockPlaintext);
            runCipherSymmetric("AES/ECB/PKCS5Padding", kAESKey, "AES", null, nonBlockPlaintext);
            runCipherSymmetric("AES/CBC/NoPadding", kAESKey, "AES", kAESiv, kBlockPlaintext);
            runCipherSymmetric("AES/CBC/PKCS5Padding", kAESKey, "AES", kAESiv, nonBlockPlaintext);

            runCipherSymmetric("DES/ECB/NoPadding", kDESKey, "DES", null, kBlockPlaintext);
            runCipherSymmetric("DES/ECB/PKCS5Padding", kDESKey, "DES", null, nonBlockPlaintext);
            runCipherSymmetric("DES/CBC/NoPadding", kDESKey, "DES", kDESiv, kBlockPlaintext);
            runCipherSymmetric("DES/CBC/PKCS5Padding", kDESKey, "DES", kDESiv, nonBlockPlaintext);

            runCipherSymmetric("DESede/ECB/NoPadding", kDESEDEKey, "DESede", null, kBlockPlaintext);
            runCipherSymmetric("DESede/ECB/PKCS5Padding", kDESEDEKey, "DESede", null,
                nonBlockPlaintext);
            runCipherSymmetric("DESede/CBC/NoPadding", kDESEDEKey, "DESede", kDESiv, kBlockPlaintext);
            runCipherSymmetric("DESede/CBC/PKCS5Padding", kDESEDEKey, "DESede", kDESiv,
                nonBlockPlaintext);

            mProgressForm.setTitle("Working...done.");
            mProgressForm.addCommand(mBackCommand);
            mProgressForm.setCommandListener(this);
        } catch (Exception e) {
            Form f = new Form("Exception");
            f.append(e.toString());
            f.addCommand(mBackCommand);
            f.setCommandListener(this);
            mDisplay.setCurrent(f);
        }
    }

    private void runDigest() throws NoSuchAlgorithmException, DigestException {
        setProgress("Generating SHA-1 digest");

        byte[] data = "abc".getBytes();
        byte[] digest = new byte[20];
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(data, 0, data.length);
        md.digest(digest, 0, 20);

        boolean pass = true;

        for (int i = 0; i < 20; i++) {
            if (digest[i] != kDigest[i]) {
                pass = false;
            }
        }

        setProgress("SHA1 digest..." + (pass ? "pass" : "fail"));
    }

    private void runSignature()
        throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException,
            SignatureException {
        X509EncodedKeySpec pks = new X509EncodedKeySpec(kRSAPublicKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey publicKey = kf.generatePublic(pks);

        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initVerify(publicKey);
        signature.update(kData, 0, kData.length);

        boolean pass = signature.verify(kSignature);
        setProgress("RSA signature..." + (pass ? "" : "not ") + "verified");
    }

    private void runCipher()
        throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
            InvalidKeyException, IllegalStateException, ShortBufferException,
            IllegalBlockSizeException, BadPaddingException {
        X509EncodedKeySpec pks = new X509EncodedKeySpec(kRSAPublicKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey publicKey = kf.generatePublic(pks);

        byte[] ciphertext = new byte[512];
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        cipher.doFinal(kData, 0, kData.length, ciphertext, 0);

        setProgress("RSA encryption...done");
    }

    private void runCipherSymmetric(String algorithm, byte[] keyBits, String keyAlgorithm,
        byte[] ivBits, byte[] plaintext)
        throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
            InvalidKeyException, IllegalStateException, ShortBufferException,
            IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(algorithm);
        Key key = new SecretKeySpec(keyBits, 0, keyBits.length, keyAlgorithm);
        IvParameterSpec iv = null;

        if (ivBits != null) {
            iv = new IvParameterSpec(ivBits, 0, ivBits.length);
        }

        // Calculate ciphertext size.
        int blocksize = 16;
        int ciphertextLength = 0;
        int remainder = plaintext.length % blocksize;

        if (remainder == 0) {
            ciphertextLength = plaintext.length;
        } else {
            ciphertextLength = plaintext.length - remainder + blocksize;
        }

        if (iv == null) {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        }

        byte[] ciphertext = new byte[ciphertextLength];
        cipher.doFinal(plaintext, 0, plaintext.length, ciphertext, 0);

        if (iv == null) {
            cipher.init(Cipher.DECRYPT_MODE, key);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
        }

        byte[] decrypted = new byte[plaintext.length];
        cipher.doFinal(ciphertext, 0, ciphertext.length, decrypted, 0);

        boolean pass = compareArrays(plaintext, decrypted);
        setProgress(algorithm + "..." + (pass ? "passed" : "failed"));
    }

    private boolean compareArrays(byte[] array1, byte[] array2) {
        if (array1.length != array2.length) {
            return false;
        }

        for (int i = 0; i < array1.length; i++) {
            if (array1[i] != array2[i]) {
                return false;
            }
        }

        return true;
    }

    private void setProgress(String s) {
        StringItem si = new StringItem(null, s);
        si.setLayout(Item.LAYOUT_2 | Item.LAYOUT_NEWLINE_AFTER);
        mProgressForm.append(si);
    }
}
