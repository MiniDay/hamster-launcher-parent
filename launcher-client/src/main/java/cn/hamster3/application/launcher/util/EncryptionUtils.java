package cn.hamster3.application.launcher.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class EncryptionUtils {

    public static boolean verificationFileSHA1(File file, String sha1) {
        return hashFile(file, "SHA1").equals(sha1);
    }

    public static boolean verificationFileSHA256(File file, String sha256) {
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
        FileInputStream inputStream = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        MessageDigest md5 = MessageDigest.getInstance(hashType);
        for (int numRead; (numRead = inputStream.read(buffer)) > 0; ) {
            md5.update(buffer, 0, numRead);
        }
        inputStream.close();
        return toHexString(md5.digest());
    }

    private static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte aB : b) {
            sb.append(Integer.toHexString(aB & 0xFF));
        }
        return sb.toString();
    }
}
