package pe.edu.vallegrande.sigrc.users.application.usecase;

import lombok.RequiredArgsConstructor;
import pe.edu.vallegrande.sigrc.users.application.dto.response.RolesResponse;
import pe.edu.vallegrande.sigrc.users.application.mappers.RolesMapper;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.NotFoundException;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.IGetRolesUseCase;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IRolesRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetRolesUseCaseImpl implements IGetRolesUseCase {
    private final IRolesRepository repository;

    @Override
    public Flux<RolesResponse> getAll() {
        return repository.findAll()
                .map(RolesMapper::toResponse);
    }

    @Override
    public Mono<RolesResponse> getById(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Roles", id)))
                .map(RolesMapper::toResponse);
    }

    @Override
    public Flux<RolesResponse> getByStatus(String status) {
        return repository.findByStatus(status)
                .map(RolesMapper::toResponse);
    }
}
