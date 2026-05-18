package pe.edu.vallegrande.sigrc.users.application.usecase;

import lombok.RequiredArgsConstructor;
import pe.edu.vallegrande.sigrc.users.application.dto.request.CreateRolesRequest;
import pe.edu.vallegrande.sigrc.users.application.dto.response.RolesResponse;
import pe.edu.vallegrande.sigrc.users.application.mappers.RolesMapper;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.DomainException;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.NotFoundException;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.IUpdateRolesUseCase;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IRolesRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class UpdateRolesUseCaseImpl implements IUpdateRolesUseCase {
    private final IRolesRepository repository;

    @Override
    public Mono<RolesResponse> update(String id, CreateRolesRequest request) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Roles", id)))
                .flatMap(roles -> validateName(id, request.getName())
                        .then(Mono.defer(() -> {
                            roles.setName(request.getName());
                            roles.setDescription(request.getDescription());
                            if (request.getStatus() != null) roles.setStatus(request.getStatus());
                            roles.setUpdatedAt(LocalDateTime.now());
                            return repository.save(roles);
                        })))
                .map(RolesMapper::toResponse);
    }

    private Mono<Void> validateName(String currentRoleId, String requestedName) {
        return repository.findByName(requestedName)
                .flatMap(existing -> {
                    if (!existing.getRoleId().equals(currentRoleId)) {
                        return Mono.error(new DomainException("ROLE_EXISTS", "El rol ya está registrado"));
                    }
                    return Mono.empty();
                });
    }
}
