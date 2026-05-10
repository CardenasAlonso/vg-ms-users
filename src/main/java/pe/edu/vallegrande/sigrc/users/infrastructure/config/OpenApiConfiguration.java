package pe.edu.vallegrande.sigrc.users.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Users Microservice API")
                        .version("1.0.0")
                        .description("""
                                Microservicio de gestión de usuarios.
                                
                                **Endpoints disponibles:**
                                - `GET    /api/v1/users`                    → Listar todos
                                - `GET    /api/v1/users/{id}`               → Buscar por ID
                                - `GET    /api/v1/users/status/{status}`    → Filtrar por estado
                                - `POST   /api/v1/users/create`             → Crear usuario
                                - `PUT    /api/v1/users/update/{id}`        → Actualizar usuario
                                - `PATCH  /api/v1/users/deactivate/{id}`    → Desactivar usuario
                                - `PATCH  /api/v1/users/restore/{id}`       → Restaurar usuario
                                """)
                        .contact(new Contact()
                                .name("Vallegrande SIGRC")
                                .email("sigrc@vallegrande.edu.pe")));
    }

    @Bean
    public GroupedOpenApi usersApi() {
        return GroupedOpenApi.builder()
                .group("users")
                .pathsToMatch("/api/v1/users/**")
                .build();
    }
}
