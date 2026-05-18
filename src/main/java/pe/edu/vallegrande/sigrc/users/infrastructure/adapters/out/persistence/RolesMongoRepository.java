package pe.edu.vallegrande.sigrc.users.infrastructure.adapters.out.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RolesMongoRepository extends ReactiveMongoRepository<RolesDocument, String> {
    Mono<RolesDocument> findByName(String name);
    Flux<RolesDocument> findByStatus(String status);
    Mono<Boolean> existsByName(String name);
}
