package pe.edu.vallegrande.sigrc.users.application.usecase;

import lombok.RequiredArgsConstructor;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.NotFoundException;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.IDeactivateUsersUseCase;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IAuthServiceClient;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IUsersRepository;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DeactivateUsersUseCaseImpl implements IDeactivateUsersUseCase {
    private final IUsersRepository repository;
    private final IAuthServiceClient authServiceClient;

    @Override
    public Mono<Void> deactivate(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Users", id)))
                .flatMap(users -> authServiceClient.disableUser(users.getUsername())
                        .then(repository.deactivate(users.getUserId())));
    }
}
