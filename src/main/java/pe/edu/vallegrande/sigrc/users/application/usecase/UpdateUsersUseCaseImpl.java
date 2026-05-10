package pe.edu.vallegrande.sigrc.users.application.usecase;

import lombok.RequiredArgsConstructor;
import pe.edu.vallegrande.sigrc.users.application.dto.request.UpdateUsersRequest;
import pe.edu.vallegrande.sigrc.users.application.dto.response.UsersResponse;
import pe.edu.vallegrande.sigrc.users.application.mappers.UsersMapper;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.NotFoundException;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.IUpdateUsersUseCase;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IUsersRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class UpdateUsersUseCaseImpl implements IUpdateUsersUseCase {
    private final IUsersRepository repository;

    @Override
    public Mono<UsersResponse> update(String id, UpdateUsersRequest request) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Users", id)))
                .flatMap(users -> {
                    if (request.getFirstName() != null) users.setFirstName(request.getFirstName());
                    if (request.getLastName() != null) users.setLastName(request.getLastName());
                    if (request.getPhone() != null) users.setPhone(request.getPhone());
                    if (request.getEmail() != null) users.setEmail(request.getEmail());
                    if (request.getDocumentType() != null) users.setDocumentType(request.getDocumentType());
                    if (request.getDocumentNumber() != null) users.setDocumentNumber(request.getDocumentNumber());
                    if (request.getRole() != null) users.setRole(request.getRole());
                    if (request.getProfileImagePath() != null) users.setProfileImagePath(request.getProfileImagePath());
                    if (request.getFirebaseId() != null) users.setFirebaseId(request.getFirebaseId());
                    if (request.getPassword() != null) users.setPassword(request.getPassword());
                    users.setUpdatedAt(LocalDateTime.now());
                    return repository.save(users);
                })
                .map(UsersMapper::toResponse);
    }
}
