package pe.edu.vallegrande.sigrc.users.infrastructure.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

public final class AesCtrCipher {
    private static final String TRANSFORMATION = "AES/CTR/NoPadding";
    private static final int AES_BLOCK_SIZE_BYTES = 16;

    private final SecretKey key;
    private final SecureRandom secureRandom;

    public AesCtrCipher(byte[] keyBytes) {
        this(keyBytes, new SecureRandom());
    }

    AesCtrCipher(byte[] keyBytes, SecureRandom secureRandom) {
        Objects.requireNonNull(keyBytes, "keyBytes must not be null");
        if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
            throw new IllegalArgumentException("AES key must be 128, 192, or 256 bits");
        }
        this.key = new SecretKeySpec(keyBytes.clone(), "AES");
        this.secureRandom = Objects.requireNonNull(secureRandom, "secureRandom must not be null");
    }

    public static AesCtrCipher fromBase64Key(String base64Key) {
        Objects.requireNonNull(base64Key, "base64Key must not be null");
        try {
            return new AesCtrCipher(Base64.getDecoder().decode(base64Key));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("AES key must be valid Base64", exception);
        }
    }

    public EncryptedValue encrypt(byte[] plaintext) {
        Objects.requireNonNull(plaintext, "plaintext must not be null");
        byte[] iv = new byte[AES_BLOCK_SIZE_BYTES];
        secureRandom.nextBytes(iv);
        return new EncryptedValue(iv, process(Cipher.ENCRYPT_MODE, plaintext, iv));
    }

    public String encryptToBase64(String plaintext) {
        Objects.requireNonNull(plaintext, "plaintext must not be null");
        EncryptedValue encrypted = encrypt(plaintext.getBytes(StandardCharsets.UTF_8));
        byte[] combined = new byte[encrypted.iv().length + encrypted.ciphertext().length];
        System.arraycopy(encrypted.iv(), 0, combined, 0, encrypted.iv().length);
        System.arraycopy(encrypted.ciphertext(), 0, combined, encrypted.iv().length,
                encrypted.ciphertext().length);
        return Base64.getEncoder().encodeToString(combined);
    }

    public byte[] decrypt(EncryptedValue encryptedValue) {
        Objects.requireNonNull(encryptedValue, "encryptedValue must not be null");
        if (encryptedValue.iv().length != AES_BLOCK_SIZE_BYTES) {
            throw new IllegalArgumentException("AES-CTR IV must be exactly 128 bits");
        }
        return process(Cipher.DECRYPT_MODE, encryptedValue.ciphertext(), encryptedValue.iv());
    }

    public String decryptFromBase64(String encodedValue) {
        Objects.requireNonNull(encodedValue, "encodedValue must not be null");
        byte[] combined;
        try {
            combined = Base64.getDecoder().decode(encodedValue);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Encrypted value must be valid Base64", exception);
        }
        if (combined.length < AES_BLOCK_SIZE_BYTES) {
            throw new IllegalArgumentException("Encrypted value must contain a 128-bit IV");
        }
        byte[] iv = new byte[AES_BLOCK_SIZE_BYTES];
        byte[] ciphertext = new byte[combined.length - AES_BLOCK_SIZE_BYTES];
        System.arraycopy(combined, 0, iv, 0, iv.length);
        System.arraycopy(combined, iv.length, ciphertext, 0, ciphertext.length);
        return new String(decrypt(new EncryptedValue(iv, ciphertext)), StandardCharsets.UTF_8);
    }

    private byte[] process(int mode, byte[] input, byte[] iv) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(mode, key, new IvParameterSpec(iv));
            return cipher.doFinal(input);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("AES-CTR operation failed", exception);
        }
    }
}
