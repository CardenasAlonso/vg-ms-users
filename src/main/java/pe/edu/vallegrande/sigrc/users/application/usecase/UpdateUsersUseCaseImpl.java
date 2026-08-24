package pe.edu.vallegrande.sigrc.users.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.edu.vallegrande.sigrc.users.application.dto.request.UpdateUsersRequest;
import pe.edu.vallegrande.sigrc.users.application.dto.response.UsersResponse;
import pe.edu.vallegrande.sigrc.users.application.mappers.UsersMapper;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.DomainException;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.NotFoundException;
import pe.edu.vallegrande.sigrc.users.domain.model.Users;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.IUpdateUsersUseCase;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IAuthServiceClient;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IUsersRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
public class UpdateUsersUseCaseImpl implements IUpdateUsersUseCase {
    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    private static final String DNI_PATTERN = "\\d{8}";
    private static final String CNE_PATTERN = "\\d{20}";

    private final IUsersRepository repository;
    private final IAuthServiceClient authServiceClient;
    private final pe.edu.vallegrande.sigrc.users.domain.ports.out.IImageStoragePort imageStoragePort;

    @Override
    public Mono<UsersResponse> updateAvatar(String id, org.springframework.http.codec.multipart.FilePart filePart) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Users", id)))
                .flatMap(user -> imageStoragePort.uploadImage(filePart, user.getUserId())
                        .flatMap(imageUrl -> {
                            user.setProfileImagePath(imageUrl);
                            user.setUpdatedAt(LocalDateTime.now());
                            return repository.save(user);
                        }))
                .map(UsersMapper::toResponse);
    }

    @Override
    public Mono<UsersResponse> deleteAvatar(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Users", id)))
                .flatMap(user -> {
                    if (user.getProfileImagePath() == null || user.getProfileImagePath().isBlank()) {
                        return Mono.just(user);
                    }
                    return imageStoragePort.deleteImage(user.getProfileImagePath())
                            .then(Mono.defer(() -> {
                                user.setProfileImagePath(null);
                                user.setUpdatedAt(LocalDateTime.now());
                                return repository.save(user);
                            }));
                })
                .map(UsersMapper::toResponse);
    }

    @Override
    public Mono<Void> updateLastLogin(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Users", id)))
                .flatMap(user -> {
                    user.setLastLogin(LocalDateTime.now());
                    return repository.save(user);
                })
                .then();
    }

    @Override
    public Mono<UsersResponse> update(String id, UpdateUsersRequest request) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Users", id)))
                .flatMap(users -> {
                    String currentUsername = users.getUsername();
                    boolean passwordChanged = request.getPassword() != null && !request.getPassword().isBlank();

                    Users updatedUsers = applyUpdates(users, request);

                    String documentError = validateDocumentNumber(
                            updatedUsers.getDocumentType(),
                            updatedUsers.getDocumentNumber());
                    if (documentError != null) {
                        return Mono.error(new DomainException("INVALID_DOCUMENT_NUMBER", documentError));
                    }

                    if (passwordChanged) {
                        updatedUsers.setPassword(PASSWORD_ENCODER.encode(request.getPassword()));
                    }

                    updatedUsers.setUpdatedAt(LocalDateTime.now());
                    return authServiceClient.updateUser(
                                    currentUsername, updatedUsers.getUsername(), updatedUsers.getEmail(),
                                    updatedUsers.getFirstName(), updatedUsers.getLastName(), updatedUsers.getRole())
                            .then(passwordChanged
                                    ? authServiceClient.resetPassword(updatedUsers.getUsername(), request.getPassword())
                                    : Mono.empty())
                            .then(repository.save(updatedUsers));
                })
                .map(UsersMapper::toResponse);
    }

    private Users applyUpdates(Users users, UpdateUsersRequest request) {
        Users updatedUsers = Users.builder()
                .userId(users.getUserId())
                .firstName(request.getFirstName() != null ? request.getFirstName() : users.getFirstName())
                .lastName(request.getLastName() != null ? request.getLastName() : users.getLastName())
                .documentType(request.getDocumentType() != null ? request.getDocumentType() : users.getDocumentType())
                .documentNumber(request.getDocumentNumber() != null ? request.getDocumentNumber() : users.getDocumentNumber())
                .phone(request.getPhone() != null ? request.getPhone() : users.getPhone())
                .email(request.getEmail() != null ? request.getEmail() : users.getEmail())
                .username(request.getUsername() != null ? request.getUsername() : users.getUsername())
                .password(users.getPassword())
                .profileImagePath(request.getProfileImagePath() != null ? request.getProfileImagePath() : users.getProfileImagePath())
                .role(request.getRole() != null ? request.getRole() : users.getRole())
                .lastLogin(users.getLastLogin())
                .status(users.getStatus())
                .createdAt(users.getCreatedAt())
                .updatedAt(users.getUpdatedAt())
                .build();
        return updatedUsers;
    }

    private String validateDocumentNumber(String documentType, String documentNumber) {
        if (documentType == null || documentNumber == null) {
            return null;
        }

        return switch (documentType) {
            case "DNI" -> documentNumber.matches(DNI_PATTERN) ? null : "El DNI debe tener 8 dígitos";
            case "CNE" -> documentNumber.matches(CNE_PATTERN) ? null : "El CNE debe tener 20 dígitos";
            default -> null;
        };
    }
}
