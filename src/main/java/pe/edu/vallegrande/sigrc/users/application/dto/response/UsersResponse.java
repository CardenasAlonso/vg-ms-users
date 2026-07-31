package pe.edu.vallegrande.sigrc.users.application.dto.response;

import lombok.Builder;
import lombok.Data;
import pe.edu.vallegrande.sigrc.users.domain.model.UserRole;

import java.time.LocalDateTime;

@Data
@Builder
public class UsersResponse {
    private String userId;
    private String keycloakId;
    private String firstName;
    private String lastName;
    private String documentType;
    private String documentNumber;
    private String phone;
    private String email;
    private String username;
    private UserRole role;
    private String profileImagePath;
    private LocalDateTime lastLogin;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
