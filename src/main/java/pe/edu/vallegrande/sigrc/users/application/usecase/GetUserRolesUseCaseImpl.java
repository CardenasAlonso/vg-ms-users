package pe.edu.vallegrande.sigrc.users.application.usecase;

import lombok.RequiredArgsConstructor;
import pe.edu.vallegrande.sigrc.users.application.dto.response.UserWithRolesResponse;
import pe.edu.vallegrande.sigrc.users.application.mappers.UserRolesMapper;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.NotFoundException;
import pe.edu.vallegrande.sigrc.users.domain.model.UserRoles;
import pe.edu.vallegrande.sigrc.users.domain.model.Users;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.IGetUserRolesUseCase;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IRolesRepository;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IUserRolesRepository;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IUsersRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class GetUserRolesUseCaseImpl implements IGetUserRolesUseCase {
    private final IUsersRepository usersRepository;
    private final IRolesRepository rolesRepository;
    private final IUserRolesRepository userRolesRepository;

    @Override
    public Mono<UserWithRolesResponse> getByUserId(String userId) {
        return usersRepository.findById(userId)
                .switchIfEmpty(Mono.error(new NotFoundException("Users", userId)))
                .flatMap(this::withRoles);
    }

    @Override
    public Flux<UserWithRolesResponse> getByRoleId(String roleId) {
        return rolesRepository.findById(roleId)
                .switchIfEmpty(Mono.error(new NotFoundException("Roles", roleId)))
                .thenMany(userRolesRepository.findByRoleId(roleId))
                .flatMap(userRole -> usersRepository.findById(userRole.getUserId())
                        .flatMap(this::withRoles));
    }

    @Override
    public Flux<UserWithRolesResponse> getUnassignedUsers() {
        return usersRepository.findAll()
                .flatMap(users -> userRolesRepository.existsByUserId(users.getUserId())
                        .filter(Boolean.FALSE::equals)
                        .map(ignored -> UserRolesMapper.toUserWithRolesResponse(users, List.of())));
    }

    @Override
    public Flux<UserWithRolesResponse> getAllUsersWithRoles() {
        return usersRepository.findAll()
                .flatMap(this::withRoles);
    }

    private Mono<UserWithRolesResponse> withRoles(Users users) {
        return roleNamesByUserId(users.getUserId())
                .map(roleNames -> UserRolesMapper.toUserWithRolesResponse(users, roleNames));
    }

    private Mono<List<String>> roleNamesByUserId(String userId) {
        return userRolesRepository.findByUserId(userId)
                .map(UserRoles::getRoleId)
                .flatMap(roleId -> rolesRepository.findById(roleId)
                        .map(role -> role.getName()))
                .collectList();
    }
}
