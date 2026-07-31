package pe.edu.vallegrande.sigrc.users.domain.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    private String userId;
    private String keycloakId;
    private String firstName;
    private String lastName;
    private String documentType;   // DNI, CNE
    private String documentNumber;
    private String phone;
    private String email;
    private String username;
    private String password;
    private String profileImagePath;
    private List<String> roles;
    private LocalDateTime lastLogin;
    private String status;         // ACTIVE, INACTIVE
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
