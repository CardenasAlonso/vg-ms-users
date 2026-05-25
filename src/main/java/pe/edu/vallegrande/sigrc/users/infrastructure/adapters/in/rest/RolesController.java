package pe.edu.vallegrande.sigrc.users.infrastructure.adapters.in.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.sigrc.users.application.dto.common.ApiResponse;
import pe.edu.vallegrande.sigrc.users.application.dto.request.CreateRolesRequest;
import pe.edu.vallegrande.sigrc.users.application.dto.response.RolesResponse;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roles")
@Tag(name = "Roles", description = "Operaciones para gestion de roles")
public class RolesController {
    private final ICreateRolesUseCase createUseCase;
    private final IGetRolesUseCase getUseCase;
    private final IUpdateRolesUseCase updateUseCase;
    private final IDeactivateRolesUseCase deactivateUseCase;
    private final IRestoreRolesUseCase restoreUseCase;

    @GetMapping
    public Mono<ApiResponse<List<RolesResponse>>> getAll() {
        return getUseCase.getAll()
                .collectList()
                .map(list -> ApiResponse.ok("Roles obtenidos", list));
    }

    @GetMapping("/{id}")
    public Mono<ApiResponse<RolesResponse>> getById(@PathVariable String id) {
        return getUseCase.getById(id)
                .map(role -> ApiResponse.ok("Rol encontrado", role));
    }

    @GetMapping("/status/{status}")
    public Mono<ApiResponse<List<RolesResponse>>> getByStatus(@PathVariable String status) {
        return getUseCase.getByStatus(status)
                .collectList()
                .map(list -> ApiResponse.ok("Roles filtrados por estado", list));
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<RolesResponse>> create(@Valid @RequestBody CreateRolesRequest request) {
        return createUseCase.create(request)
                .map(role -> ApiResponse.ok("Rol creado exitosamente", role));
    }

    @PutMapping("/update/{id}")
    public Mono<ApiResponse<RolesResponse>> update(
            @PathVariable String id,
            @Valid @RequestBody CreateRolesRequest request) {
        return updateUseCase.update(id, request)
                .map(role -> ApiResponse.ok("Rol actualizado exitosamente", role));
    }

    @PatchMapping("/deactivate/{id}")
    public Mono<ApiResponse<Void>> deactivate(@PathVariable String id) {
        return deactivateUseCase.deactivate(id)
                .then(Mono.just(ApiResponse.ok("Rol desactivado", null)));
    }

    @PatchMapping("/restore/{id}")
    public Mono<ApiResponse<Void>> restore(@PathVariable String id) {
        return restoreUseCase.restore(id)
                .then(Mono.just(ApiResponse.ok("Rol restaurado", null)));
    }
}
