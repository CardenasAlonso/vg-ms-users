package pe.edu.vallegrande.sigrc.users.infrastructure.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.sigrc.users.application.mappers.UsersMapper;
import pe.edu.vallegrande.sigrc.users.domain.model.Users;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IUsersRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UsersRepositoryAdapter implements IUsersRepository {

    private final UsersMongoRepository mongoRepository;

    @Override
    public Mono<Users> save(Users users) {
        return mongoRepository.save(UsersMapper.toDocument(users))
                .map(UsersMapper::toDomain);
    }

    @Override
    public Mono<Users> findById(String id) {
        return mongoRepository.findById(id)
                .map(UsersMapper::toDomain);
    }

    @Override
    public Mono<Users> findByEmail(String email) {
        return mongoRepository.findByEmail(email)
                .map(UsersMapper::toDomain);
    }

    @Override
    public Mono<Users> findByUsername(String username) {
        return mongoRepository.findByUsername(username)
                .map(UsersMapper::toDomain);
    }

    @Override
    public Flux<Users> findAll() {
        return mongoRepository.findAll()
                .map(UsersMapper::toDomain);
    }

    @Override
    public Flux<Users> findByStatus(String status) {
        return mongoRepository.findByStatus(status)
                .map(UsersMapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return mongoRepository.existsByEmail(email);
    }

    @Override
    public Mono<Boolean> existsByDocumentNumber(String documentNumber) {
        return mongoRepository.existsByDocumentNumber(documentNumber);
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
