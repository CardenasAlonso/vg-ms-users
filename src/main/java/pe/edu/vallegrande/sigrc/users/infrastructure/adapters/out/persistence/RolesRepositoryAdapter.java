package pe.edu.vallegrande.sigrc.users.infrastructure.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.sigrc.users.application.mappers.RolesMapper;
import pe.edu.vallegrande.sigrc.users.domain.model.Roles;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IRolesRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RolesRepositoryAdapter implements IRolesRepository {

    private final RolesMongoRepository mongoRepository;

    @Override
    public Mono<Roles> save(Roles roles) {
        return mongoRepository.save(RolesMapper.toDocument(roles))
                .map(RolesMapper::toDomain);
    }

    @Override
    public Mono<Roles> findById(String id) {
        return mongoRepository.findById(id)
                .map(RolesMapper::toDomain);
    }

    @Override
    public Mono<Roles> findByName(String name) {
        return mongoRepository.findByName(name)
                .map(RolesMapper::toDomain);
    }

    @Override
    public Flux<Roles> findAll() {
        return mongoRepository.findAll()
                .map(RolesMapper::toDomain);
    }

    @Override
    public Flux<Roles> findByStatus(String status) {
        return mongoRepository.findByStatus(status)
                .map(RolesMapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return mongoRepository.existsByName(name);
    }

    @Override
    public Mono<Void> deactivate(String id) {
        return mongoRepository.findById(id)
                .flatMap(doc -> {
                    doc.setStatus("INACTIVE");
                    doc.setUpdatedAt(LocalDateTime.now());
                    return mongoRepository.save(doc);
                })
                .then();
    }

    @Override
    public Mono<Void> restore(String id) {
        return mongoRepository.findById(id)
                .flatMap(doc -> {
                    doc.setStatus("ACTIVE");
                    doc.setUpdatedAt(LocalDateTime.now());
                    return mongoRepository.save(doc);
                })
                .then();
    }
}
