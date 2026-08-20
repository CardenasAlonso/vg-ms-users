package pe.edu.vallegrande.sigrc.users.application.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.sigrc.users.application.dto.request.UpdateUsersRequest;
import pe.edu.vallegrande.sigrc.users.application.dto.response.UsersResponse;
import pe.edu.vallegrande.sigrc.users.application.mappers.UsersMapper;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.DomainException;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.IProfileUseCase;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IAuthServiceClient;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IImageStoragePort;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IUsersRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProfileUseCaseImpl implements IProfileUseCase {

    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    private final IUsersRepository repository;
    private final IImageStoragePort imageStoragePort;
    private final IAuthServiceClient authServiceClient;

    @Override
    public Mono<UsersResponse> getMyProfile(String username) {
        return repository.findByUsername(username)
                .switchIfEmpty(Mono.error(new DomainException("USER_NOT_FOUND", "Usuario no encontrado en la base de datos.")))
                .map(UsersMapper::toResponse);
    }

    @Override
    public Mono<UsersResponse> updateMyProfile(String username, UpdateUsersRequest request) {
        return repository.findByUsername(username)
                .switchIfEmpty(Mono.error(new DomainException("USER_NOT_FOUND", "Usuario no encontrado.")))
                .flatMap(user -> {
                    String currentUsername = user.getUsername();
                    boolean passwordChanged = request.getPassword() != null && !request.getPassword().isBlank();

                    user.setFirstName(request.getFirstName());
                    user.setLastName(request.getLastName());
                    user.setPhone(request.getPhone());
                    user.setEmail(request.getEmail());
                    user.setDocumentType(request.getDocumentType());
                    user.setDocumentNumber(request.getDocumentNumber());
                    if (request.getUsername() != null && !request.getUsername().isBlank()) {
                        user.setUsername(request.getUsername());
                    }
                    if (passwordChanged) {
                        user.setPassword(PASSWORD_ENCODER.encode(request.getPassword()));
                    }

                    String documentError = validateDocumentNumber(user.getDocumentType(), user.getDocumentNumber());
                    if (documentError != null) {
                        return Mono.error(new DomainException("INVALID_DOCUMENT_NUMBER", documentError));
                    }

                    user.setUpdatedAt(LocalDateTime.now());
                    
                    return repository.save(user)
                            .flatMap(saved -> authServiceClient.updateUser(
                                            currentUsername, saved.getUsername(), saved.getEmail(),
                                            saved.getFirstName(), saved.getLastName(), saved.getRole())
                                    .then(passwordChanged
                                            ? authServiceClient.resetPassword(saved.getUsername(), request.getPassword())
                                            : Mono.empty())
                                    .thenReturn(saved));
                })
                .map(UsersMapper::toResponse);
    }

    private static final String DNI_PATTERN = "\\d{8}";
    private static final String CNE_PATTERN = "\\d{20}";

    private String validateDocumentNumber(String documentType, String documentNumber) {
        if (documentType == null || documentNumber == null || documentType.isBlank() || documentNumber.isBlank()) {
            return null;
        }

        return switch (documentType) {
            case "DNI" -> documentNumber.matches(DNI_PATTERN) ? null : "El DNI debe tener 8 dígitos";
            case "CNE" -> documentNumber.matches(CNE_PATTERN) ? null : "El CNE debe tener 20 dígitos";
            default -> null;
        };
    }

    @Override
    public Mono<UsersResponse> updateMyAvatar(String username, FilePart filePart) {
        return repository.findByUsername(username)
                .switchIfEmpty(Mono.error(new DomainException("USER_NOT_FOUND", "Usuario no encontrado.")))
                .flatMap(user -> imageStoragePort.uploadImage(filePart, user.getUserId())
                        .flatMap(imageUrl -> {
                            user.setProfileImagePath(imageUrl);
                            user.setUpdatedAt(LocalDateTime.now());
                            return repository.save(user);
                        }))
                .map(UsersMapper::toResponse);
    }

    @Override
    public Mono<UsersResponse> deleteMyAvatar(String username) {
        return repository.findByUsername(username)
                .switchIfEmpty(Mono.error(new DomainException("USER_NOT_FOUND", "Usuario no encontrado.")))
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
}
