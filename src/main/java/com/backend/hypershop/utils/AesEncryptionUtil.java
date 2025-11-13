package com.backend.hypershop.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Production-ready AES Encryption Utility using GCM mode
 * Provides methods for encrypting/decrypting passwords and tokens
 */
public class AesEncryptionUtil {
    
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int AES_KEY_BIT = 256;
    
    /**
     * Generates a secure AES-256 key
     * Store this key securely (environment variable, AWS KMS, etc.)
     */
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(AES_KEY_BIT, SecureRandom.getInstanceStrong());
        return keyGenerator.generateKey();
    }
    
    /**
     * Converts base64 encoded key string to SecretKey
     */
    public static SecretKey getKeyFromString(String base64Key) {
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        return new SecretKeySpec(decodedKey, "AES");
    }
    
    /**
     * Converts SecretKey to base64 string for storage
     */
    public static String keyToString(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
    
    // ==================== SIMPLE ENCRYPTION/DECRYPTION ====================
    
    /**
     * Simple encrypt method for passwords, sensitive strings, etc.
     * Returns Base64 encoded string with IV prepended
     * 
     * @param plainText The text to encrypt
     * @param secretKey The AES key
     * @return Base64 encoded encrypted string
     */
    public static String encrypt(String plainText, SecretKey secretKey) throws Exception {
        if (plainText == null || plainText.isEmpty()) {
            throw new IllegalArgumentException("Plain text cannot be null or empty");
        }
        
        // Generate random IV
        byte[] iv = generateIV();
        
        // Initialize cipher
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);
        
        // Encrypt
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        
        // Combine IV + encrypted data
        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedBytes.length);
        byteBuffer.put(iv);
        byteBuffer.put(encryptedBytes);
        
        // Return as Base64
        return Base64.getEncoder().encodeToString(byteBuffer.array());
    }
    
    /**
     * Simple decrypt method for passwords, sensitive strings, etc.
     * 
     * @param encryptedText Base64 encoded encrypted string (with IV prepended)
     * @param secretKey The AES key
     * @return Decrypted plain text
     */
    public static String decrypt(String encryptedText, SecretKey secretKey) throws Exception {
        if (encryptedText == null || encryptedText.isEmpty()) {
            throw new IllegalArgumentException("Encrypted text cannot be null or empty");
        }
        
        // Decode Base64
        byte[] decoded = Base64.getDecoder().decode(encryptedText);
        
        // Extract IV
        ByteBuffer byteBuffer = ByteBuffer.wrap(decoded);
        byte[] iv = new byte[IV_LENGTH_BYTE];
        byteBuffer.get(iv);
        
        // Extract encrypted data
        byte[] encryptedBytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(encryptedBytes);
        
        // Initialize cipher for decryption
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
        
        // Decrypt
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    
    // ==================== TOKEN ENCRYPTION/DECRYPTION ====================
    
    /**
     * Encrypts a token (JWT payload, session ID, etc.)
     * Returns URL-safe Base64 encoded string for use in URLs
     * 
     * @param tokenPayload The token payload to encrypt
     * @param secretKey The AES key
     * @return URL-safe Base64 encoded encrypted token
     */
    public static String encryptToken(String tokenPayload, SecretKey secretKey) throws Exception {
        if (tokenPayload == null || tokenPayload.isEmpty()) {
            throw new IllegalArgumentException("Token payload cannot be null or empty");
        }
        
        // Generate random IV
        byte[] iv = generateIV();
        
        // Initialize cipher
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);
        
        // Encrypt
        byte[] encryptedBytes = cipher.doFinal(tokenPayload.getBytes(StandardCharsets.UTF_8));
        
        // Combine IV + encrypted data
        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedBytes.length);
        byteBuffer.put(iv);
        byteBuffer.put(encryptedBytes);
        
        // Return as URL-safe Base64 (no padding for cleaner URLs)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(byteBuffer.array());
    }
    
    /**
     * Decrypts an encrypted token
     * 
     * @param encryptedToken URL-safe Base64 encoded encrypted token
     * @param secretKey The AES key
     * @return Decrypted token payload
     */
    public static String decryptToken(String encryptedToken, SecretKey secretKey) throws Exception {
        if (encryptedToken == null || encryptedToken.isEmpty()) {
            throw new IllegalArgumentException("Encrypted token cannot be null or empty");
        }
        
        // Decode URL-safe Base64
        byte[] decoded = Base64.getUrlDecoder().decode(encryptedToken);
        
        // Extract IV
        ByteBuffer byteBuffer = ByteBuffer.wrap(decoded);
        byte[] iv = new byte[IV_LENGTH_BYTE];
        byteBuffer.get(iv);
        
        // Extract encrypted data
        byte[] encryptedBytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(encryptedBytes);
        
        // Initialize cipher for decryption
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
        
        // Decrypt
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Generates a cryptographically secure random IV
     */
    private static byte[] generateIV() {
        byte[] iv = new byte[IV_LENGTH_BYTE];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        return iv;
    }
    
    // ==================== USAGE EXAMPLE ====================
    
    public static void main(String[] args) {
        try {
            // Generate key (do this once and store securely)
            SecretKey secretKey = generateKey();
            String keyString = keyToString(secretKey);
            System.out.println("Generated Key (Store this securely): " + keyString);
            System.out.println();
            
            // ===== Example 1: Encrypt/Decrypt Password =====
            String password = "MySecurePassword123!";
            System.out.println("=== Password Encryption ===");
            System.out.println("Original Password: " + password);
            
            String encryptedPassword = encrypt(password, secretKey);
            System.out.println("Encrypted Password: " + encryptedPassword);
            
            String decryptedPassword = decrypt(encryptedPassword, secretKey);
            System.out.println("Decrypted Password: " + decryptedPassword);
            System.out.println();
            
            // ===== Example 2: Encrypt/Decrypt Token =====
            String tokenPayload = "{\"userId\":\"12345\",\"role\":\"admin\",\"exp\":1699999999}";
            System.out.println("=== Token Encryption ===");
            System.out.println("Original Token Payload: " + tokenPayload);
            
            String encryptedToken = encryptToken(tokenPayload, secretKey);
            System.out.println("Encrypted Token: " + encryptedToken);
            
            String decryptedToken = decryptToken(encryptedToken, secretKey);
            System.out.println("Decrypted Token Payload: " + decryptedToken);
            System.out.println();
            
            // ===== Example 3: Using stored key =====
            System.out.println("=== Using Stored Key ===");
            SecretKey retrievedKey = getKeyFromString(keyString);
            String testDecrypt = decrypt(encryptedPassword, retrievedKey);
            System.out.println("Decryption with retrieved key works: " + testDecrypt.equals(password));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
