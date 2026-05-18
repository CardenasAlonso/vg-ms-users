package pe.edu.vallegrande.sigrc.users.application.usecase;

import lombok.RequiredArgsConstructor;
import pe.edu.vallegrande.sigrc.users.application.dto.request.CreateRolesRequest;
import pe.edu.vallegrande.sigrc.users.application.dto.response.RolesResponse;
import pe.edu.vallegrande.sigrc.users.application.mappers.RolesMapper;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.DomainException;
import pe.edu.vallegrande.sigrc.users.domain.model.Roles;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.ICreateRolesUseCase;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IRolesRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class CreateRolesUseCaseImpl implements ICreateRolesUseCase {
    private final IRolesRepository repository;

    @Override
    public Mono<RolesResponse> create(CreateRolesRequest request) {
        return repository.existsByName(request.getName())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new DomainException("ROLE_EXISTS", "El rol ya está registrado"));
                    }
                    Roles roles = Roles.builder()
                            .name(request.getName())
                            .description(request.getDescription())
                            .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    return repository.save(roles);
                })
                .map(RolesMapper::toResponse);
    }
}
