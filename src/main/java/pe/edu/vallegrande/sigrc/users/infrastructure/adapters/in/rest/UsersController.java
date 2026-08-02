package pe.edu.vallegrande.sigrc.users.infrastructure.adapters.in.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.sigrc.users.application.dto.common.ApiResponse;
import pe.edu.vallegrande.sigrc.users.application.dto.request.CreateUsersRequest;
import pe.edu.vallegrande.sigrc.users.application.dto.request.UpdateUsersRequest;
import pe.edu.vallegrande.sigrc.users.application.dto.response.UsersResponse;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.ICreateUsersUseCase;

import pe.edu.vallegrande.sigrc.users.domain.ports.in.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Operaciones para gestion de usuarios")
public class UsersController {
    private final ICreateUsersUseCase createUseCase;
    private final IGetUsersUseCase getUseCase;
    private final IUpdateUsersUseCase updateUseCase;
    private final IDeactivateUsersUseCase deactivateUseCase;
    private final IRestoreUsersUseCase restoreUseCase;

    // GET /api/v1/users
    @GetMapping
    public Mono<ApiResponse<List<UsersResponse>>> getAll() {
        return getUseCase.getAll()
                .collectList()
                .map(list -> ApiResponse.ok("Usuarios obtenidos", list));
    }

    // GET /api/v1/users/{id}
    @GetMapping("/{id}")
    public Mono<ApiResponse<UsersResponse>> getById(@PathVariable String id) {
        return getUseCase.getById(id)
                .map(user -> ApiResponse.ok("Usuario encontrado", user));
    }

    // GET /api/v1/users/status/{status}
    @GetMapping("/status/{status}")
    public Mono<ApiResponse<List<UsersResponse>>> getByStatus(@PathVariable String status) {
        return getUseCase.getByStatus(status)
                .collectList()
                .map(list -> ApiResponse.ok("Usuarios filtrados por estado", list));
    }

    // POST /api/v1/users/create
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<UsersResponse>> create(@Valid @RequestBody CreateUsersRequest request) {
        return createUseCase.create(request)
                .map(user -> ApiResponse.ok("Usuario creado exitosamente", user));
    }

    // PUT /api/v1/users/update/{id}
    @PutMapping("/update/{id}")
    public Mono<ApiResponse<UsersResponse>> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateUsersRequest request) {
        return updateUseCase.update(id, request)
                .map(user -> ApiResponse.ok("Usuario actualizado exitosamente", user));
    }

    // PATCH /api/v1/users/deactivate/{id}
    @PatchMapping("/deactivate/{id}")
    public Mono<ApiResponse<Void>> deactivate(@PathVariable String id) {
        return deactivateUseCase.deactivate(id)
                .then(Mono.just(ApiResponse.ok("Usuario desactivado", null)));
    }

    // PATCH /api/v1/users/restore/{id}
    @PatchMapping("/restore/{id}")
    public Mono<ApiResponse<Void>> restore(@PathVariable String id) {
        return restoreUseCase.restore(id)
                .then(Mono.just(ApiResponse.ok("Usuario restaurado", null)));
    }
}
