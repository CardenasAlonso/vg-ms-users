package pe.edu.vallegrande.sigrc.users.domain.ports.out;

import pe.edu.vallegrande.sigrc.users.domain.model.Roles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IRolesRepository {
    Mono<Roles> save(Roles roles);
    Mono<Roles> findById(String id);
    Mono<Roles> findByName(String name);
    Flux<Roles> findAll();
    Flux<Roles> findByStatus(String status);
    Mono<Boolean> existsByName(String name);
    Mono<Void> deactivate(String id);
    Mono<Void> restore(String id);
}
