package pe.edu.vallegrande.sigrc.users.domain.ports.in;

import reactor.core.publisher.Mono;

public interface IRestoreRolesUseCase {
    Mono<Void> restore(String id);
}
