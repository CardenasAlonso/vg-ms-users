package pe.edu.vallegrande.sigrc.users.infrastructure.adapters.out.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRolesMongoRepository extends ReactiveMongoRepository<UserRolesDocument, String> {
    Flux<UserRolesDocument> findByUserId(String userId);
    Flux<UserRolesDocument> findByRoleId(String roleId);
    Mono<Boolean> existsByUserId(String userId);
    Mono<Boolean> existsByUserIdAndRoleId(String userId, String roleId);
    Mono<Void> deleteByUserIdAndRoleId(String userId, String roleId);
}
