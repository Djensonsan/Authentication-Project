package com.pluralsight.calcengine;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Formatter;
public class SHA256Hasher {

    public static String HashSHA256(String salt, String password)
    {
        String sha1 = null;
        String saltedValue = salt+password;
            try {
                MessageDigest crypt = MessageDigest.getInstance("SHA-256");
                crypt.reset();
                crypt.update(saltedValue.getBytes("UTF-8"));
                sha1 = byteToHex(crypt.digest());
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        return sha1;
    }

    public static byte[] getSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    public String byteToString(byte[] input) {
        return org.apache.commons.codec.binary.Base64.encodeBase64String(input);
    }

    private static String byteToHex(final byte[] hash)
    {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}
