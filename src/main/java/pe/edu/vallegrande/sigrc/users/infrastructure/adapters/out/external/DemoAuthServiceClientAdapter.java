package pe.edu.vallegrande.sigrc.users.infrastructure.adapters.out.external;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.sigrc.users.domain.model.UserRole;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IAuthServiceClient;
import reactor.core.publisher.Mono;

@Component
@Profile("demo")
public class DemoAuthServiceClientAdapter implements IAuthServiceClient {
    @Override
    public Mono<Void> createUser(String userId, String username, String email, String firstName,
                                 String lastName, String password, UserRole role) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> updateUser(String currentUsername, String newUsername, String email,
                                 String firstName, String lastName, UserRole role) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> resetPassword(String username, String newPassword) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> disableUser(String username) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> enableUser(String username) {
        return Mono.empty();
    }
}