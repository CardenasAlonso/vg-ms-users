package pe.edu.vallegrande.sigrc.users.domain.ports.out;

import pe.edu.vallegrande.sigrc.users.domain.model.UserRoles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IUserRolesRepository {
    Mono<UserRoles> save(UserRoles userRoles);
    Flux<UserRoles> findByUserId(String userId);
    Flux<UserRoles> findByRoleId(String roleId);
    Mono<Boolean> existsByUserId(String userId);
    Mono<Boolean> existsByUserIdAndRoleId(String userId, String roleId);
    Mono<Void> deleteByUserIdAndRoleId(String userId, String roleId);
}
