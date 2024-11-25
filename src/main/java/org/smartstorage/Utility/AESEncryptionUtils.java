package org.smartstorage.Utility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class AESEncryptionUtils {


    @Value("${secrete.config.file}")
    private String secreteConfigFile;

    private static final String AES_ALGORITHM = "AES";

    private static final String SECRET_KEY = "9147m6yu1lk4rwx8";

//    public static String encrypt() throws Exception {
//        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
//        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);
//        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//        byte[] encryptedBytes = cipher.doFinal(secreteConfigFile.getBytes(StandardCharsets.UTF_8));
//        return Base64.getEncoder().encodeToString(encryptedBytes);
//    }

    public String decrypt() throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] encryptedBytes = Base64.getDecoder().decode(secreteConfigFile);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

}
