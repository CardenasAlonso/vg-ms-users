package pe.edu.vallegrande.sigrc.users.domain.ports.out;

import pe.edu.vallegrande.sigrc.users.domain.model.Users;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IUsersRepository {
    Mono<Users> save(Users users);
    Mono<Users> findById(String id);
    Mono<Users> findByEmail(String email);
    Mono<Users> findByUsername(String username);
    Flux<Users> findAll();
    Flux<Users> findByStatus(String status);
    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByDocumentNumber(String documentNumber);
    Mono<Void> deactivate(String id);  // cambia status → INACTIVE
    Mono<Void> restore(String id);     // cambia status → ACTIVE
}
