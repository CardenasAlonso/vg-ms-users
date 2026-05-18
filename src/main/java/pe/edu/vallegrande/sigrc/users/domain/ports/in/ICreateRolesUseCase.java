package pe.edu.vallegrande.sigrc.users.domain.ports.in;

import pe.edu.vallegrande.sigrc.users.application.dto.request.CreateRolesRequest;
import pe.edu.vallegrande.sigrc.users.application.dto.response.RolesResponse;
import reactor.core.publisher.Mono;

public interface ICreateRolesUseCase {
    Mono<RolesResponse> create(CreateRolesRequest request);
}
