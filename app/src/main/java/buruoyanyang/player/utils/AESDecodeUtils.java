package buruoyanyang.player.utils;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * buruoyanyang.player.utils
 * author xiaofeng
 * 16/7/7
 */
public class AESDecodeUtils {
    private static final byte[] ivBytes = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    private static final String HEX = "1234567890123456";

    public static String Decrypt(String src, String key) {
        try {
            byte[] keyBytes = key.getBytes("UTF-8");
            byte[] cipherData = null;
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            SecretKeySpec newKey = new SecretKeySpec(keyBytes, "AES");
            //设置加密类型
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            //Base64解密
            byte[] encrypted = Base64.decode(src, Base64.DEFAULT);
            cipherData = cipher.doFinal(encrypted);
            return new String(cipherData);
        } catch (Exception ex) {
            return "2";
        }
    }

}
