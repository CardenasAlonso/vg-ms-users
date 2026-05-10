package pe.edu.vallegrande.sigrc.users.domain.ports.in;

import pe.edu.vallegrande.sigrc.users.application.dto.request.UpdateUsersRequest;
import pe.edu.vallegrande.sigrc.users.application.dto.response.UsersResponse;
import reactor.core.publisher.Mono;

public interface IUpdateUsersUseCase {
    Mono<UsersResponse> update(String id, UpdateUsersRequest request);
}
