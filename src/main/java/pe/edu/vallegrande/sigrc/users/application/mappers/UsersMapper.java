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
                .firebaseId(users.getFirebaseId())
                .firstName(users.getFirstName())
                .lastName(users.getLastName())
                .documentType(users.getDocumentType())
                .documentNumber(users.getDocumentNumber())
                .phone(users.getPhone())
                .email(users.getEmail())
                .username(users.getUsername())
                .roles(users.getRoles())
                .profileImagePath(users.getProfileImagePath())
                .lastLogin(users.getLastLogin())
                .status(users.getStatus())
                .createdAt(users.getCreatedAt())
                .updatedAt(users.getUpdatedAt())
                .build();
    }

    // Document → Domain
    public static Users toDomain(UsersDocument doc) {
        return Users.builder()
                .userId(doc.getId())
                .firebaseId(doc.getFirebaseId())
                .firstName(doc.getFirstName())
                .lastName(doc.getLastName())
                .documentType(doc.getDocumentType())
                .documentNumber(doc.getDocumentNumber())
                .phone(doc.getPhone())
                .email(doc.getEmail())
                .username(doc.getUsername())
                .password(doc.getPassword())
                .profileImagePath(doc.getProfileImagePath())
                .lastLogin(doc.getLastLogin())
                .status(doc.getStatus())
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }

    // Domain → Document
    public static UsersDocument toDocument(Users users) {
        return UsersDocument.builder()
                .id(users.getUserId())
                .firebaseId(users.getFirebaseId())
                .firstName(users.getFirstName())
                .lastName(users.getLastName())
                .documentType(users.getDocumentType())
                .documentNumber(users.getDocumentNumber())
                .phone(users.getPhone())
                .email(users.getEmail())
                .username(users.getUsername())
                .password(users.getPassword())
                .profileImagePath(users.getProfileImagePath())
                .lastLogin(users.getLastLogin())
                .status(users.getStatus())
                .createdAt(users.getCreatedAt())
                .updatedAt(users.getUpdatedAt())
                .build();
    }
}
