package pe.edu.vallegrande.sigrc.users.application.usecase;

import lombok.RequiredArgsConstructor;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.DomainException;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.NotFoundException;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.IRestoreUsersUseCase;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IKeycloakAdminService;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IUsersRepository;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RestoreUsersUseCaseImpl implements IRestoreUsersUseCase {

    private final IUsersRepository repository;
    private final IKeycloakAdminService keycloakAdminService;

    @Override
    public Mono<Void> restore(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Users", id)))
                .flatMap(users -> {
                    if (users.getKeycloakId() == null || users.getKeycloakId().isBlank()) {
                        return Mono.error(new DomainException("KEYCLOAK_ID_REQUIRED",
                                "El usuario no tiene keycloakId para sincronizar con Keycloak"));
                    }
                    return keycloakAdminService.setUserEnabled(users.getKeycloakId(), true)
                            .then(repository.restore(users.getUserId()));
                });
    }
}
