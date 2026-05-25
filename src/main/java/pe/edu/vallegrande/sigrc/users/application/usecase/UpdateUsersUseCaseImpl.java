package pe.edu.vallegrande.sigrc.users.application.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.edu.vallegrande.sigrc.users.application.dto.request.UpdateUsersRequest;
import pe.edu.vallegrande.sigrc.users.application.dto.response.UsersResponse;
import pe.edu.vallegrande.sigrc.users.application.mappers.UsersMapper;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.DomainException;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.NotFoundException;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.IUpdateUsersUseCase;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IUsersRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class UpdateUsersUseCaseImpl implements IUpdateUsersUseCase {
    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    private static final String DNI_PATTERN = "\\d{8}";
    private static final String CNE_PATTERN = "\\d{20}";

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
                    if (request.getProfileImagePath() != null) users.setProfileImagePath(request.getProfileImagePath());
                    if (request.getFirebaseId() != null) users.setFirebaseId(request.getFirebaseId());
                    if (request.getPassword() != null) users.setPassword(PASSWORD_ENCODER.encode(request.getPassword()));

                    String documentError = validateDocumentNumber(users.getDocumentType(), users.getDocumentNumber());
                    if (documentError != null) {
                        return Mono.error(new DomainException("INVALID_DOCUMENT_NUMBER", documentError));
                    }

                    users.setUpdatedAt(LocalDateTime.now());
                    return repository.save(users);
                })
                .map(UsersMapper::toResponse);
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
