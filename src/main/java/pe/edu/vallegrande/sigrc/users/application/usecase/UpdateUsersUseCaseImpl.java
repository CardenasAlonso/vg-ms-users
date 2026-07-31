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
import pe.edu.vallegrande.sigrc.users.domain.model.UserRole;
import pe.edu.vallegrande.sigrc.users.domain.model.Users;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.IUpdateUsersUseCase;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IKeycloakAdminService;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IUsersRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class UpdateUsersUseCaseImpl implements IUpdateUsersUseCase {
    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    private static final String DNI_PATTERN = "\\d{8}";
    private static final String CNE_PATTERN = "\\d{20}";

    private final IUsersRepository repository;
    private final IKeycloakAdminService keycloakAdminService;

    @Override
    public Mono<UsersResponse> update(String id, UpdateUsersRequest request) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Users", id)))
                .flatMap(users -> {
                    Users updatedUsers = applyUpdates(users, request);

                    String documentError = validateDocumentNumber(
                            updatedUsers.getDocumentType(),
                            updatedUsers.getDocumentNumber());
                    if (documentError != null) {
                        return Mono.error(new DomainException("INVALID_DOCUMENT_NUMBER", documentError));
                    }

                    return syncKeycloakBeforeUpdate(users, updatedUsers, request)
                            .then(Mono.defer(() -> {
                                updatedUsers.setUpdatedAt(LocalDateTime.now());
                                return repository.save(updatedUsers)
                                        .doOnError(error -> log.error(
                                                "Usuario actualizado en Keycloak pero falló el guardado en MongoDB, keycloakId: {}, requiere revisión manual",
                                                updatedUsers.getKeycloakId(),
                                                error));
                            }));
                })
                .map(UsersMapper::toResponse);
    }

    private Users applyUpdates(Users users, UpdateUsersRequest request) {
        Users updatedUsers = Users.builder()
                .userId(users.getUserId())
                .keycloakId(request.getKeycloakId() != null ? request.getKeycloakId() : users.getKeycloakId())
                .firstName(request.getFirstName() != null ? request.getFirstName() : users.getFirstName())
                .lastName(request.getLastName() != null ? request.getLastName() : users.getLastName())
                .documentType(request.getDocumentType() != null ? request.getDocumentType() : users.getDocumentType())
                .documentNumber(request.getDocumentNumber() != null ? request.getDocumentNumber() : users.getDocumentNumber())
                .phone(request.getPhone() != null ? request.getPhone() : users.getPhone())
                .email(request.getEmail() != null ? request.getEmail() : users.getEmail())
                .username(request.getUsername() != null ? request.getUsername() : users.getUsername())
                .password(request.getPassword() != null ? PASSWORD_ENCODER.encode(request.getPassword()) : users.getPassword())
                .profileImagePath(request.getProfileImagePath() != null ? request.getProfileImagePath() : users.getProfileImagePath())
                .role(request.getRole() != null ? request.getRole() : users.getRole())
                .lastLogin(users.getLastLogin())
                .status(users.getStatus())
                .createdAt(users.getCreatedAt())
                .updatedAt(users.getUpdatedAt())
                .build();
        return updatedUsers;
    }

    private Mono<Void> syncKeycloakBeforeUpdate(
            Users currentUsers,
            Users updatedUsers,
            UpdateUsersRequest request) {
        if (!hasKeycloakChanges(currentUsers, updatedUsers, request)) {
            return Mono.empty();
        }

        String keycloakId = updatedUsers.getKeycloakId();
        if (keycloakId == null || keycloakId.isBlank()) {
            return Mono.error(new DomainException("KEYCLOAK_ID_REQUIRED",
                    "El usuario no tiene keycloakId para sincronizar con Keycloak"));
        }

        return updateBasicDataInKeycloak(updatedUsers, request)
                .then(updateRoleInKeycloak(keycloakId, currentUsers.getRole(), updatedUsers.getRole()));
    }

    private boolean hasKeycloakChanges(
            Users currentUsers,
            Users updatedUsers,
            UpdateUsersRequest request) {
        return hasBasicKeycloakChanges(request)
                || !Objects.equals(currentUsers.getRole(), updatedUsers.getRole())
                || request.getKeycloakId() != null;
    }

    private boolean hasBasicKeycloakChanges(UpdateUsersRequest request) {
        return request.getFirstName() != null
                || request.getLastName() != null
                || request.getEmail() != null
                || request.getUsername() != null;
    }

    private Mono<Void> updateBasicDataInKeycloak(Users updatedUsers, UpdateUsersRequest request) {
        if (!hasBasicKeycloakChanges(request)) {
            return Mono.empty();
        }

        return keycloakAdminService.updateUserInKeycloak(
                updatedUsers.getKeycloakId(),
                updatedUsers.getFirstName(),
                updatedUsers.getLastName(),
                updatedUsers.getEmail(),
                updatedUsers.getUsername());
    }

    private Mono<Void> updateRoleInKeycloak(
            String keycloakId,
            UserRole currentRole,
            UserRole updatedRole) {
        if (Objects.equals(currentRole, updatedRole)) {
            return Mono.empty();
        }

        Mono<Void> removeCurrentRole = currentRole == null
                ? Mono.empty()
                : keycloakAdminService.removeRealmRoleFromUser(keycloakId, currentRole.name());

        return removeCurrentRole
                .then(keycloakAdminService.assignRealmRoleToUser(keycloakId, updatedRole.name()))
                .doOnError(error -> log.error(
                        "Falló la sincronización de rol en Keycloak, keycloakId: {}, requiere revisión manual",
                        keycloakId,
                        error));
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
