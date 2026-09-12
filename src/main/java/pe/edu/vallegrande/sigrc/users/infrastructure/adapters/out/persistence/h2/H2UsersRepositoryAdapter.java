package pe.edu.vallegrande.sigrc.users.infrastructure.adapters.out.persistence.h2;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.sigrc.users.domain.model.Users;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IUsersRepository;
import pe.edu.vallegrande.sigrc.users.infrastructure.security.AesCtrCipher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Profile("demo")
@RequiredArgsConstructor
public class H2UsersRepositoryAdapter implements IUsersRepository {
    private final UsersJpaRepository repository;
    private final AesCtrCipher aesCtrCipher;

    @Override
    public Mono<Users> save(Users users) {
        return Mono.fromCallable(() -> repository.save(toEntity(users)))
                .map(this::toDomain);
    }

    @Override
    public Mono<Users> findById(String id) {
        return Mono.fromCallable(() -> repository.findById(id).map(this::toDomain).orElse(null));
    }

    @Override
    public Mono<Users> findByEmail(String email) {
        return Mono.fromCallable(() -> repository.findByEmailHash(emailHash(email))
                .map(this::toDomain).orElse(null));
    }

    @Override
    public Mono<Users> findByUsername(String username) {
        return Mono.fromCallable(() -> repository.findByUsername(username)
                .map(this::toDomain).orElse(null));
    }

    @Override
    public Flux<Users> findAll() {
        return Mono.fromCallable(repository::findAll).flatMapMany(Flux::fromIterable).map(this::toDomain);
    }

    @Override
    public Flux<Users> findByStatus(String status) {
        return Mono.fromCallable(() -> repository.findByStatus(status))
                .flatMapMany(Flux::fromIterable).map(this::toDomain);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return Mono.fromCallable(() -> repository.existsByEmailHash(emailHash(email)));
    }

    @Override
    public Mono<Boolean> existsByDocumentNumber(String documentNumber) {
        return Mono.fromCallable(() -> repository.existsByDocumentNumber(documentNumber));
    }

    @Override
    public Mono<Void> deactivate(String id) {
        return updateStatus(id, "INACTIVE");
    }

    @Override
    public Mono<Void> restore(String id) {
        return updateStatus(id, "ACTIVE");
    }

    private Mono<Void> updateStatus(String id, String status) {
        return Mono.fromRunnable(() -> repository.findById(id).ifPresent(user -> {
            user.setStatus(status);
            user.setUpdatedAt(LocalDateTime.now());
            repository.save(user);
        })).then();
    }

    private UsersJpaEntity toEntity(Users users) {
        String email = normalizeEmail(users.getEmail());
        return UsersJpaEntity.builder().id(users.getUserId() != null ? users.getUserId() : UUID.randomUUID().toString()).firstName(users.getFirstName())
                .lastName(users.getLastName()).documentType(users.getDocumentType())
                .documentNumber(users.getDocumentNumber()).phone(users.getPhone())
                .email("ENC:" + aesCtrCipher.encryptToBase64(email)).emailHash(emailHash(email))
                .username(users.getUsername()).password(users.getPassword())
                .profileImagePath(users.getProfileImagePath()).role(users.getRole())
                .lastLogin(users.getLastLogin()).status(users.getStatus())
                .createdAt(users.getCreatedAt()).updatedAt(users.getUpdatedAt()).build();
    }

    private Users toDomain(UsersJpaEntity entity) {
        return Users.builder().userId(entity.getId()).firstName(entity.getFirstName())
                .lastName(entity.getLastName()).documentType(entity.getDocumentType())
                .documentNumber(entity.getDocumentNumber()).phone(entity.getPhone())
                .email(aesCtrCipher.decryptFromBase64(entity.getEmail().substring(4)))
                .username(entity.getUsername()).password(entity.getPassword())
                .profileImagePath(entity.getProfileImagePath()).role(entity.getRole())
                .lastLogin(entity.getLastLogin()).status(entity.getStatus())
                .createdAt(entity.getCreatedAt()).updatedAt(entity.getUpdatedAt()).build();
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private String emailHash(String email) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(normalizeEmail(email).getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder(digest.length * 2);
            for (byte value : digest) hash.append(String.format("%02x", value));
            return hash.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 no está disponible", exception);
        }
    }
}