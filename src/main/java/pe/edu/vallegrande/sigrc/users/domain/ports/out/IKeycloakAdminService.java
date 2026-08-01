package pe.edu.vallegrande.sigrc.users.domain.ports.out;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface IKeycloakAdminService {
    Mono<String> getServiceAccountToken();

    Mono<String> createUserInKeycloak(
            String username,
            String email,
            String firstName,
            String lastName,
            String password);

    Mono<Map<String, Object>> getRealmRoleByName(String roleName);

    Mono<Void> assignRealmRoleToUser(String keycloakUserId, String roleName);

    Mono<Void> removeRealmRoleFromUser(String keycloakUserId, String roleName);

    Mono<Void> setUserEnabled(String keycloakUserId, boolean enabled);

    Mono<Void> resetUserPassword(String keycloakUserId, String newPassword);

    Mono<Void> updateUserInKeycloak(
            String keycloakUserId,
            String firstName,
            String lastName,
            String email,
            String username);
}
