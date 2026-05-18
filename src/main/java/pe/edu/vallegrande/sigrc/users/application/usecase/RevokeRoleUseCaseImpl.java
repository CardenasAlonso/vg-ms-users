package pe.edu.vallegrande.sigrc.users.application.usecase;

import lombok.RequiredArgsConstructor;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.DomainException;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.NotFoundException;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.IRevokeRoleUseCase;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IRolesRepository;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IUserRolesRepository;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IUsersRepository;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RevokeRoleUseCaseImpl implements IRevokeRoleUseCase {
    private final IUsersRepository usersRepository;
    private final IRolesRepository rolesRepository;
    private final IUserRolesRepository userRolesRepository;

    @Override
    public Mono<Void> revoke(String userId, String roleId) {
        return usersRepository.findById(userId)
                .switchIfEmpty(Mono.error(new NotFoundException("Users", userId)))
                .then(rolesRepository.findById(roleId)
                        .switchIfEmpty(Mono.error(new NotFoundException("Roles", roleId))))
                .then(userRolesRepository.existsByUserIdAndRoleId(userId, roleId))
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new DomainException("ROLE_NOT_ASSIGNED",
                                "El usuario no tiene asignado este rol"));
                    }
                    return userRolesRepository.deleteByUserIdAndRoleId(userId, roleId);
                });
    }
}
