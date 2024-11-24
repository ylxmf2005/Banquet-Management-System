package hk.polyu.comp.project2411.bms.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncoding {
    public static String encoding(String passwd)  {
        String ret = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] input = passwd.getBytes();
            byte[] buff = md.digest(input);
            ret = bytesToHex(buff);
        } catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
        return ret;
    }
    public static String bytesToHex(byte[] bytes) {
        StringBuffer str = new StringBuffer();
        for (int aByte : bytes) {
            int cur = aByte;
            if (cur < 0) cur += 256;
            if (cur < 16) str.append("0");
            str.append(Integer.toHexString(cur));
        }
        return str.toString();
    }
}
