package pe.edu.vallegrande.sigrc.users.domain.exceptions;

public class KeycloakIntegrationException extends DomainException {
    public KeycloakIntegrationException(String message) {
        super("KEYCLOAK_INTEGRATION_ERROR", message);
    }
}
