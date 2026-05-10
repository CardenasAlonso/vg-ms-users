package pe.edu.vallegrande.sigrc.users.domain.ports.in;

import pe.edu.vallegrande.sigrc.users.application.dto.request.CreateUsersRequest;
import pe.edu.vallegrande.sigrc.users.application.dto.response.UsersResponse;
import reactor.core.publisher.Mono;

public interface ICreateUsersUseCase {
    Mono<UsersResponse> create(CreateUsersRequest request);
}
