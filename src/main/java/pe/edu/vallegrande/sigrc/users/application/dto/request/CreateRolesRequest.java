package pe.edu.vallegrande.sigrc.users.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateRolesRequest {
    @NotBlank(message = "El nombre del rol es obligatorio")
    private String name;

    private String description;

    @Pattern(regexp = "ACTIVE|INACTIVE", message = "Estado inválido")
    private String status;
}
