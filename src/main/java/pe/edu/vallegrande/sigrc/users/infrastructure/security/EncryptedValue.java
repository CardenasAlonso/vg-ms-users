package pe.edu.vallegrande.sigrc.users.infrastructure.security;

import java.util.Arrays;
import java.util.Objects;

public final class EncryptedValue {
    private final byte[] iv;
    private final byte[] ciphertext;

    public EncryptedValue(byte[] iv, byte[] ciphertext) {
        Objects.requireNonNull(iv, "iv no debe ser nulo");
        Objects.requireNonNull(ciphertext, "el texto cifrado no debe ser nulo");
        this.iv = iv.clone();
        this.ciphertext = ciphertext.clone();
    }

    public byte[] iv() {
        return this.iv.clone();
    }

    public byte[] ciphertext() {
        return this.ciphertext.clone();
    }
}
