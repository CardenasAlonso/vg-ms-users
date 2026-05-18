package pe.edu.vallegrande.sigrc.users.application.usecase;

import lombok.RequiredArgsConstructor;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.NotFoundException;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.IDeactivateRolesUseCase;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IRolesRepository;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DeactivateRolesUseCaseImpl implements IDeactivateRolesUseCase {
    private final IRolesRepository repository;

    @Override
    public Mono<Void> deactivate(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Roles", id)))
                .flatMap(roles -> repository.deactivate(roles.getRoleId()));
    }
}
