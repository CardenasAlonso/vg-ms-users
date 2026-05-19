package pe.edu.vallegrande.sigrc.users.domain.ports.in;

import pe.edu.vallegrande.sigrc.users.application.dto.response.UserWithRolesResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGetUserRolesUseCase {
    Mono<UserWithRolesResponse> getByUserId(String userId);
    Flux<UserWithRolesResponse> getByRoleId(String roleId);
    Flux<UserWithRolesResponse> getUnassignedUsers();
    Flux<UserWithRolesResponse> getAllUsersWithRoles();
}
