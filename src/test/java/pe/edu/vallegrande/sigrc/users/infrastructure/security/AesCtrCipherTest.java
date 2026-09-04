package pe.edu.vallegrande.sigrc.users.infrastructure.security;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AesCtrCipherTest {
    private static final byte[] KEY = "0123456789abcdef".getBytes(StandardCharsets.US_ASCII);

    @Test
    void cifraYDescifraTextoConClaveAes128() {
        AesCtrCipher cipher = new AesCtrCipher(KEY);
        String plaintext = "datos sensibles del usuario";

        String encrypted = cipher.encryptToBase64(plaintext);

        assertNotEquals(plaintext, encrypted);
        assertTrue(encrypted.length() > 24);
        assertEquals(plaintext, cipher.decryptFromBase64(encrypted));
    }

    @Test
    void generaUnIvNuevoParaCadaCifrado() {
        AesCtrCipher cipher = new AesCtrCipher(KEY);

        EncryptedValue first = cipher.encrypt("same plaintext".getBytes(StandardCharsets.UTF_8));
        EncryptedValue second = cipher.encrypt("same plaintext".getBytes(StandardCharsets.UTF_8));

        assertNotEquals(Base64.getEncoder().encodeToString(first.iv()),
                Base64.getEncoder().encodeToString(second.iv()));
        assertArrayEquals("same plaintext".getBytes(StandardCharsets.UTF_8), cipher.decrypt(first));
    }

    @Test
    void rechazaClavesQueNoSonAes() {
        assertThrows(IllegalArgumentException.class, () -> new AesCtrCipher(new byte[15]));
        assertThrows(IllegalArgumentException.class, () -> new AesCtrCipher(new byte[20]));
        assertThrows(IllegalArgumentException.class, () -> new AesCtrCipher(new byte[33]));
    }

    @Test
    void rechazaValoresSinIvCompleto() {
        AesCtrCipher cipher = new AesCtrCipher(KEY);

        assertThrows(IllegalArgumentException.class, () -> cipher.decryptFromBase64("AA=="));
    }

    @Test
    void coincideConVectorNistParaAesCtr128() {
        byte[] key = hex("2b7e151628aed2a6abf7158809cf4f3c");
        byte[] iv = hex("f0f1f2f3f4f5f6f7f8f9fafbfcfdfeff");
        byte[] plaintext = hex("6bc1bee22e409f96e93d7e117393172a");
        byte[] expectedCiphertext = hex("874d6191b620e3261bef6864990db6ce");

        AesCtrCipher cipher = new AesCtrCipher(key, new FixedSecureRandom(iv));

        assertArrayEquals(expectedCiphertext, cipher.encrypt(plaintext).ciphertext());
    }

    private static byte[] hex(String value) {
        byte[] result = new byte[value.length() / 2];
        for (int index = 0; index < result.length; index++) {
            result[index] = (byte) Integer.parseInt(value.substring(index * 2, index * 2 + 2), 16);
        }
        return result;
    }

    private static final class FixedSecureRandom extends SecureRandom {
        private final byte[] bytes;

        private FixedSecureRandom(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public void nextBytes(byte[] target) {
            System.arraycopy(bytes, 0, target, 0, target.length);
        }
    }
}
