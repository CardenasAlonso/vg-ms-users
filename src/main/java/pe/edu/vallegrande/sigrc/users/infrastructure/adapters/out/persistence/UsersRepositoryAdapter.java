package pe.edu.vallegrande.sigrc.users.infrastructure.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.sigrc.users.application.mappers.UsersMapper;
import pe.edu.vallegrande.sigrc.users.domain.model.Users;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IUsersRepository;
import pe.edu.vallegrande.sigrc.users.infrastructure.security.AesCtrCipher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Component
@Profile("!demo")
@RequiredArgsConstructor
public class UsersRepositoryAdapter implements IUsersRepository {

    private final UsersMongoRepository mongoRepository;
    private final AesCtrCipher aesCtrCipher;

    @Override
    public Mono<Users> save(Users users) {
        String email = normalizeEmail(users.getEmail());
        UsersDocument document = UsersMapper.toDocument(users,
            "ENC:" + aesCtrCipher.encryptToBase64(email), emailHash(email));
        return mongoRepository.save(document)
            .map(this::toDomain);
    }

    @Override
    public Mono<Users> findById(String id) {
        return mongoRepository.findById(id)
            .map(this::toDomain);
    }

    @Override
    public Mono<Users> findByEmail(String email) {
        return mongoRepository.findByEmailHash(emailHash(email))
            .map(this::toDomain);
    }

    @Override
    public Mono<Users> findByUsername(String username) {
        return mongoRepository.findByUsername(username)
            .map(this::toDomain);
    }

    @Override
    public Flux<Users> findAll() {
        return mongoRepository.findAll()
            .map(this::toDomain);
    }

    @Override
    public Flux<Users> findByStatus(String status) {
        return mongoRepository.findByStatus(status)
            .map(this::toDomain);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return mongoRepository.existsByEmailHash(emailHash(email));
    }

    @Override
    public Mono<Boolean> existsByDocumentNumber(String documentNumber) {
        return mongoRepository.existsByDocumentNumber(documentNumber);
    }

    @Override
    public Mono<Void> deactivate(String id) {
        return mongoRepository.findById(id)
                .flatMap(doc -> {
                    doc.setStatus("INACTIVE");
                    doc.setUpdatedAt(LocalDateTime.now());
                    return mongoRepository.save(doc);
                })
                .then();
    }

    @Override
    public Mono<Void> restore(String id) {
        return mongoRepository.findById(id)
                .flatMap(doc -> {
                    doc.setStatus("ACTIVE");
                    doc.setUpdatedAt(LocalDateTime.now());
                    return mongoRepository.save(doc);
                })
                .then();
    }

    private Users toDomain(UsersDocument document) {
        String storedEmail = document.getEmail();
        String email = storedEmail != null && storedEmail.startsWith("ENC:")
                ? aesCtrCipher.decryptFromBase64(storedEmail.substring(4))
                : storedEmail;
        return UsersMapper.toDomain(document, email);
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private String emailHash(String email) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(normalizeEmail(email).getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder(digest.length * 2);
            for (byte value : digest) {
                hash.append(String.format("%02x", value));
            }
            return hash.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 no está disponible", exception);
        }
    }
}
