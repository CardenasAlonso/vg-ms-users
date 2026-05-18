package pe.edu.vallegrande.sigrc.users.domain.ports.in;

import pe.edu.vallegrande.sigrc.users.application.dto.request.AssignRoleRequest;
import pe.edu.vallegrande.sigrc.users.application.dto.response.UserWithRolesResponse;
import reactor.core.publisher.Mono;

public interface IAssignRoleUseCase {
    Mono<UserWithRolesResponse> assign(AssignRoleRequest request);
}
