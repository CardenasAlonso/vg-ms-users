package pe.edu.vallegrande.sigrc.users.infrastructure.adapters.out.persistence;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class UsersDocument {
    @Id
    private String id;

    private String firebaseId;
    private String firstName;
    private String lastName;
    private String documentType;
    private String documentNumber;
    private String phone;

    @Indexed(unique = true)
    private String email;

    @Indexed(unique = true)
    private String username;

    private String password;
    private String profileImagePath;
    private LocalDateTime lastLogin;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
