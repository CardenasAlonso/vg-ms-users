package pe.edu.vallegrande.sigrc.users.infrastructure.adapters.out.keycloak;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.DomainException;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.KeycloakIntegrationException;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.KeycloakRoleNotFoundException;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.KeycloakUserAlreadyExistsException;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IKeycloakAdminService;
import pe.edu.vallegrande.sigrc.users.infrastructure.config.KeycloakProperties;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeycloakAdminService implements IKeycloakAdminService {
    private final WebClient.Builder webClientBuilder;
    private final KeycloakProperties properties;

    @Override
    public Mono<String> getServiceAccountToken() {
        return webClientBuilder.build()
                .post()
                .uri(tokenUri())
                .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                        .with("client_id", properties.getClientId())
                        .with("client_secret", properties.getClientSecret()))
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                                .flatMap(body -> {
                                    Object accessToken = body.get("access_token");
                                    if (accessToken instanceof String token && !token.isBlank()) {
                                        return Mono.just(token);
                                    }
                                    return Mono.error(new KeycloakIntegrationException(
                                            "Keycloak no devolvió access_token"));
                                });
                    }

                    return response.bodyToMono(String.class)
                            .defaultIfEmpty("")
                            .flatMap(body -> Mono.error(new KeycloakIntegrationException(
                                    "Error obteniendo token de Keycloak. Status: "
                                            + response.statusCode().value()
                                            + ", body: " + body)));
                })
                .onErrorMap(ex -> !(ex instanceof DomainException),
                        ex -> new KeycloakIntegrationException(
                                "Servicio de autenticación no disponible, intente más tarde"));
    }

    @Override
    public Mono<Map<String, Object>> getRealmRoleByName(String roleName) {
        return getServiceAccountToken()
                .flatMap(token -> webClientBuilder.build()
                        .get()
                        .uri(realmRoleUri(roleName))
                        .headers(headers -> headers.setBearerAuth(token))
                        .exchangeToMono(response -> {
                            if (response.statusCode().is2xxSuccessful()) {
                                return response.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
                            }

                            if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                                return Mono.error(new KeycloakRoleNotFoundException(roleName));
                            }

                            return response.bodyToMono(String.class)
                                    .defaultIfEmpty("")
                                    .flatMap(body -> Mono.error(new KeycloakIntegrationException(
                                            "Error obteniendo rol de Keycloak. Status: "
                                                    + response.statusCode().value()
                                                    + ", body: " + body)));
                        }))
                .onErrorMap(ex -> !(ex instanceof DomainException),
                        ex -> new KeycloakIntegrationException(
                                "Servicio de autenticación no disponible, intente más tarde"));
    }

    @Override
    public Mono<Void> assignRealmRoleToUser(String keycloakUserId, String roleName) {
        return getRealmRoleByName(roleName)
                .flatMap(role -> getServiceAccountToken()
                        .flatMap(token -> webClientBuilder.build()
                                .post()
                                .uri(userRealmRoleMappingsUri(keycloakUserId))
                                .headers(headers -> headers.setBearerAuth(token))
                                .bodyValue(List.of(role))
                                .exchangeToMono(response -> handleEmptyResponse(
                                        response.statusCode().is2xxSuccessful(),
                                        response.statusCode().value(),
                                        response.bodyToMono(String.class),
                                        "Error asignando rol en Keycloak"))))
                .onErrorMap(ex -> !(ex instanceof DomainException),
                        ex -> new KeycloakIntegrationException(
                                "Servicio de autenticación no disponible, intente más tarde"));
    }

    @Override
    public Mono<Void> removeRealmRoleFromUser(String keycloakUserId, String roleName) {
        return getRealmRoleByName(roleName)
                .flatMap(role -> getServiceAccountToken()
                        .flatMap(token -> webClientBuilder.build()
                                .method(HttpMethod.DELETE)
                                .uri(userRealmRoleMappingsUri(keycloakUserId))
                                .headers(headers -> headers.setBearerAuth(token))
                                .bodyValue(List.of(role))
                                .exchangeToMono(response -> handleEmptyResponse(
                                        response.statusCode().is2xxSuccessful(),
                                        response.statusCode().value(),
                                        response.bodyToMono(String.class),
                                        "Error quitando rol en Keycloak"))))
                .onErrorMap(ex -> !(ex instanceof DomainException),
                        ex -> new KeycloakIntegrationException(
                                "Servicio de autenticación no disponible, intente más tarde"));
    }

    @Override
    public Mono<Void> setUserEnabled(String keycloakUserId, boolean enabled) {
        return getServiceAccountToken()
                .flatMap(token -> webClientBuilder.build()
                        .put()
                        .uri(userUri(keycloakUserId))
                        .headers(headers -> headers.setBearerAuth(token))
                        .bodyValue(Map.of("enabled", enabled))
                        .exchangeToMono(response -> handleEmptyResponse(
                                response.statusCode().is2xxSuccessful(),
                                response.statusCode().value(),
                                response.bodyToMono(String.class),
                                "Error actualizando estado de usuario en Keycloak")))
                .onErrorMap(ex -> !(ex instanceof DomainException),
                        ex -> new KeycloakIntegrationException(
                                "Servicio de autenticación no disponible, intente más tarde"));
    }

    @Override
    public Mono<Void> resetUserPassword(String keycloakUserId, String newPassword) {
        return getServiceAccountToken()
                .flatMap(token -> webClientBuilder.build()
                        .put()
                        .uri(userResetPasswordUri(keycloakUserId))
                        .headers(headers -> headers.setBearerAuth(token))
                        .bodyValue(Map.of(
                                "type", "password",
                                "value", newPassword,
                                "temporary", false
                        ))
                        .exchangeToMono(response -> handleEmptyResponse(
                                response.statusCode().is2xxSuccessful(),
                                response.statusCode().value(),
                                response.bodyToMono(String.class),
                                "Error actualizando contraseña de usuario en Keycloak")))
                .onErrorMap(ex -> !(ex instanceof DomainException),
                        ex -> new KeycloakIntegrationException(
                                "Servicio de autenticación no disponible, intente más tarde"));
    }

    @Override
    public Mono<Void> updateUserInKeycloak(
            String keycloakUserId,
            String firstName,
            String lastName,
            String email,
            String username) {
        return getServiceAccountToken()
                .flatMap(token -> webClientBuilder.build()
                        .put()
                        .uri(userUri(keycloakUserId))
                        .headers(headers -> headers.setBearerAuth(token))
                        .bodyValue(Map.of(
                                "firstName", firstName,
                                "lastName", lastName,
                                "email", email,
                                "username", username
                        ))
                        .exchangeToMono(response -> {
                            if (response.statusCode().equals(HttpStatus.CONFLICT)) {
                                return Mono.error(new KeycloakUserAlreadyExistsException());
                            }
                            return handleEmptyResponse(
                                    response.statusCode().is2xxSuccessful(),
                                    response.statusCode().value(),
                                    response.bodyToMono(String.class),
                                    "Error actualizando usuario en Keycloak");
                        }))
                .onErrorMap(ex -> !(ex instanceof DomainException),
                        ex -> new KeycloakIntegrationException(
                                "Servicio de autenticación no disponible, intente más tarde"));
    }

    @Override
    public Mono<String> createUserInKeycloak(
            String username,
            String email,
            String firstName,
            String lastName,
            String password) {
        return getServiceAccountToken()
                .flatMap(token -> webClientBuilder.build()
                        .post()
                        .uri(usersUri())
                        .headers(headers -> headers.setBearerAuth(token))
                        .bodyValue(userBody(username, email, firstName, lastName, password))
                        .exchangeToMono(response -> {
                            if (response.statusCode().equals(HttpStatus.CREATED)) {
                                URI location = response.headers().asHttpHeaders().getLocation();
                                if (location == null) {
                                    return Mono.error(new KeycloakIntegrationException(
                                            "Keycloak creó el usuario pero no devolvió header Location"));
                                }
                                return Mono.just(extractKeycloakId(location));
                            }

                            if (response.statusCode().equals(HttpStatus.CONFLICT)) {
                                return Mono.error(new KeycloakUserAlreadyExistsException());
                            }

                            return response.bodyToMono(String.class)
                                    .defaultIfEmpty("")
                                    .flatMap(body -> Mono.error(new KeycloakIntegrationException(
                                            "Error creando usuario en Keycloak. Status: "
                                                    + response.statusCode().value()
                                                    + ", body: " + body)));
                        }))
                .onErrorMap(ex -> !(ex instanceof DomainException),
                        ex -> new KeycloakIntegrationException(
                                "Servicio de autenticación no disponible, intente más tarde"));
    }

    private String tokenUri() {
        return properties.getBaseUrl() + "/realms/" + properties.getRealm()
                + "/protocol/openid-connect/token";
    }

    private String usersUri() {
        return properties.getBaseUrl() + "/admin/realms/" + properties.getRealm() + "/users";
    }

    private String userUri(String keycloakUserId) {
        return usersUri() + "/" + keycloakUserId;
    }

    private String realmRoleUri(String roleName) {
        return properties.getBaseUrl() + "/admin/realms/" + properties.getRealm()
                + "/roles/" + UriUtils.encodePathSegment(roleName, StandardCharsets.UTF_8);
    }

    private String userRealmRoleMappingsUri(String keycloakUserId) {
        return userUri(keycloakUserId) + "/role-mappings/realm";
    }

    private String userResetPasswordUri(String keycloakUserId) {
        return userUri(keycloakUserId) + "/reset-password";
    }

    private Map<String, Object> userBody(
            String username,
            String email,
            String firstName,
            String lastName,
            String password) {
        return Map.of(
                "username", username,
                "email", email,
                "emailVerified", true,
                "firstName", firstName,
                "lastName", lastName,
                "enabled", true,
                "credentials", List.of(Map.of(
                        "type", "password",
                        "value", password,
                        "temporary", false
                ))
        );
    }

    private String extractKeycloakId(URI location) {
        String path = location.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

    private Mono<Void> handleEmptyResponse(
            boolean successful,
            int statusCode,
            Mono<String> responseBody,
            String errorMessage) {
        if (successful) {
            return Mono.empty();
        }

        return responseBody
                .defaultIfEmpty("")
                .flatMap(body -> Mono.error(new KeycloakIntegrationException(
                        errorMessage + ". Status: " + statusCode + ", body: " + body)));
    }
}
