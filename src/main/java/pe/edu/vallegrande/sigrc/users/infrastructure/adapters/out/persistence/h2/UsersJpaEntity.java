package pe.edu.vallegrande.sigrc.users.infrastructure.adapters.out.persistence.h2;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.vallegrande.sigrc.users.domain.model.UserRole;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersJpaEntity {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String documentType;
    private String documentNumber;
    private String phone;
    private String email;
    private String emailHash;
    private String username;
    private String password;
    private String profileImagePath;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    private LocalDateTime lastLogin;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}