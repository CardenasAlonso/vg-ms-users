package pe.edu.vallegrande.sigrc.users.domain.exceptions;

public class KeycloakUserAlreadyExistsException extends DomainException {
    public KeycloakUserAlreadyExistsException() {
        super("KEYCLOAK_USER_EXISTS", "El usuario o email ya existe");
    }
}
