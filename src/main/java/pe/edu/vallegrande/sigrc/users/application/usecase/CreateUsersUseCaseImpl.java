package pe.edu.vallegrande.sigrc.users.application.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    private static final String DNI_PATTERN = "\\d{8}";
    private static final String CNE_PATTERN = "\\d{20}";

    private final IUsersRepository repository;

    @Override
    public Mono<UsersResponse> create(CreateUsersRequest request) {
        String documentError = validateDocumentNumber(request.getDocumentType(), request.getDocumentNumber());
        if (documentError != null) {
            return Mono.error(new DomainException("INVALID_DOCUMENT_NUMBER", documentError));
        }

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
                            .password(PASSWORD_ENCODER.encode(request.getPassword()))
                            .profileImagePath(request.getProfileImagePath())
                            .status("ACTIVE")
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
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
