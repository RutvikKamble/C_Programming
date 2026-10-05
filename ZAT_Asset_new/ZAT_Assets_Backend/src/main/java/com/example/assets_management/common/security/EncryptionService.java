package com.example.assets_management.common.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class EncryptionService {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;
    private final SecureRandom secureRandom = new SecureRandom();
    private final byte[] secret;

    public EncryptionService(@Value("${SOFTWARE_LICENSE_ENCRYPTION_KEY:}") String configuredKey) {
        if (configuredKey == null || configuredKey.isBlank()) {
            this.secret = null;
        } else {
            byte[] bytes = configuredKey.getBytes(StandardCharsets.UTF_8);
            if (bytes.length != 16 && bytes.length != 24 && bytes.length != 32) {
                throw new IllegalArgumentException("SOFTWARE_LICENSE_ENCRYPTION_KEY must be 16, 24, or 32 UTF-8 bytes");
            }
            this.secret = bytes;
        }
    }

    public String encrypt(String plainText) {
        requireConfiguredKey();
        try {
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secret, "AES"), new GCMParameterSpec(TAG_LENGTH, iv));
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to encrypt software license key", e);
        }
    }

    public String decrypt(String encryptedText) {
        requireConfiguredKey();
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedText);
            byte[] iv = new byte[IV_LENGTH];
            byte[] ciphertext = new byte[combined.length - IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, IV_LENGTH);
            System.arraycopy(combined, IV_LENGTH, ciphertext, 0, ciphertext.length);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secret, "AES"), new GCMParameterSpec(TAG_LENGTH, iv));
            return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to decrypt software license key", e);
        }
    }

    private void requireConfiguredKey() {
        if (secret == null) {
            throw new IllegalStateException("SOFTWARE_LICENSE_ENCRYPTION_KEY is not configured");
        }
    }
}
