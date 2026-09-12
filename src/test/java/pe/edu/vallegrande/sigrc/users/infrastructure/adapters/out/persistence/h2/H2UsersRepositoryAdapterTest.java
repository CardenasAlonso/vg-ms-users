package pe.edu.vallegrande.sigrc.users.infrastructure.adapters.out.persistence.h2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pe.edu.vallegrande.sigrc.users.domain.model.UserRole;
import pe.edu.vallegrande.sigrc.users.domain.model.Users;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("demo")
class H2UsersRepositoryAdapterTest {

    @Autowired
    private H2UsersRepositoryAdapter usersRepository;

    @Autowired
    private UsersJpaRepository jpaRepository;

    @Test
    void guardaEmailCifradoYLoDevuelveDesencriptado() {
        Users user = Users.builder()
                .firstName("Test")
                .lastName("H2")
                .documentType("DNI")
                .documentNumber("11112222")
                .phone("999111222")
                .email("Test.Email@example.com")
                .username("test-h2")
                .password("$2a$10$hash")
                .role(UserRole.USER)
                .status("ACTIVE")
                .build();

        Users saved = usersRepository.save(user).block();

        System.out.println("===============================================================");
        System.out.println("Email devuelto: " + saved.getEmail());
        assertEquals("test.email@example.com", saved.getEmail());
        UsersJpaEntity stored = jpaRepository.findById(saved.getUserId()).orElseThrow();
        System.out.println("Email almacenado en H2: " + stored.getEmail());
        System.out.println("===============================================================");
        assertTrue(stored.getEmail().startsWith("ENC:"));
        assertTrue(stored.getEmailHash() != null && !stored.getEmailHash().isBlank());

        StepVerifier.create(usersRepository.findByEmail("TEST.EMAIL@EXAMPLE.COM"))
            .assertNext(found -> {
                System.out.println("===============================================================");
                System.out.println("Email encontrado por búsqueda (desencriptado): " + found.getEmail());
                System.out.println("===============================================================");
                assertEquals("test.email@example.com", found.getEmail());
            })
                .verifyComplete();
    }
}
