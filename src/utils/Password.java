/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author c1308l3436
 */
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Base64.Decoder;

public class Password {
    
    private static final int iterations = 1000;
    private static final int saltLen = 32;
    private static final int desiredKeyLen = 256;
    private static final Encoder base64enc = Base64.getEncoder();
    private static final Decoder base64dec = Base64.getDecoder();

    public static String getSaltedHash(String password) throws Exception {
        byte[] salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLen);
        return base64enc.encodeToString(salt) + "$" + hash(password, salt);
    }


    public static boolean check(String password, String stored) throws Exception{
        String[] saltAndPass = stored.split("\\$");
        if (saltAndPass.length != 2) {
            throw new IllegalStateException(
                "The stored password have the form 'salt$hash'");
        }
        String hashOfInput = hash(password, base64dec.decode(saltAndPass[0]));
        return hashOfInput.equals(saltAndPass[1]);
    }

    private static String hash(String password, byte[] salt) throws Exception {
        if (password == null || password.length() == 0)
            throw new IllegalArgumentException("Empty passwords are not supported.");
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = f.generateSecret(new PBEKeySpec(
            password.toCharArray(), salt, iterations, desiredKeyLen)
        );
        return base64enc.encodeToString(key.getEncoded());
    }
}