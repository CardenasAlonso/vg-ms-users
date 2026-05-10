package pe.edu.vallegrande.sigrc.users.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pe.edu.vallegrande.sigrc.users.application.usecase.*;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.*;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IUsersRepository;

@Configuration
public class BeanConfiguration {
    @Bean
    public ICreateUsersUseCase createUsersUseCase(IUsersRepository repository) {
        return new CreateUsersUseCaseImpl(repository);
    }

    @Bean
    public IGetUsersUseCase getUsersUseCase(IUsersRepository repository) {
        return new GetUsersUseCaseImpl(repository);
    }

    @Bean
    public IUpdateUsersUseCase updateUsersUseCase(IUsersRepository repository) {
        return new UpdateUsersUseCaseImpl(repository);
    }

    @Bean
    public IDeactivateUsersUseCase deactivateUsersUseCase(IUsersRepository repository) {
        return new DeactivateUsersUseCaseImpl(repository);
    }

    @Bean
    public IRestoreUsersUseCase restoreUsersUseCase(IUsersRepository repository) {
        return new RestoreUsersUseCaseImpl(repository);
    }
}
