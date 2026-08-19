package pe.edu.vallegrande.sigrc.users.application.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.vallegrande.sigrc.users.application.dto.request.CreateUsersRequest;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.DomainException;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.NotFoundException;
import pe.edu.vallegrande.sigrc.users.domain.model.Users;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IAuthServiceClient;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IUsersRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UsersUseCasesTest {

    @Mock
    private IUsersRepository repository;

    @Mock
    private IAuthServiceClient authServiceClient;

    @InjectMocks
    private CreateUsersUseCaseImpl createUseCase;

    @InjectMocks
    private DeactivateUsersUseCaseImpl deactivateUseCase;

    @Test
    @DisplayName("CP01 - Debe fallar al crear usuario si el correo electrónico ya está registrado")
    void deberiaFallarCuandoEmailYaExiste() {
        CreateUsersRequest request = new CreateUsersRequest();
        request.setEmail("test@test.com");
        request.setDocumentType("DNI");
        request.setDocumentNumber("12345678");

        when(repository.existsByEmail("test@test.com")).thenReturn(Mono.just(true));

        StepVerifier.create(createUseCase.create(request))
                .expectErrorMatches(throwable -> throwable instanceof DomainException &&
                        throwable.getMessage().contains("email"))
                .verify();
    }

    @Test
    @DisplayName("CP02 - Debe fallar al crear usuario si el formato del DNI es menor a 8 dígitos")
    void deberiaFallarCuandoDocumentoEsDniInvalido() {
        CreateUsersRequest request = new CreateUsersRequest();
        request.setDocumentType("DNI");
        request.setDocumentNumber("123"); // Invalid length

        StepVerifier.create(createUseCase.create(request))
                .expectErrorMatches(throwable -> throwable instanceof DomainException &&
                        throwable.getMessage().contains("DNI"))
                .verify();
    }

    @Test
    @DisplayName("CP03 - Debe crear un usuario exitosamente, guardar en BD y sincronizar con Keycloak")
    void deberiaCrearUsuarioExitosamente() {
        CreateUsersRequest request = new CreateUsersRequest();
        request.setEmail("new@test.com");
        request.setDocumentType("DNI");
        request.setDocumentNumber("12345678");
        request.setPassword("password");
        request.setFirstName("John");
        request.setLastName("Doe");

        Users mockUser = Users.builder().userId("id-1").username("jdoe").email("new@test.com").build();

        when(repository.existsByEmail(anyString())).thenReturn(Mono.just(false));
        when(repository.existsByDocumentNumber(anyString())).thenReturn(Mono.just(false));
        when(repository.save(any(Users.class))).thenReturn(Mono.just(mockUser));
        when(authServiceClient.createUser(any(), any(), any(), any(), any(), any(), any())).thenReturn(Mono.empty());

        StepVerifier.create(createUseCase.create(request))
                .expectNextCount(1)
                .verifyComplete();
                
        verify(repository, times(1)).save(any(Users.class));
        verify(authServiceClient, times(1)).createUser(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("CP04 - Debe fallar la desactivación de usuario si el ID proporcionado no existe en BD")
    void deberiaFallarDesactivarUsuarioInexistente() {
        when(repository.findById("invalid-id")).thenReturn(Mono.empty());

        StepVerifier.create(deactivateUseCase.deactivate("invalid-id"))
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException)
                .verify();
    }
}
