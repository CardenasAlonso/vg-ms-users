package pe.edu.vallegrande.sigrc.users.application.mappers;

import pe.edu.vallegrande.sigrc.users.application.dto.response.UserWithRolesResponse;
import pe.edu.vallegrande.sigrc.users.domain.model.UserRoles;
import pe.edu.vallegrande.sigrc.users.domain.model.Users;
import pe.edu.vallegrande.sigrc.users.infrastructure.adapters.out.persistence.UserRolesDocument;

import java.util.List;

public class UserRolesMapper {
    private UserRolesMapper() {}

    public static UserRoles toDomain(UserRolesDocument doc) {
        return UserRoles.builder()
                .userRoleId(doc.getId())
                .userId(doc.getUserId())
                .roleId(doc.getRoleId())
                .assignedAt(doc.getAssignedAt())
                .assignedBy(doc.getAssignedBy())
                .build();
    }

    public static UserRolesDocument toDocument(UserRoles userRoles) {
        return UserRolesDocument.builder()
                .id(userRoles.getUserRoleId())
                .userId(userRoles.getUserId())
                .roleId(userRoles.getRoleId())
                .assignedAt(userRoles.getAssignedAt())
                .assignedBy(userRoles.getAssignedBy())
                .build();
    }

    public static UserWithRolesResponse toUserWithRolesResponse(Users users, List<String> roles) {
        return UserWithRolesResponse.builder()
                .userId(users.getUserId())
                .firebaseId(users.getFirebaseId())
                .firstName(users.getFirstName())
                .lastName(users.getLastName())
                .documentType(users.getDocumentType())
                .documentNumber(users.getDocumentNumber())
                .phone(users.getPhone())
                .email(users.getEmail())
                .username(users.getUsername())
                .profileImagePath(users.getProfileImagePath())
                .lastLogin(users.getLastLogin())
                .status(users.getStatus())
                .createdAt(users.getCreatedAt())
                .updatedAt(users.getUpdatedAt())
                .roles(roles)
                .build();
    }
}
