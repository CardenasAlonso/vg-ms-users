package pe.edu.vallegrande.sigrc.users.domain.ports.in;

import reactor.core.publisher.Mono;

public interface IDeactivateUsersUseCase {
    Mono<Void> deactivate(String id);
}
