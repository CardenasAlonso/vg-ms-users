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
                                Microservicio de gestion de usuarios con rol embebido.

                                **Usuarios**
                                - `GET    /api/v1/users`                    - Listar todos los usuarios
                                - `GET    /api/v1/users/{id}`               - Buscar usuario por ID
                                - `GET    /api/v1/users/status/{status}`    - Filtrar usuarios por estado
                                - `POST   /api/v1/users/create`             - Crear usuario
                                - `PUT    /api/v1/users/update/{id}`        - Actualizar usuario
                                - `PATCH  /api/v1/users/deactivate/{id}`    - Desactivar usuario
                                - `PATCH  /api/v1/users/restore/{id}`       - Restaurar usuario

                                **Roles permitidos**
                                - `Buyer`
                                - `Cashier`
                                - `admin`
                                - `doctor`
                                - `user`
                                """)
                        .contact(new Contact()
                                .name("Vallegrande SIGRC")
                                .email("sigrc@vallegrande.edu.pe")))
                .tags(List.of(
                        new Tag()
                                .name("Users")
                                .description("Operaciones para gestion de usuarios con rol embebido.")
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
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("all")
                .pathsToMatch("/api/v1/users/**")
                .build();
    }
}
