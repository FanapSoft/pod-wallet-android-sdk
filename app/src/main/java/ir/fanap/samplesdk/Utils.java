package ir.fanap.samplesdk;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class Utils {
    public static String codeVerifier(){
        SecureRandom sr = new SecureRandom();
        byte[] code = new byte[32];
        sr.nextBytes(code);
        return Base64.encodeToString(code, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
    }

    public static String transformToCodeChallenge(String codeVerifier) {
        try {
            byte[] bytes;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                bytes = codeVerifier.getBytes(StandardCharsets.US_ASCII);
            }else {
                bytes = codeVerifier.getBytes("US-ASCII");
            }

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(bytes, 0, bytes.length);
            byte[] digest = md.digest();

            return Base64.encodeToString(digest, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
}
