package pe.edu.vallegrande.sigrc.users.domain.ports.out;

import pe.edu.vallegrande.sigrc.users.domain.model.UserRole;
import reactor.core.publisher.Mono;

public interface IAuthServiceClient {

    Mono<Void> createUser(String userId, String username, String email, String firstName,
                          String lastName, String password, UserRole role);

    Mono<Void> updateUser(String currentUsername, String newUsername, String email, String firstName,
                          String lastName, UserRole role);

    Mono<Void> resetPassword(String username, String newPassword);

    Mono<Void> disableUser(String username);

    Mono<Void> enableUser(String username);
}
