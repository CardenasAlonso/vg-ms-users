package pe.edu.vallegrande.sigrc.users.infrastructure.adapters.out.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UsersMongoRepository extends ReactiveMongoRepository<UsersDocument, String> {
    Mono<UsersDocument> findByEmail(String email);
    Mono<UsersDocument> findByUsername(String username);
    Flux<UsersDocument> findByStatus(String status);
    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByDocumentNumber(String documentNumber);
}
