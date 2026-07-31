package pe.edu.vallegrande.sigrc.users.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.sigrc.users.application.usecase.*;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.*;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IKeycloakAdminService;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IUsersRepository;

@Configuration
@EnableConfigurationProperties(KeycloakProperties.class)
public class BeanConfiguration {
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public ICreateUsersUseCase createUsersUseCase(
            IUsersRepository repository,
            IKeycloakAdminService keycloakAdminService) {
        return new CreateUsersUseCaseImpl(repository, keycloakAdminService);
    }

    @Bean
    public IGetUsersUseCase getUsersUseCase(IUsersRepository repository) {
        return new GetUsersUseCaseImpl(repository);
    }

    @Bean
    public IUpdateUsersUseCase updateUsersUseCase(
            IUsersRepository repository,
            IKeycloakAdminService keycloakAdminService) {
        return new UpdateUsersUseCaseImpl(repository, keycloakAdminService);
    }

    @Bean
    public IDeactivateUsersUseCase deactivateUsersUseCase(
            IUsersRepository repository,
            IKeycloakAdminService keycloakAdminService) {
        return new DeactivateUsersUseCaseImpl(repository, keycloakAdminService);
    }

    @Bean
    public IRestoreUsersUseCase restoreUsersUseCase(
            IUsersRepository repository,
            IKeycloakAdminService keycloakAdminService) {
        return new RestoreUsersUseCaseImpl(repository, keycloakAdminService);
    }

}
