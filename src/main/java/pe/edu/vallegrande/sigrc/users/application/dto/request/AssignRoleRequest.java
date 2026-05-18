package pe.edu.vallegrande.sigrc.users.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssignRoleRequest {
    @NotBlank(message = "El usuario es obligatorio")
    private String userId;

    @NotBlank(message = "El rol es obligatorio")
    private String roleId;

    private String assignedBy;
}
