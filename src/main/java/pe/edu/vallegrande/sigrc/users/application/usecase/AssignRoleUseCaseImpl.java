package pe.edu.vallegrande.sigrc.users.application.usecase;

import lombok.RequiredArgsConstructor;
import pe.edu.vallegrande.sigrc.users.application.dto.request.AssignRoleRequest;
import pe.edu.vallegrande.sigrc.users.application.dto.response.UserWithRolesResponse;
import pe.edu.vallegrande.sigrc.users.application.mappers.UserRolesMapper;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.DomainException;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.NotFoundException;
import pe.edu.vallegrande.sigrc.users.domain.model.UserRoles;
import pe.edu.vallegrande.sigrc.users.domain.model.Users;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.IAssignRoleUseCase;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IRolesRepository;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IUserRolesRepository;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IUsersRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class AssignRoleUseCaseImpl implements IAssignRoleUseCase {
    private final IUsersRepository usersRepository;
    private final IRolesRepository rolesRepository;
    private final IUserRolesRepository userRolesRepository;

    @Override
    public Mono<UserWithRolesResponse> assign(AssignRoleRequest request) {
        Mono<Users> userMono = usersRepository.findById(request.getUserId())
                .switchIfEmpty(Mono.error(new NotFoundException("Users", request.getUserId())));

        return userMono
                .zipWith(rolesRepository.findById(request.getRoleId())
                        .switchIfEmpty(Mono.error(new NotFoundException("Roles", request.getRoleId()))))
                .flatMap(tuple -> {
                    if (!"ACTIVE".equals(tuple.getT2().getStatus())) {
                        return Mono.error(new DomainException("ROLE_INACTIVE", "No se puede asignar un rol inactivo"));
                    }
                    return userRolesRepository.existsByUserId(request.getUserId())
                            .flatMap(exists -> {
                                if (exists) {
                                    return Mono.error(new DomainException("USER_ALREADY_HAS_ROLE",
                                            "El usuario ya tiene un rol asignado"));
                                }
                                UserRoles userRoles = UserRoles.builder()
                                        .userId(request.getUserId())
                                        .roleId(request.getRoleId())
                                        .assignedAt(LocalDateTime.now())
                                        .assignedBy(request.getAssignedBy())
                                        .build();
                                return userRolesRepository.save(userRoles)
                                        .thenReturn(tuple.getT1());
                            });
                })
                .flatMap(this::withRoles);
    }

    private Mono<UserWithRolesResponse> withRoles(Users users) {
        return userRolesRepository.findByUserId(users.getUserId())
                .flatMap(userRole -> rolesRepository.findById(userRole.getRoleId()).map(role -> role.getName()))
                .collectList()
                .map((List<String> roleNames) -> UserRolesMapper.toUserWithRolesResponse(users, roleNames));
    }
}
