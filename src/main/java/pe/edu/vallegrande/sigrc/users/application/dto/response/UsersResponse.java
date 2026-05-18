package pe.edu.vallegrande.sigrc.users.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UsersResponse {
    private String userId;
    private String firebaseId;
    private String firstName;
    private String lastName;
    private String documentType;
    private String documentNumber;
    private String phone;
    private String email;
    private String username;
    private List<String> roles;
    private String profileImagePath;
    private LocalDateTime lastLogin;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
