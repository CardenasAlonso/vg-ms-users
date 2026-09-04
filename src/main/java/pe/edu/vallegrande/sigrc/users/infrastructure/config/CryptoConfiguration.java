package pe.edu.vallegrande.sigrc.users.infrastructure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import pe.edu.vallegrande.sigrc.users.infrastructure.security.AesCtrCipher;

@Configuration
public class CryptoConfiguration {

    @Bean
    @ConditionalOnProperty(name = "crypto.aes.key-base64")
    public AesCtrCipher aesCtrCipher(@Value("${crypto.aes.key-base64}") String aesKeyBase64) {
        return AesCtrCipher.fromBase64Key(aesKeyBase64);
    }
}
