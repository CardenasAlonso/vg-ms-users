package pe.edu.vallegrande.sigrc.users.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "keycloak.admin")
public class KeycloakProperties {
    private String baseUrl;
    private String realm;
    private String clientId;
    private String clientSecret;
}
