package com.javainuse.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncryptDecryptHelper {

    public static String doEncryption(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    public static String doDecryption(String encyptedText) {
        return  new String(Base64.getDecoder().decode(encyptedText));
    }

}
