package pe.edu.vallegrande.sigrc.users.application.usecase;

import lombok.RequiredArgsConstructor;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.NotFoundException;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.IRestoreRolesUseCase;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IRolesRepository;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RestoreRolesUseCaseImpl implements IRestoreRolesUseCase {
    private final IRolesRepository repository;

    @Override
    public Mono<Void> restore(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Roles", id)))
                .flatMap(roles -> repository.restore(roles.getRoleId()));
    }
}
