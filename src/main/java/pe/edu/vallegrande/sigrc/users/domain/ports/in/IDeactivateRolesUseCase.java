package pe.edu.vallegrande.sigrc.users.domain.ports.in;

import reactor.core.publisher.Mono;

public interface IDeactivateRolesUseCase {
    Mono<Void> deactivate(String id);
}
