package pe.edu.vallegrande.sigrc.users.application.mappers;


import pe.edu.vallegrande.sigrc.users.application.dto.response.UsersResponse;
import pe.edu.vallegrande.sigrc.users.domain.model.Users;
import pe.edu.vallegrande.sigrc.users.infrastructure.adapters.out.persistence.UsersDocument;

public class UsersMapper {
    private UsersMapper() {}

    // Domain → Response
    public static UsersResponse toResponse(Users users) {
        return UsersResponse.builder()
                .userId(users.getUserId())
                .firstName(users.getFirstName())
                .lastName(users.getLastName())
                .documentType(users.getDocumentType())
                .documentNumber(users.getDocumentNumber())
                .phone(users.getPhone())
                .email(users.getEmail())
                .username(users.getUsername())
                .role(users.getRole())
                .profileImagePath(users.getProfileImagePath())
                .lastLogin(users.getLastLogin())
                .status(users.getStatus())
                .createdAt(users.getCreatedAt())
                .updatedAt(users.getUpdatedAt())
                .build();
    }

    // Document → Domain
    public static Users toDomain(UsersDocument doc) {
        return toDomain(doc, doc.getEmail());
    }

    public static Users toDomain(UsersDocument doc, String email) {
        return Users.builder()
                .userId(doc.getId())
                .firstName(doc.getFirstName())
                .lastName(doc.getLastName())
                .documentType(doc.getDocumentType())
                .documentNumber(doc.getDocumentNumber())
                .phone(doc.getPhone())
                .email(email)
                .username(doc.getUsername())
                .password(doc.getPassword())
                .profileImagePath(doc.getProfileImagePath())
                .role(doc.getRole())
                .lastLogin(doc.getLastLogin())
                .status(doc.getStatus())
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }

    // Domain → Document
    public static UsersDocument toDocument(Users users) {
        return toDocument(users, users.getEmail(), null);
    }

    public static UsersDocument toDocument(Users users, String email, String emailHash) {
        return UsersDocument.builder()
                .id(users.getUserId())
                .firstName(users.getFirstName())
                .lastName(users.getLastName())
                .documentType(users.getDocumentType())
                .documentNumber(users.getDocumentNumber())
                .phone(users.getPhone())
                .email(email)
                .emailHash(emailHash)
                .username(users.getUsername())
                .password(users.getPassword())
                .profileImagePath(users.getProfileImagePath())
                .role(users.getRole())
                .lastLogin(users.getLastLogin())
                .status(users.getStatus())
                .createdAt(users.getCreatedAt())
                .updatedAt(users.getUpdatedAt())
                .build();
    }
}
