package pe.edu.vallegrande.sigrc.users.infrastructure.adapters.out.persistence.h2;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsersJpaRepository extends JpaRepository<UsersJpaEntity, String> {
    Optional<UsersJpaEntity> findByEmailHash(String emailHash);
    Optional<UsersJpaEntity> findByUsername(String username);
    List<UsersJpaEntity> findByStatus(String status);
    boolean existsByEmailHash(String emailHash);
    boolean existsByDocumentNumber(String documentNumber);
}