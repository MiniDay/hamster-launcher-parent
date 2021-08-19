package cn.hamster3.application.launcher.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class EncryptionUtils {

    public static boolean sha1(File file, String sha1) {
        return hashFile(file, "SHA1").equals(sha1);
    }

    public static boolean sha256(File file, String sha256) {
        return hashFile(file, "SHA-256").equals(sha256);
    }

    private static String hashFile(File file, String hashType) {
        String str = "";
        try {
            str = getHash(file, hashType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    private static String getHash(File file, String hashType) throws Exception {
        FileInputStream stream = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        MessageDigest digest = MessageDigest.getInstance(hashType);
        int read = stream.read(buffer);
        while (read > 0) {
            digest.update(buffer, 0, read);
            read = stream.read(buffer);
        }
        stream.close();
        return toHexString(digest.digest());
    }

    private static String toHexString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() < 2) {
                builder.append(0);
            }
            builder.append(hex);
        }
        return builder.toString();
    }
}
