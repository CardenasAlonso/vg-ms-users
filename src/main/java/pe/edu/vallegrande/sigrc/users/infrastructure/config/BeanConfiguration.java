package pe.edu.vallegrande.sigrc.users.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pe.edu.vallegrande.sigrc.users.application.usecase.*;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.*;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IRolesRepository;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IUserRolesRepository;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IUsersRepository;

@Configuration
public class BeanConfiguration {
    @Bean
    public ICreateUsersUseCase createUsersUseCase(IUsersRepository repository) {
        return new CreateUsersUseCaseImpl(repository);
    }

    @Bean
    public IGetUsersUseCase getUsersUseCase(
            IUsersRepository repository,
            IRolesRepository rolesRepository,
            IUserRolesRepository userRolesRepository) {
        return new GetUsersUseCaseImpl(repository, rolesRepository, userRolesRepository);
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

    @Bean
    public ICreateRolesUseCase createRolesUseCase(IRolesRepository repository) {
        return new CreateRolesUseCaseImpl(repository);
    }

    @Bean
    public IGetRolesUseCase getRolesUseCase(IRolesRepository repository) {
        return new GetRolesUseCaseImpl(repository);
    }

    @Bean
    public IUpdateRolesUseCase updateRolesUseCase(IRolesRepository repository) {
        return new UpdateRolesUseCaseImpl(repository);
    }

    @Bean
    public IDeactivateRolesUseCase deactivateRolesUseCase(IRolesRepository repository) {
        return new DeactivateRolesUseCaseImpl(repository);
    }

    @Bean
    public IRestoreRolesUseCase restoreRolesUseCase(IRolesRepository repository) {
        return new RestoreRolesUseCaseImpl(repository);
    }

    @Bean
    public IGetUserRolesUseCase getUserRolesUseCase(
            IUsersRepository usersRepository,
            IRolesRepository rolesRepository,
            IUserRolesRepository userRolesRepository) {
        return new GetUserRolesUseCaseImpl(usersRepository, rolesRepository, userRolesRepository);
    }

    @Bean
    public IAssignRoleUseCase assignRoleUseCase(
            IUsersRepository usersRepository,
            IRolesRepository rolesRepository,
            IUserRolesRepository userRolesRepository) {
        return new AssignRoleUseCaseImpl(usersRepository, rolesRepository, userRolesRepository);
    }

    @Bean
    public IRevokeRoleUseCase revokeRoleUseCase(
            IUsersRepository usersRepository,
            IRolesRepository rolesRepository,
            IUserRolesRepository userRolesRepository) {
        return new RevokeRoleUseCaseImpl(usersRepository, rolesRepository, userRolesRepository);
    }

    @Bean
    public IUpdateUserRoleUseCase updateUserRoleUseCase(
            IUsersRepository usersRepository,
            IRolesRepository rolesRepository,
            IUserRolesRepository userRolesRepository) {
        return new UpdateUserRoleUseCaseImpl(usersRepository, rolesRepository, userRolesRepository);
    }
}
