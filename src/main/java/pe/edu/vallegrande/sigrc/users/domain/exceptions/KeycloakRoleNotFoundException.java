package pe.edu.vallegrande.sigrc.users.domain.exceptions;

public class KeycloakRoleNotFoundException extends DomainException {
    public KeycloakRoleNotFoundException(String roleName) {
        super("KEYCLOAK_ROLE_NOT_FOUND", "El rol no existe en Keycloak: " + roleName);
    }
}
