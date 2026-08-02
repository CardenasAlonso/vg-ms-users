package pe.edu.vallegrande.sigrc.users.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pe.edu.vallegrande.sigrc.users.application.usecase.*;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.*;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IAuthServiceClient;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IUsersRepository;

@Configuration
public class BeanConfiguration {
    @Bean
    public ICreateUsersUseCase createUsersUseCase(IUsersRepository repository,
                                                  IAuthServiceClient authServiceClient) {
        return new CreateUsersUseCaseImpl(repository, authServiceClient);
    }

    @Bean
    public IGetUsersUseCase getUsersUseCase(IUsersRepository repository) {
        return new GetUsersUseCaseImpl(repository);
    }

    @Bean
    public IUpdateUsersUseCase updateUsersUseCase(IUsersRepository repository,
                                                  IAuthServiceClient authServiceClient) {
        return new UpdateUsersUseCaseImpl(repository, authServiceClient);
    }

    @Bean
    public IDeactivateUsersUseCase deactivateUsersUseCase(IUsersRepository repository,
                                                          IAuthServiceClient authServiceClient) {
        return new DeactivateUsersUseCaseImpl(repository, authServiceClient);
    }

    @Bean
    public IRestoreUsersUseCase restoreUsersUseCase(IUsersRepository repository,
                                                    IAuthServiceClient authServiceClient) {
        return new RestoreUsersUseCaseImpl(repository, authServiceClient);
    }

}
