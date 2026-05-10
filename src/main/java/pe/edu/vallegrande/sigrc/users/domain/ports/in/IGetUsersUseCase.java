package pe.edu.vallegrande.sigrc.users.domain.ports.in;

import pe.edu.vallegrande.sigrc.users.application.dto.response.UsersResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGetUsersUseCase {
    Flux<UsersResponse> getAll();
    Mono<UsersResponse> getById(String id);
    Flux<UsersResponse> getByStatus(String status);
}
