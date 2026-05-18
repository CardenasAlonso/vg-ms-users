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
public class UserRoles {
    private String userRoleId;
    private String userId;
    private String roleId;
    private LocalDateTime assignedAt;
    private String assignedBy;
}
