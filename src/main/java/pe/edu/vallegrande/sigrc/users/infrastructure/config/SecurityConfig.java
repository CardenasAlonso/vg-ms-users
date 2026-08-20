    package pe.edu.vallegrande.sigrc.users.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final String[] API_USERS = {"/api/v1/users", "/api/v1/users/**"};
    private static final String ROLES_CLAIM = "roles";

    private static final String[] ADMIN_ROLES = {"ADMIN"};

    private static final String[] SWAGGER_PATHS = {
            "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/webjars/**", "/api-docs/**"
    };

    private static final String[] ACTUATOR_PATHS = {"/actuator/**"};

    @Bean
    @Profile("dev")
    public SecurityWebFilterChain devSecurityWebFilterChain(ServerHttpSecurity http) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers(SWAGGER_PATHS).permitAll()
                        .pathMatchers(ACTUATOR_PATHS).permitAll()
                        .pathMatchers(HttpMethod.GET, API_USERS).hasAnyRole(ADMIN_ROLES)
                        .pathMatchers(HttpMethod.POST, API_USERS).hasAnyRole(ADMIN_ROLES)
                        .pathMatchers(HttpMethod.PUT, API_USERS).hasAnyRole(ADMIN_ROLES)
                        .pathMatchers(HttpMethod.PATCH, API_USERS).hasAnyRole(ADMIN_ROLES)
                        .pathMatchers(HttpMethod.DELETE, API_USERS).hasAnyRole(ADMIN_ROLES)
                        .anyExchange().permitAll())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor())))
                .build();
    }

    @Bean
    @Profile("prod")
    public SecurityWebFilterChain prodSecurityWebFilterChain(ServerHttpSecurity http) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers(SWAGGER_PATHS).denyAll()
                        .pathMatchers(ACTUATOR_PATHS).permitAll()
                        .pathMatchers(HttpMethod.GET, API_USERS).hasAnyRole(ADMIN_ROLES)
                        .pathMatchers(HttpMethod.POST, API_USERS).hasAnyRole(ADMIN_ROLES)
                        .pathMatchers(HttpMethod.PUT, API_USERS).hasAnyRole(ADMIN_ROLES)
                        .pathMatchers(HttpMethod.PATCH, API_USERS).hasAnyRole(ADMIN_ROLES)
                        .pathMatchers(HttpMethod.DELETE, API_USERS).hasAnyRole(ADMIN_ROLES)
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor())))
                .build();
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

    static class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

        @SuppressWarnings("unchecked")
        @Override
        @Nullable
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null && realmAccess.get(ROLES_CLAIM) instanceof List) {
                ((List<String>) realmAccess.get(ROLES_CLAIM)).stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r.toUpperCase()))
                        .forEach(authorities::add);
            }
            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
            if (resourceAccess != null) {
                resourceAccess.values().stream()
                        .filter(Map.class::isInstance)
                        .map(v -> (Map<String, Object>) v)
                        .filter(m -> m.get(ROLES_CLAIM) instanceof List)
                        .flatMap(m -> ((List<String>) m.get(ROLES_CLAIM)).stream())
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r.toUpperCase()))
                        .forEach(authorities::add);
            }
            return authorities;
        }
    }
}
