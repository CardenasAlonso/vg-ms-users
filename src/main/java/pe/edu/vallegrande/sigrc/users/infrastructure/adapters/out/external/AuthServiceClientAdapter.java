package pe.edu.vallegrande.sigrc.users.infrastructure.adapters.out.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.KeycloakIntegrationException;
import pe.edu.vallegrande.sigrc.users.domain.model.UserRole;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IAuthServiceClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AuthServiceClientAdapter implements IAuthServiceClient {

    private static final String INTERNAL_API_KEY_HEADER = "X-Internal-Key";
    private static final String BASE_PATH = "/api/auth/internal/users";

    private final WebClient webClient;
    private final String internalApiKey;

    public AuthServiceClientAdapter(WebClient.Builder webClientBuilder,
                                    @Value("${auth-service.url}") String authServiceUrl,
                                    @Value("${auth-service.internal-api-key}") String internalApiKey) {
        this.webClient = webClientBuilder.baseUrl(authServiceUrl).build();
        this.internalApiKey = internalApiKey;
    }

    @Override
    public Mono<Void> createUser(String userId, String username, String email, String firstName,
                                 String lastName, String password, UserRole role) {
        Map<String, Object> body = new HashMap<>();
        body.put("userId", userId);
        body.put("username", username);
        body.put("email", email);
        body.put("firstName", firstName);
        body.put("lastName", lastName);
        body.put("password", password);
        body.put("role", role != null ? role.name() : null);

        return post(BASE_PATH, body, "creando usuario en Keycloak: " + username);
    }

    @Override
    public Mono<Void> updateUser(String currentUsername, String newUsername, String email, String firstName,
                                 String lastName, UserRole role) {
        Map<String, Object> body = new HashMap<>();
        body.put("username", newUsername);
        body.put("email", email);
        body.put("firstName", firstName);
        body.put("lastName", lastName);
        body.put("role", role != null ? role.name() : null);

        return put(BASE_PATH + "/" + currentUsername, body, "actualizando usuario en Keycloak: " + currentUsername);
    }

    @Override
    public Mono<Void> resetPassword(String username, String newPassword) {
        Map<String, Object> body = new HashMap<>();
        body.put("newPassword", newPassword);

        return put(BASE_PATH + "/" + username + "/password", body,
                "cambiando contraseña en Keycloak: " + username);
    }

    @Override
    public Mono<Void> disableUser(String username) {
        return put(BASE_PATH + "/" + username + "/disable", Map.of(),
                "deshabilitando usuario en Keycloak: " + username);
    }

    @Override
    public Mono<Void> enableUser(String username) {
        return put(BASE_PATH + "/" + username + "/enable", Map.of(),
                "habilitando usuario en Keycloak: " + username);
    }

    private Mono<Void> post(String uri, Map<String, Object> body, String action) {
        return webClient.post()
                .uri(uri)
                .header(INTERNAL_API_KEY_HEADER, internalApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .then()
                .doOnSuccess(v -> log.info("Auth OK: {}", action))
                .doOnError(e -> log.error("Auth fallido {}: {}", action, e.getMessage()))
                .onErrorMap(e -> new KeycloakIntegrationException("No se pudo sincronizar con Keycloak " + action));
    }

    private Mono<Void> put(String uri, Map<String, Object> body, String action) {
        return webClient.put()
                .uri(uri)
                .header(INTERNAL_API_KEY_HEADER, internalApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .then()
                .doOnSuccess(v -> log.info("Auth OK: {}", action))
                .doOnError(e -> log.error("Auth fallido {}: {}", action, e.getMessage()))
                .onErrorMap(e -> new KeycloakIntegrationException("No se pudo sincronizar con Keycloak " + action));
    }
}
