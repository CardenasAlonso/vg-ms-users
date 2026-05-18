package pe.edu.vallegrande.sigrc.users.domain.ports.in;

import pe.edu.vallegrande.sigrc.users.application.dto.response.RolesResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGetRolesUseCase {
    Flux<RolesResponse> getAll();
    Mono<RolesResponse> getById(String id);
    Flux<RolesResponse> getByStatus(String status);
}
