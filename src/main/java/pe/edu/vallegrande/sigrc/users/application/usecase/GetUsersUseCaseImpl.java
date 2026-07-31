package pe.edu.vallegrande.sigrc.users.application.usecase;

import lombok.RequiredArgsConstructor;
import pe.edu.vallegrande.sigrc.users.application.dto.response.UsersResponse;
import pe.edu.vallegrande.sigrc.users.application.mappers.UsersMapper;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.NotFoundException;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.IGetUsersUseCase;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IUsersRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetUsersUseCaseImpl implements IGetUsersUseCase {
    private final IUsersRepository repository;

    @Override
    public Flux<UsersResponse> getAll() {
        return repository.findAll()
                .map(UsersMapper::toResponse);
    }

    @Override
    public Mono<UsersResponse> getById(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Users", id)))
                .map(UsersMapper::toResponse);
    }

    @Override
    public Flux<UsersResponse> getByStatus(String status) {
        return repository.findByStatus(status)
                .map(UsersMapper::toResponse);
    }
}
