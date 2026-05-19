package pe.edu.vallegrande.sigrc.users.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Users Microservice API")
                        .version("1.1.0")
                        .description("""
                                Microservicio de gestion de usuarios, roles y asignaciones de roles.

                                **Usuarios**
                                - `GET    /api/v1/users`                    - Listar todos los usuarios con sus roles
                                - `GET    /api/v1/users/{id}`               - Buscar usuario por ID con sus roles
                                - `GET    /api/v1/users/status/{status}`    - Filtrar usuarios por estado
                                - `POST   /api/v1/users/create`             - Crear usuario
                                - `PUT    /api/v1/users/update/{id}`        - Actualizar usuario
                                - `PATCH  /api/v1/users/deactivate/{id}`    - Desactivar usuario
                                - `PATCH  /api/v1/users/restore/{id}`       - Restaurar usuario

                                **Roles**
                                - `GET    /api/v1/roles`                    - Listar todos los roles
                                - `GET    /api/v1/roles/{id}`               - Buscar rol por ID
                                - `GET    /api/v1/roles/status/{status}`    - Filtrar roles por estado
                                - `POST   /api/v1/roles/create`             - Crear rol
                                - `PUT    /api/v1/roles/update/{id}`        - Actualizar rol
                                - `PATCH  /api/v1/roles/deactivate/{id}`    - Desactivar rol
                                - `PATCH  /api/v1/roles/restore/{id}`       - Restaurar rol

                                **Asignacion de roles**
                                - `GET    /api/v1/user-roles/unassigned-users`    - Usuarios disponibles para asignacion
                                - `GET    /api/v1/user-roles/user/{userId}`       - Rol asignado a un usuario
                                - `GET    /api/v1/user-roles/role/{roleId}`       - Usuarios asignados a un rol
                                - `POST   /api/v1/user-roles/assign`              - Asignar rol a usuario
                                - `PUT    /api/v1/user-roles/update`              - Cambiar rol asignado a un usuario
                                - `DELETE /api/v1/user-roles/revoke/{userId}/{roleId}` - Revocar rol de usuario
                                """)
                        .contact(new Contact()
                                .name("Vallegrande SIGRC")
                                .email("sigrc@vallegrande.edu.pe")))
                .tags(List.of(
                        new Tag()
                                .name("Users")
                                .description("Operaciones para gestion de usuarios sin rol embebido."),
                        new Tag()
                                .name("Roles")
                                .description("Operaciones para crear, consultar, actualizar, desactivar y restaurar roles."),
                        new Tag()
                                .name("User Roles")
                                .description("Operaciones para asignar, consultar y revocar roles de usuarios.")
                ));
    }

    @Bean
    public GroupedOpenApi usersApi() {
        return GroupedOpenApi.builder()
                .group("users")
                .pathsToMatch("/api/v1/users/**")
                .build();
    }

    @Bean
    public GroupedOpenApi rolesApi() {
        return GroupedOpenApi.builder()
                .group("roles")
                .pathsToMatch("/api/v1/roles/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userRolesApi() {
        return GroupedOpenApi.builder()
                .group("user-roles")
                .pathsToMatch("/api/v1/user-roles/**")
                .build();
    }

    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("all")
                .pathsToMatch("/api/v1/users/**", "/api/v1/roles/**", "/api/v1/user-roles/**")
                .build();
    }
}
