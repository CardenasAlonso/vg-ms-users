package pe.edu.vallegrande.sigrc.users.infrastructure.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.sigrc.users.application.mappers.UserRolesMapper;
import pe.edu.vallegrande.sigrc.users.domain.model.UserRoles;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IUserRolesRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserRolesRepositoryAdapter implements IUserRolesRepository {

    private final UserRolesMongoRepository mongoRepository;

    @Override
    public Mono<UserRoles> save(UserRoles userRoles) {
        return mongoRepository.save(UserRolesMapper.toDocument(userRoles))
                .map(UserRolesMapper::toDomain);
    }

    @Override
    public Flux<UserRoles> findByUserId(String userId) {
        return mongoRepository.findByUserId(userId)
                .map(UserRolesMapper::toDomain);
    }

    @Override
    public Flux<UserRoles> findByRoleId(String roleId) {
        return mongoRepository.findByRoleId(roleId)
                .map(UserRolesMapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByUserId(String userId) {
        return mongoRepository.existsByUserId(userId);
    }

    @Override
    public Mono<Boolean> existsByUserIdAndRoleId(String userId, String roleId) {
        return mongoRepository.existsByUserIdAndRoleId(userId, roleId);
    }

    @Override
    public Mono<Void> deleteByUserIdAndRoleId(String userId, String roleId) {
        return mongoRepository.deleteByUserIdAndRoleId(userId, roleId);
    }
}
