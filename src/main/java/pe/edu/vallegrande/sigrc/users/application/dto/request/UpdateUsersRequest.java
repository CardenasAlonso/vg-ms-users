package pe.edu.vallegrande.sigrc.users.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUsersRequest {
    private String firstName;
    private String lastName;
    private String phone;

    @Email(message = "Formato de email inválido")
    private String email;

    @Pattern(regexp = "DNI|CNE", message = "Tipo de documento inválido")
    private String documentType;

    private String documentNumber;

    private String profileImagePath;
    private String firebaseId;

    @Size(min = 6, message = "La contraseña debe tener mínimo 6 caracteres")
    private String password;
}
