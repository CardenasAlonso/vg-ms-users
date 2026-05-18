package pe.edu.vallegrande.sigrc.users.application.mappers;

import pe.edu.vallegrande.sigrc.users.application.dto.response.RolesResponse;
import pe.edu.vallegrande.sigrc.users.domain.model.Roles;
import pe.edu.vallegrande.sigrc.users.infrastructure.adapters.out.persistence.RolesDocument;

public class RolesMapper {
    private RolesMapper() {}

    public static RolesResponse toResponse(Roles roles) {
        return RolesResponse.builder()
                .roleId(roles.getRoleId())
                .name(roles.getName())
                .description(roles.getDescription())
                .status(roles.getStatus())
                .createdAt(roles.getCreatedAt())
                .updatedAt(roles.getUpdatedAt())
                .build();
    }

    public static Roles toDomain(RolesDocument doc) {
        return Roles.builder()
                .roleId(doc.getId())
                .name(doc.getName())
                .description(doc.getDescription())
                .status(doc.getStatus())
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }

    public static RolesDocument toDocument(Roles roles) {
        return RolesDocument.builder()
                .id(roles.getRoleId())
                .name(roles.getName())
                .description(roles.getDescription())
                .status(roles.getStatus())
                .createdAt(roles.getCreatedAt())
                .updatedAt(roles.getUpdatedAt())
                .build();
    }
}
