package pe.edu.vallegrande.sigrc.users.application.usecase;

import lombok.RequiredArgsConstructor;
import pe.edu.vallegrande.sigrc.users.application.dto.response.UsersResponse;
import pe.edu.vallegrande.sigrc.users.application.mappers.UsersMapper;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.NotFoundException;
import pe.edu.vallegrande.sigrc.users.domain.model.Users;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.IGetUsersUseCase;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IRolesRepository;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IUserRolesRepository;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IUsersRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class GetUsersUseCaseImpl implements IGetUsersUseCase {
    private final IUsersRepository repository;
    private final IRolesRepository rolesRepository;
    private final IUserRolesRepository userRolesRepository;

    @Override
    public Flux<UsersResponse> getAll() {
        return repository.findAll()
                .flatMap(this::toResponseWithRoles);
    }

    @Override
    public Mono<UsersResponse> getById(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Users", id)))
                .flatMap(this::toResponseWithRoles);
    }

    @Override
    public Flux<UsersResponse> getByStatus(String status) {
        return repository.findByStatus(status)
                .flatMap(this::toResponseWithRoles);
    }

    private Mono<UsersResponse> toResponseWithRoles(Users users) {
        return userRolesRepository.findByUserId(users.getUserId())
                .flatMap(userRole -> rolesRepository.findById(userRole.getRoleId()).map(role -> role.getName()))
                .collectList()
                .map((List<String> roleNames) -> {
                    users.setRoles(roleNames);
                    return UsersMapper.toResponse(users);
                });
    }
}
