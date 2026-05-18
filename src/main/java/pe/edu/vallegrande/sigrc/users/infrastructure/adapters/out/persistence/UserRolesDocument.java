package pe.edu.vallegrande.sigrc.users.infrastructure.adapters.out.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_roles")
@CompoundIndex(name = "user_role_unique", def = "{'userId': 1, 'roleId': 1}", unique = true)
public class UserRolesDocument {
    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;
    private String roleId;
    private LocalDateTime assignedAt;
    private String assignedBy;
}
