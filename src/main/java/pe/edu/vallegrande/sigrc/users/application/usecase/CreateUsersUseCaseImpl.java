package pe.edu.vallegrande.sigrc.users.application.usecase;

import lombok.RequiredArgsConstructor;
import pe.edu.vallegrande.sigrc.users.application.dto.request.CreateUsersRequest;
import pe.edu.vallegrande.sigrc.users.application.dto.response.UsersResponse;
import pe.edu.vallegrande.sigrc.users.application.mappers.UsersMapper;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.DomainException;
import pe.edu.vallegrande.sigrc.users.domain.model.Users;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.ICreateUsersUseCase;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IUsersRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class CreateUsersUseCaseImpl implements ICreateUsersUseCase {
    private final IUsersRepository repository;

    @Override
    public Mono<UsersResponse> create(CreateUsersRequest request) {
        return repository.existsByEmail(request.getEmail())
                .flatMap(emailExists -> {
                    if (emailExists) {
                        return Mono.error(new DomainException("EMAIL_EXISTS",
                                "El email ya está registrado"));
                    }
                    return repository.existsByDocumentNumber(request.getDocumentNumber());
                })
                .flatMap(docExists -> {
                    if (docExists) {
                        return Mono.error(new DomainException("DOCUMENT_EXISTS",
                                "El número de documento ya está registrado"));
                    }
                    Users users = Users.builder()
                            .firebaseId(request.getFirebaseId())
                            .firstName(request.getFirstName())
                            .lastName(request.getLastName())
                            .documentType(request.getDocumentType())
                            .documentNumber(request.getDocumentNumber())
                            .phone(request.getPhone())
                            .email(request.getEmail())
                            .username(request.getUsername())
                            .password(request.getPassword()) // ⚠️ encriptar antes en producción
                            .role(request.getRole())
                            .profileImagePath(request.getProfileImagePath())
                            .status("ACTIVE")
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    return repository.save(users);
                })
                .map(UsersMapper::toResponse);
    }
}
