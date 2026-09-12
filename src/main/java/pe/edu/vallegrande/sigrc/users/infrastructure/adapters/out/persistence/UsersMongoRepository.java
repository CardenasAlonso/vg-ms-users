package pe.edu.vallegrande.sigrc.users.infrastructure.adapters.out.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.context.annotation.Profile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Profile("!demo")
public interface UsersMongoRepository extends ReactiveMongoRepository<UsersDocument, String> {
    Mono<UsersDocument> findByEmailHash(String emailHash);
    Mono<UsersDocument> findByUsername(String username);
    Flux<UsersDocument> findByStatus(String status);
    Mono<Boolean> existsByEmailHash(String emailHash);
    Mono<Boolean> existsByDocumentNumber(String documentNumber);
}
