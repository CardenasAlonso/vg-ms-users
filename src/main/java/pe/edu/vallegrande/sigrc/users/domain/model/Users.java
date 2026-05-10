package pe.edu.vallegrande.sigrc.users.domain.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    private String userId;
    private String firebaseId;
    private String firstName;
    private String lastName;
    private String documentType;   // DNI, CE, PASAPORTE
    private String documentNumber;
    private String phone;
    private String email;
    private String username;
    private String password;
    private String role;           // COORDINADOR, ADMIN, VOLUNTARIO, etc.
    private String profileImagePath;
    private LocalDateTime lastLogin;
    private String status;         // ACTIVE, INACTIVE
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
